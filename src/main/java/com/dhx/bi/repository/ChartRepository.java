package com.dhx.bi.repository;

import com.dhx.bi.model.document.Chart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * @author adorabled4
 * @className ChartRepository
 * @date : 2023/08/25/ 17:17
 **/
@Component
public interface ChartRepository extends MongoRepository<Chart,String> {
}
