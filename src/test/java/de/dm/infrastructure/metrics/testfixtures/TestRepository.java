package de.dm.infrastructure.metrics.testfixtures;

import de.dm.infrastructure.metrics.annotation.aop.Metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends CrudRepository<TrivialDomain, String> {

    @Metric
    List<TrivialDomain> findAll();
}
