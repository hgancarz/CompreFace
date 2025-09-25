package com.exadel.frs.commonservice.repository;

import com.exadel.frs.commonservice.entity.Embedding;
import com.exadel.frs.commonservice.projection.EnhancedEmbeddingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EmbeddingRepository extends JpaRepository<Embedding, UUID> {

    List<Embedding> findBySubjectApiKey(String apiKey);

    Page<Embedding> findBySubjectApiKey(String apiKey, Pageable pageable);

    Optional<Embedding> findBySubjectApiKeyAndId(String apiKey, UUID id);

    @Query("select e from Embedding e where e.subject.apiKey = :apiKey and e.subject.id = :subjectId")
    List<Embedding> findByApiKeyAndSubjectId(@Param("apiKey") String apiKey, @Param("subjectId") Long subjectId);

    @Query("select e from Embedding e where e.subject.apiKey = :apiKey and e.subject.name = :subjectName")
    List<Embedding> findByApiKeyAndSubjectName(@Param("apiKey") String apiKey, @Param("subjectName") String subjectName);

    @Query("select e from Embedding e where e.subject.apiKey = :apiKey and e.subject.name = :subjectName and e.id = :embeddingId")
    Optional<Embedding> findByApiKeyAndSubjectNameAndEmbeddingId(@Param("apiKey") String apiKey, @Param("subjectName") String subjectName, @Param("embeddingId") UUID embeddingId);

    @Query("select new com.exadel.frs.commonservice.projection.EnhancedEmbeddingProjection(e.id, e.subject.name, e.calculator, e.img.rawContent) from Embedding e where e.subject.apiKey = :apiKey")
    Page<EnhancedEmbeddingProjection> findEnhancedEmbeddingsByApiKey(@Param("apiKey") String apiKey, Pageable pageable);

    @Query("select new com.exadel.frs.commonservice.projection.EnhancedEmbeddingProjection(e.id, e.subject.name, e.calculator, e.img.rawContent) from Embedding e where e.subject.apiKey = :apiKey and e.subject.name = :subjectName")
    Page<EnhancedEmbeddingProjection> findEnhancedEmbeddingsByApiKeyAndSubjectName(@Param("apiKey") String apiKey, @Param("subjectName") String subjectName, Pageable pageable);

    @Modifying
    @Query("delete from Embedding e where e.subject.apiKey = :apiKey and e.subject.name = :subjectName")
    void deleteByApiKeyAndSubjectName(@Param("apiKey") String apiKey, @Param("subjectName") String subjectName);

    @Modifying
    @Query("delete from Embedding e where e.subject.apiKey = :apiKey and e.subject.name = :subjectName and e.id = :embeddingId")
    void deleteByApiKeyAndSubjectNameAndEmbeddingId(@Param("apiKey") String apiKey, @Param("subjectName") String subjectName, @Param("embeddingId") UUID embeddingId);

    @Modifying
    @Query("delete from Embedding e where e.subject.apiKey = :apiKey")
    void deleteByApiKey(@Param("apiKey") String apiKey);

    @Query("select e from Embedding e where e.id in :ids")
    List<Embedding> findByIds(@Param("ids") Set<UUID> ids);
}
