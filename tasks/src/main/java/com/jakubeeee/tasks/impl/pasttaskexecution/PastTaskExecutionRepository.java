package com.jakubeeee.tasks.impl.pasttaskexecution;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PastTaskExecutionRepository extends CrudRepository<PastTaskExecution, Long> {
}
