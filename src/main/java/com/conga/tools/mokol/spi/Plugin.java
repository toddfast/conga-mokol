package com.conga.tools.mokol.spi;

import com.conga.tools.mokol.PluginBase;
import com.conga.tools.mokol.metadata.CommandDescriptor;
import com.conga.tools.mokol.Shell;
import com.conga.tools.mokol.ShellException;
import com.conga.tools.mokol.metadata.PluginIntrospector;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * A shell plugin providing commands to the shell
 *
 * @author Todd Fast
 */
public abstract class Plugin extends PluginBase {

	/**
	 *
	 *
	 */
	public abstract String getName();


	/**
	 *
	 *
	 */
	public abstract String getVersion();


	/**
	 *
	 *
	 */
	protected Map<String,CommandDescriptor> initialize(Shell shell)
			throws ShellException {
		if (commandDescriptors==null) {
			commandDescriptors=PluginIntrospector.getCommandDescriptors(this);
		}

		return commandDescriptors;
	}


	/**
	 *
	 *
	 */
	protected Map<String,Object> getMutableEnvironment() {
		return environment;
	}


	/**
	 *
	 *
	 */
	protected Map<String,Object> getEnvironment() {
		return Collections.unmodifiableMap(getMutableEnvironment());
	}


	/**
	 *
	 *
	 */
	protected <T> T getEnvironmentValue(String key, Class<T> clazz) {
		return clazz.cast(getMutableEnvironment().get(key));
	}


	/**
	 *
	 *
	 */
	protected void putEnvironmentValue(String key, Object value) {
		if (key==null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("Parameter \"key\" cannot be "+
				"null or an empty string");
		}

		getMutableEnvironment().put(key,value);
	}


	/**
	 *
	 *
	 */
	protected void removeEnvironmentValue(String key) {
		if (key==null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("Parameter \"key\" cannot be "+
				"null or an empty string");
		}

		getMutableEnvironment().remove(key);
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private Map<String,Object> environment=Collections.synchronizedMap(
		new TreeMap<String,Object>());
	private Map<String,CommandDescriptor> commandDescriptors;
}
