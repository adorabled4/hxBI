package com.dhx.bi.repository;

import com.dhx.bi.model.document.Chart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author adorabled4
 * @className ChartRepository
 * @date : 2023/08/25/ 17:17
 **/
@Component
public interface ChartRepository extends MongoRepository<Chart,String> {

    @Query("{'userId': ?0}")
    List<Chart> findAllByUserId(long userId,Pageable pageable);


    boolean deleteAllByChartId(long chartId);

}
