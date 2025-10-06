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

import com.exadel.frs.commonservice.entity.Model;
import com.exadel.frs.commonservice.projection.ModelProjection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Optional<Model> findByApiKey(String apiKey);

    List<Model> findAllByAppId(Long appId);

    Stream<Model> findAllByIdIn(Set<Long> ids);

    Optional<Model> findByGuid(String guid);

    @Query("select case when count(m) > 0 then TRUE else FALSE end from Model m where lower(m.name) = lower(:name) and m.app.id = :appId")
    boolean existsByUniqueNameAndAppId(String name, Long appId);

    @Query("select count(m) from Model m where lower(m.name) = lower(:name) and m.app.id = :appId")
    long countByUniqueNameAndAppId(String name, Long appId);

    @Query("select new com.exadel.frs.commonservice.projection.ModelProjection(m.name, m.apiKey) from Model m where m.app.id = :appId")
    List<ModelProjection> findModelProjectionsByAppId(Long appId);

    @Modifying
    @Query("update Model m set m.name = :name where m.id = :id")
    void updateModelName(Long id, String name);

    @Query("select m from Model m where m.app.id = :appId and m.apiKey = :apiKey")
    Optional<Model> findByAppIdAndApiKey(@Param("appId") Long appId, @Param("apiKey") String apiKey);
}
