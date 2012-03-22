package com.conga.tools.mokol.metadata;

import com.conga.tools.mokol.metadata.CommandDescriptor;
import com.conga.tools.mokol.CommandFactory;
import com.conga.tools.mokol.CommandFactory;

/**
 * A trivial implementation of {@link CommandDescriptor}
 *
 * @author Todd Fast
 */
public class SimpleCommandDescriptor implements CommandDescriptor {

	/**
	 *
	 *
	 */
	public SimpleCommandDescriptor() {
		super();
	}


	/**
	 * 
	 * 
	 */
	public SimpleCommandDescriptor(String alias, CommandFactory factory) {
		super();
		this.alias=alias;
		this.factory=factory;
	}


	/**
	 * 
	 * 
	 */
	@Override
	public String getAlias() {
		return alias;
	}


	/**
	 *
	 *
	 */
	public void setAlias(String value) {
		alias=value;
	}


	/**
	 *
	 *
	 */
	@Override
	public CommandFactory getFactory() {
		return factory;
	}


	/**
	 *
	 *
	 */
	public void setFactory(CommandFactory value) {
		factory=value;
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private String alias;
	private CommandFactory factory;
}
