package com.jakubeeee.tasks.service;

import com.jakubeeee.core.service.DummyService;
import com.jakubeeee.tasks.model.GenericTask;

/**
 * Interface for dummy service beans used as imitations of real task providers.
 */
public interface DummyTaskProvider<T extends GenericTask> extends TaskProvider<T>, DummyService {

}
