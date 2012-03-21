package com.conga.tools.mokol.spi;

import com.conga.tools.mokol.Shell;
import com.conga.tools.mokol.ShellException;

/**
 * A shell plugin providing commands to the shell
 *
 * @author Todd Fast
 */
public interface Plugin {

	/**
	 *
	 *
	 */
	public String getName();


	/**
	 *
	 *
	 */
	public String getVersion();


	/**
	 * TODO: Don't pass Shell here; instead pass a safe context
	 *
	 */
	public void initialize(Shell cli)
		throws ShellException;
}
