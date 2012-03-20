Mokol
=====

Mokol is a simple, extensible command-line interface (CLI) platform written in Java, and an ecosystem of plugins that provide commands for a variety of purposes.

## Purpose & Goals
It's a fact of life: Sometimes you need a command line. And when you do, it's frustating to work with homegrown command-line tools that all have different conventions, dependencies, and error handling.

We intend Mokol to make using--and equally important, *writing*--command-line tools consistent and easy. Mokol's simple plugin architecture lets developers create and deploy new commands quickly, and lets users find and use them easily. Mokol is Eclipse for the command line.

Mokol evolved from internal command-line tools we were building at Conga. After a point, it made sense to factor out our infrastructure and refactor all of our tools as modular plugins. A further benefit of this approach is that it allows a 3rd-party ecosystem to evolve around our work. So here we are.

Mokol's philosophy emphasizes simplicity and productivity through metadata. Its goal is to make the work of writing, managing, deploying, and using command line tools easier and more consistent.

## Features
- Plugins! Hopefully lots of them in time.
- Fully cross-platform (Java)
- The Mokol core platform is a single executable JAR. Yes, we consider this a feature.
- Persistent command history
- ANSI support allows powerful formatting across platforms
- Simple, lightweight API using Java annotations makes writing commands easy. Really easy.

### Upcoming features
- Documentation
- More plugins
- Dynamic download and installation of plugins at runtime
- Infrastructure to make writing commands easier and more consistent
- SecurityManager integration to prevent malicious commands from doing Bad Things
- Keyring for storing and accessing data and credentials securely
- Scripting support so that Mokol commands can be easily included in external scripts, and external scripts can be called from Mokol
	
## Using Mokol

Mokol is based on a plugin architecture, and all commands available in the CLI are provided by plugins. There are no built-in commands per se. Plugins are packaged as JAR files and discovered from the classpath.

Therefore, to run Mokol with one or more plugins, simply include the plugins' JAR files in the classpath when running Mokol:

    java -classpath path/to/plugin.jar -jar conga-mokol-0.1-SNAPSHOT.jar

You can also run Mokol with only the base plugin enabled, which is not particularly useful, but lets you verify your environment:

    java -jar conga-mokol-0.1-SNAPSHOT.jar

### Available commands

Type 'help' at Mokol's command line to see all available commands:

    [mokol] help

## Status

The humble code in these initial checkins is *very* minimal and immature. There are missing annotations, key features haven't been added, packages and access modifiers aren't strictly defined for proper modularity, and a whole bunch of other things that we just haven't gotten around to yet. As we refactor our internal plugins, we will have the freedom and time to make these changes and more. Stay tuned!

## FAQ
- Q: Why didn't you use OSGi?

	A: Maybe we will, someday. But writing OSGi bundles can be a pain, and OSGi runtimes are relatively heavyweight because they are trying to solve a much larger problem--more than what we needed. Understanding and writing Mokol plugins is simpler, and the runtime is flyweight by comparison.

- Q: Why didn't you just use existing shells / shell scripts?

	A: We needed something that worked reliably cross-platform, was easier to debug, and that could support an ecosystem of plugins.

- Q: Why didn't you base Mokol on scripting languages/environments like Python, Ruby, or Rhino?

	A: Well, we like Java. Mokol's focus is on creating structured commands with strong metadata, and Java is a great choice for that. Besides, there are already plenty of ways to use these other languages on the command line, but less so for Java. It's fair to say we are interested in integrating plugins written in other languages at a later stage.