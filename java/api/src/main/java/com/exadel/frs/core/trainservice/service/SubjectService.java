package com.exadel.frs.core.trainservice.service;

import static java.math.RoundingMode.HALF_UP;
import com.exadel.frs.commonservice.entity.Embedding;
import com.exadel.frs.commonservice.entity.Subject;
import com.exadel.frs.commonservice.exception.EmbeddingNotFoundException;
import com.exadel.frs.commonservice.exception.TooManyFacesException;
import com.exadel.frs.commonservice.exception.WrongEmbeddingCountException;
import com.exadel.frs.commonservice.sdk.faces.FacesApiClient;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.FindFacesResponse;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.FindFacesResult;
import com.exadel.frs.commonservice.sdk.faces.feign.dto.PluginsVersions;
import com.exadel.frs.core.trainservice.cache.EmbeddingCacheProvider;
import com.exadel.frs.core.trainservice.component.FaceClassifierPredictor;
import com.exadel.frs.core.trainservice.component.classifiers.EuclideanDistanceClassifier;
import com.exadel.frs.core.trainservice.dao.SubjectDao;
import com.exadel.frs.core.trainservice.dto.EmbeddingInfo;
import com.exadel.frs.core.trainservice.dto.EmbeddingVerificationProcessResult;
import com.exadel.frs.core.trainservice.dto.EmbeddingsVerificationProcessResponse;
import com.exadel.frs.core.trainservice.dto.FaceVerification;
import com.exadel.frs.core.trainservice.dto.ProcessEmbeddingsParams;
import com.exadel.frs.core.trainservice.dto.ProcessImageParams;
import com.exadel.frs.core.trainservice.system.global.Constants;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectService {

    private static final int MINIMUM_EMBEDDING_COUNT = 1;
    private static final int MAX_FACES_TO_SAVE = 1;
    public static final int MAX_FACES_TO_RECOGNIZE = 2;

    private final SubjectDao subjectDao;
    private final FacesApiClient facesApiClient;
    private final EmbeddingCacheProvider embeddingCacheProvider;
    private final FaceClassifierPredictor predictor;
    private final EuclideanDistanceClassifier classifier;

    public Collection<String> getSubjectsNames(final String apiKey) {
        return subjectDao.getSubjectNames(apiKey);
    }

    public Page<String> getSubjectsNames(final String apiKey, final String search, final Pageable pageable) {
        return subjectDao.getSubjectNames(apiKey, search, pageable);
    }

    public long countSubjects(final String apiKey, final String search) {
        return subjectDao.countSubjects(apiKey, search);
    }

    public Subject createSubject(final String apiKey, final String subjectName) {
        // subject is empty (without embeddings) no need to update cache
        return subjectDao.createSubject(apiKey, subjectName);
    }

    public int deleteSubjectsByApiKey(final String apiKey) {
        int deletedCount = subjectDao.deleteSubjectsByApiKey(apiKey);
        // we need invalidate cache
        embeddingCacheProvider.invalidate(apiKey);

        return deletedCount;
    }

    public int removeAllSubjectEmbeddings(final String apiKey, final String subjectName) {
        int removed;
        if (StringUtils.isNotEmpty(subjectName)) {
            removed = subjectDao.removeAllSubjectEmbeddings(apiKey, subjectName);
            if (removed > 0) {
                embeddingCacheProvider.ifPresent(
                        apiKey,
                        c -> c.removeEmbeddingsBySubjectName(subjectName)
                );
            }
        } else {
            removed = subjectDao.removeAllSubjectEmbeddings(apiKey);
            embeddingCacheProvider.invalidate(apiKey);
        }

        return removed;
    }

    public Pair<Integer, Subject> deleteSubjectByName(final String apiKey, final String subjectName) {
        if (StringUtils.isBlank(subjectName)) {
            return Pair.of(deleteSubjectsByApiKey(apiKey), null);
        } else {
            return Pair.of(null, deleteSubjectByNameAndApiKey(apiKey, subjectName));
        }
    }

    public Subject deleteSubjectByNameAndApiKey(final String apiKey, final String subjectName) {
        var subject = subjectDao.deleteSubjectByName(apiKey, subjectName);

        // remove subject from cache if required
        embeddingCacheProvider.ifPresent(
                apiKey,
                c -> c.removeEmbeddingsBySubjectName(subjectName)
        );

        return subject;
    }

    public Embedding removeSubjectEmbedding(final String apiKey, final UUID embeddingId) {
        var embedding = subjectDao.removeSubjectEmbedding(apiKey, embeddingId);

        // remove embedding from cache if required
        embeddingCacheProvider.ifPresent(
                apiKey,
                c -> c.removeEmbedding(embedding)
        );

        return embedding;
    }
    public List<Embedding> removeSubjectEmbeddings(final String apiKey, final List<UUID> embeddingIds) {
        var embeddings = embeddingIds.stream()
                .map(id -> removeSubjectEmbedding(apiKey, id))
                .toList();

        return embeddings;
    }

    public boolean updateSubjectName(final String apiKey, final String oldSubjectName, final String newSubjectName) {
        var updated = subjectDao.updateSubjectName(apiKey, oldSubjectName, newSubjectName);

        // update cache if required
        embeddingCacheProvider.ifPresent(
                apiKey,
                c -> c.updateSubjectName(oldSubjectName, newSubjectName)
        );

        return updated;
    }

    public Pair<Subject, Embedding> addEmbedding(final String apiKey,
                                                 final String subjectName,
                                                 final byte[] content) throws IOException {
        FindFacesResponse findFacesResponse = facesApiClient.findFacesBase64WithCalculator(
                Base64.getEncoder().encodeToString(content),
                1,
                0.0,
                null,
                true
        );

        if (findFacesResponse == null || findFacesResponse.getResult().isEmpty()) {
            // no faces found
            return Pair.of(subjectDao.createSubject(apiKey, subjectName), null);
        }

        // we are here => at least one face exists
        List<FindFacesResult> result = findFacesResponse.getResult();

        if (result.size() > MAX_FACES_TO_SAVE) {
            throw new TooManyFacesException();
        }

        Double[] embedding = result.stream().findFirst().orElseThrow().getEmbedding();
        double[] normalized = classifier.normalizeOne(Arrays.stream(embedding).mapToDouble(d -> d).toArray());

        var embeddingToSave = new EmbeddingInfo(
                findFacesResponse.getPluginsVersions().getCalculator(),
                normalized,
                content
        );

        final Pair<Subject, Embedding> pair = subjectDao.addEmbedding(apiKey, subjectName, embeddingToSave);

        embeddingCacheProvider.ifPresent(
                apiKey,
                subjectCollection -> subjectCollection.addEmbedding(pair.getRight())
        );

        return pair;
    }

    public Pair<List<FaceVerification>, PluginsVersions> verifyFace(ProcessImageParams processImageParams) {
        FindFacesResponse findFacesResponse;
        if (processImageParams.getFile() != null) {
            MultipartFile file = (MultipartFile) processImageParams.getFile();
            findFacesResponse = facesApiClient.findFacesWithCalculator(file, processImageParams.getLimit(),
                    processImageParams.getDetProbThreshold(), processImageParams.getFacePlugins(), true);
        } else {
            findFacesResponse = facesApiClient.findFacesBase64WithCalculator(processImageParams.getImageBase64(),
                    processImageParams.getLimit(), processImageParams.getDetProbThreshold(), processImageParams.getFacePlugins(), true);
        }

        if (findFacesResponse == null) {
            return Pair.of(Collections.emptyList(), null);
        }

        var embeddingId = (UUID) processImageParams.getAdditionalParams().get(Constants.IMAGE_ID);

        final String subjectName = embeddingCacheProvider
                .getOrLoad(processImageParams.getApiKey()) // do we really need to load cache here?
                .getSubjectNameByEmbeddingId(embeddingId)
                .orElse("");

        var results = new ArrayList<FaceVerification>();
        for (var findResult : findFacesResponse.getResult()) {
            var prediction = predictor.verify(
                    processImageParams.getApiKey(),
                    Stream.of(findResult.getEmbedding()).mapToDouble(d -> d).toArray(),
                    embeddingId
            );

            var inBoxProb = BigDecimal.valueOf(findResult.getBox().getProbability());
            inBoxProb = inBoxProb.setScale(5, HALF_UP);
            findResult.getBox().setProbability(inBoxProb.doubleValue());

            var pred = BigDecimal.valueOf(prediction);
            pred = pred.setScale(5, HALF_UP);

            var faceVerification = FaceVerification
                    .builder()
                    .box(findResult.getBox())
                    .subject(subjectName)
                    .similarity(pred.floatValue())
                    .landmarks(findResult.getLandmarks())
                    .gender(findResult.getGender())
                    .embedding(findResult.getEmbedding())
                    .executionTime(findResult.getExecutionTime())
                    .age(findResult.getAge())
                    .pose(findResult.getPose())
                    .mask(findResult.getMask())
                    .build()
                    .prepareResponse(processImageParams); // do some tricks with obj

            results.add(faceVerification);
        }

        return Pair.of(
                results,
                Boolean.TRUE.equals(processImageParams.getStatus()) ? findFacesResponse.getPluginsVersions() : null
        );
    }

    public EmbeddingsVerificationProcessResponse verifyEmbedding(ProcessEmbeddingsParams processEmbeddingsParams) {
        double[][] targets = processEmbeddingsParams.getEmbeddings();
        if (ArrayUtils.isEmpty(targets)) {
            throw new WrongEmbeddingCountException(MINIMUM_EMBEDDING_COUNT, 0);
        }

        UUID sourceId = (UUID) processEmbeddingsParams.getAdditionalParams().get(Constants.IMAGE_ID);
        String apiKey = processEmbeddingsParams.getApiKey();

        List<EmbeddingVerificationProcessResult> results =
                Arrays.stream(targets)
                      .map(target -> processTarget(target, sourceId, apiKey))
                      .sorted((e1, e2) -> Float.compare(e2.getSimilarity(), e1.getSimilarity()))
                      .toList();

        return new EmbeddingsVerificationProcessResponse(results);
    }

    private EmbeddingVerificationProcessResult processTarget(double[] target, UUID sourceId, String apiKey) {
        double similarity = predictor.verify(apiKey, target, sourceId);
        float scaledSimilarity = BigDecimal.valueOf(similarity).setScale(5, HALF_UP).floatValue();
        return new EmbeddingVerificationProcessResult(target, scaledSimilarity);
    }
}
