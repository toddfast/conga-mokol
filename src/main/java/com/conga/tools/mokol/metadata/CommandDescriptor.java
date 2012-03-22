package com.conga.tools.mokol.metadata;

import com.conga.tools.mokol.CommandFactory;
import com.conga.tools.mokol.CommandFactory;

/**
 *
 * @author Todd Fast
 */
public interface CommandDescriptor {

	/**
	 *
	 *
	 */
	public String getAlias();


	/**
	 *
	 *
	 */
	public CommandFactory getFactory();
}
