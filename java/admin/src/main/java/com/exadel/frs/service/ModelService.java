package com.exadel.frs.service;

import com.exadel.frs.dto.ModelDto;
import com.exadel.frs.entity.App;
import com.exadel.frs.entity.Model;
import com.exadel.frs.entity.ModelType;
import com.exadel.frs.exception.ModelNotFoundException;
import com.exadel.frs.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final ModelRepository modelRepository;

    public Model createModel(ModelDto modelDto, App app) {
        Model model = Model.builder()
                .name(modelDto.getName())
                .type(ModelType.valueOf(modelDto.getType()))
                .guid(UUID.randomUUID().toString())
                .apiKey(UUID.randomUUID().toString())
                .app(app)
                .build();

        return modelRepository.save(model);
    }

    public Page<Model> getModelsByAppId(Long appId, Pageable pageable) {
        return modelRepository.findByAppId(appId, pageable);
    }

    public Optional<Model> getModelById(Long id) {
        return modelRepository.findById(id);
    }

    public Optional<Model> getModelByGuid(String guid) {
        return modelRepository.findByGuid(guid);
    }

    public Model updateModel(Long id, ModelDto modelDto) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Model not found with id: " + id));

        model.setName(modelDto.getName());
        model.setType(ModelType.valueOf(modelDto.getType()));

        return modelRepository.save(model);
    }

    public void deleteModel(Long id) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Model not found with id: " + id));

        modelRepository.delete(model);
    }

    public boolean existsByUniqueNameAndAppId(String name, Long appId) {
        return modelRepository.existsByUniqueNameAndAppId(name, appId);
    }

    public List<Model> getModelsByIds(List<Long> ids) {
        return modelRepository.findAllById(ids);
    }
}
