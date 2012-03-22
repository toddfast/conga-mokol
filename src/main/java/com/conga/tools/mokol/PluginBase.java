package com.conga.tools.mokol;

import com.conga.tools.mokol.metadata.CommandDescriptor;
import com.conga.tools.mokol.metadata.PluginIntrospector;
import java.util.Map;

/**
 *
 * @author Todd Fast
 */
public abstract class PluginBase {

	/**
	 *
	 *
	 */
	/*pkg*/ final Map<String,CommandDescriptor> _initialize(Shell shell)
			throws ShellException {
		return initialize(shell);
	}


	/**
	 *
	 *
	 */
	protected abstract Map<String,CommandDescriptor> initialize(Shell shell)
		throws ShellException;
}
