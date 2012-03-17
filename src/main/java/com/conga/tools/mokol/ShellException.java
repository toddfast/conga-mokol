package com.conga.tools.mokol;

/**
 *
 * @author Todd Fast
 */
public class ShellException extends Exception {

	public ShellException() {
		super();
	}

	public ShellException(String message) {
		super(message);
	}

	public ShellException(Throwable rootCause) {
		super(rootCause);
	}

	public ShellException(String message, Throwable rootCause) {
		super(message,rootCause);
	}
}
