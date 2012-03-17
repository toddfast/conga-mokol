package com.conga.tools.mokol;

/**
 * A Shell plugin
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


//	/**
//	 *
//	 *
//	 */
//	public void finalize(Shell cli)
//		throws ShellException;
}
