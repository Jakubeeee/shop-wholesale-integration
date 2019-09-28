package com.jakubeeee.tasks;


/**
 * An enum used to configure the behaviour of a task
 *
 * @see GenericTask
 */
public enum TaskMode {
    /**
     * TESTING mode is used for testing purposes, without saving the changes made by a task
     */
    TESTING,
    /**
     * OPERATING mode is used when changes made by the task are to be saved
     */
    OPERATING
}