package com.exadel.frs.commonservice.repository;

import com.exadel.frs.commonservice.entity.ModelStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ModelStatisticRepository extends JpaRepository<ModelStatistic, Long> {

    @Modifying
    @Query("delete from ModelStatistic ms where ms.createdDate < :date")
    void deleteByCreatedDateBefore(@Param("date") Date date);

    @Modifying
    @Query("update ModelStatistic ms set ms.requestCount = ms.requestCount + 1 where ms.model.apiKey = :apiKey and ms.createdDate = :date")
    void incrementRequestCount(@Param("apiKey") String apiKey, @Param("date") Date date);
}
