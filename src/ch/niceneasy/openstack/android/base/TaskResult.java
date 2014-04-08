/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.android.base;

/**
 * The Class TaskResult.
 * 
 * @author Daniele
 * @param <Type>
 *            the generic type
 */
public class TaskResult<Type> {

	/** The exception. */
	private Exception exception;

	/** The result. */
	private Type result;

	/**
	 * Instantiates a new task result.
	 * 
	 * @param result
	 *            the result
	 */
	public TaskResult(final Type result) {
		this.result = result;
	}

	/**
	 * Instantiates a new task result.
	 * 
	 * @param exception
	 *            the exception
	 */
	public TaskResult(final Exception exception) {
		this.exception = exception;
	}

	/**
	 * Checks if is valid.
	 * 
	 * @return true, if is valid
	 */
	public final boolean isValid() {
		return exception == null;
	}

	/**
	 * Gets the exception.
	 * 
	 * @return the exception
	 */
	public final Exception getException() {
		return exception;
	}

	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	public final Type getResult() {
		return result;
	}
}
