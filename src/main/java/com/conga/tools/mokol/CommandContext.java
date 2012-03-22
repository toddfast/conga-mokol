package com.conga.tools.mokol;

import com.conga.tools.mokol.Shell;
import java.io.PrintWriter;

/**
 * Context provided to a command instance when it executes
 *
 * @author Todd Fast
 */
public interface CommandContext {

	/**
	 *
	 *
	 */
	public Shell getShell();


	/**
	 *
	 *
	 */
	public String getCommandAlias();


	/**
	 *
	 *
	 */
	public String readLine();


	/**
	 *
	 *
	 */
	public String readLine(String format,Object... args);


	/**
	 *
	 *
	 */
	public String readPassword();


	/**
	 *
	 *
	 */
	public String readPassword(String format,Object... args);


	/**
	 *
	 *
	 */
	public PrintWriter writer();


	/**
	 *
	 *
	 */
	public CommandContext printf(String format,Object... args);
}
