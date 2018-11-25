package com.jakubeeee.tasks.repositories;

import com.jakubeeee.tasks.model.PastTaskExecution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PastTaskExecutionRepository extends CrudRepository<PastTaskExecution, Long> {
}
