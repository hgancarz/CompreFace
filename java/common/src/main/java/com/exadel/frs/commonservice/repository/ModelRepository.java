package com.exadel.frs.commonservice.repository;

import com.exadel.frs.commonservice.entity.Model;
import com.exadel.frs.commonservice.projection.ModelSubjectProjection;
import com.exadel.frs.commonservice.projection.ModelStatisticProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    List<Model> findByApiKey(String apiKey);

    Optional<Model> findByApiKeyAndId(String apiKey, Long id);

    Optional<Model> findByApiKeyAndName(String apiKey, String name);

    Stream<Model> findAllByIdIn(Set<Long> ids);

    Optional<Model> findByGuid(String guid);

    @Query("select " +
            "case when count(m) > 0 then TRUE else FALSE end " +
        "from " +
            "Model m " +
        "where " +
            "lower(m.name) = lower(:name) " +
        "and " +
            "m.app.id = :appId")
    boolean existsByUniqueNameAndAppId(String name, Long appId);

    @Query("select " +
            "count(m) " +
        "from " +
            "Model m " +
        "where " +
            "lower(m.name) = lower(:name) " +
        "and " +
            "m.app.id = :appId")
    int countByUniqueNameAndAppId(String name, Long appId);

    @Query("select " +
            "new com.exadel.frs.commonservice.projection.ModelSubjectProjection(m.guid, count(s.id)) " +
        "from " +
            "Model m " +
        "left join " +
            "Subject s on m.apiKey = s.apiKey " +
        "group by " +
            "m.guid")
    List<ModelSubjectProjection> getModelSubjectsCount();

    @Query("select " +
            "new com.exadel.frs.commonservice.projection.ModelStatisticProjection(ms.requestCount, ms.createdDate) " +
        "from " +
            "ModelStatistic ms " +
        "where " +
            "ms.model.apiKey = :apiKey " +
        "and " +
            "ms.createdDate >= :startDate " +
        "and " +
            "ms.createdDate <= :endDate " +
        "order by " +
            "ms.createdDate")
    List<ModelStatisticProjection> getModelStatistics(@Param("apiKey") String apiKey, @Param("startDate") java.util.Date startDate, @Param("endDate") java.util.Date endDate);
}
