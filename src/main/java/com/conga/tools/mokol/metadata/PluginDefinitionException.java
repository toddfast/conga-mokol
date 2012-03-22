package com.conga.tools.mokol.metadata;

/**
 * Indicates a fatal problem with a plugin definition
 *
 * @author Todd Fast
 */
public class PluginDefinitionException extends RuntimeException {

	/**
	 *
	 *
	 */
	public PluginDefinitionException() {
	}


	/**
	 *
	 *
	 */
	public PluginDefinitionException(String message) {
		super(message);
	}


	/**
	 *
	 *
	 */
	public PluginDefinitionException(Throwable cause) {
		super(cause);
	}


	/**
	 *
	 *
	 */
	public PluginDefinitionException(String message,Throwable cause) {
		super(message,cause);
	}
}
