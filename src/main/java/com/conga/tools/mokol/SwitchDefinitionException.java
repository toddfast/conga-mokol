package com.conga.tools.mokol;

/**
 * Indicates a fatal problem with a switch definition
 *
 * @author Todd Fast
 */
public class SwitchDefinitionException extends RuntimeException {

	/**
	 *
	 *
	 */
	public SwitchDefinitionException() {
	}


	/**
	 *
	 *
	 */
	public SwitchDefinitionException(String message) {
		super(message);
	}


	/**
	 *
	 *
	 */
	public SwitchDefinitionException(Throwable cause) {
		super(cause);
	}


	/**
	 *
	 *
	 */
	public SwitchDefinitionException(String message,Throwable cause) {
		super(message,cause);
	}
}
