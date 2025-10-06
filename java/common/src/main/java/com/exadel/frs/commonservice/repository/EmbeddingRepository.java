/*
 * Copyright (c) 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.exadel.frs.commonservice.repository;

import com.exadel.frs.commonservice.entity.Embedding;
import com.exadel.frs.commonservice.projection.EmbeddingProjection;
import com.exadel.frs.commonservice.projection.EnhancedEmbeddingProjection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmbeddingRepository extends JpaRepository<Embedding, UUID> {

    List<Embedding> findAllBySubjectApiKey(String subjectApiKey);

    Optional<Embedding> findByIdAndSubjectApiKey(UUID embeddingId, String subjectApiKey);

    List<Embedding> findAllBySubjectId(Long subjectId);

    @Modifying
    @Query("delete from Embedding e where e.subject.id = :subjectId")
    void deleteBySubjectId(Long subjectId);

    @Modifying
    @Query("delete from Embedding e where e.subject.id in :subjectIds")
    void deleteBySubjectIds(@Param("subjectIds") Set<Long> subjectIds);

    @Query("select new com.exadel.frs.commonservice.projection.EmbeddingProjection(e.id, e.subject.name) from Embedding e where e.subject.apiKey = :subjectApiKey")
    List<EmbeddingProjection> findEmbeddingProjectionsBySubjectApiKey(String subjectApiKey);

    @Query("select new com.exadel.frs.commonservice.projection.EnhancedEmbeddingProjection(e.id, e.subject.name, e.calculator) from Embedding e where e.subject.apiKey = :subjectApiKey")
    List<EnhancedEmbeddingProjection> findEnhancedEmbeddingProjectionsBySubjectApiKey(String subjectApiKey);

    @Query("select count(e) from Embedding e where e.subject.apiKey = :subjectApiKey")
    long countBySubjectApiKey(String subjectApiKey);

    @Query("select count(e) from Embedding e where e.subject.id = :subjectId")
    long countBySubjectId(Long subjectId);

    @Query("select e from Embedding e where e.subject.apiKey = :subjectApiKey and e.subject.name = :subjectName")
    List<Embedding> findBySubjectApiKeyAndSubjectName(@Param("subjectApiKey") String subjectApiKey, @Param("subjectName") String subjectName);
}
