package ch.niceneasy.openstack.android.base;

public class TaskResult<Type> {

	private Exception exception;

	private Type result;

	public TaskResult(final Type result) {
		this.result = result;
	}

	public TaskResult(final Exception exception) {
		this.exception = exception;
	}

	public final boolean isValid() {
		return exception == null;
	}

	public final Exception getException() {
		return exception;
	}

	public final Type getResult() {
		return result;
	}
}
