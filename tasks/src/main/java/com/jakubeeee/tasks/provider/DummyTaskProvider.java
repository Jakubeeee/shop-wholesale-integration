package com.jakubeeee.tasks.provider;

import com.jakubeeee.core.DummyService;
import com.jakubeeee.tasks.GenericTask;

/**
 * Interface for dummy service beans used as imitations of real task providers.
 */
public interface DummyTaskProvider<T extends GenericTask> extends TaskProvider<T>, DummyService {

}
