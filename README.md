Mokol
=====

Mokol is a simple, extensible command-line interface (CLI) platform written in Java, and an ecosystem of plugins that provide commands for a variety of purposes.

### Purpose & Goals
Sometimes you need a command-line. Mokol is intended to make writing command-line tools consistent and easy. Mokol's simple plugin architecture lets developers create and deploy new commands quickly.

Mokol evolved from internal command-line tools we were building at Conga. After a point, it made sense to factor out the infrastructure and refactor all the tools as plugins. A further benefit of this approach is that it allows an ecosystem to evolve around our work.

Mokol's philosophy emphasizes simplicity over sheer power (though of course, Mokol plugins can do just about anything). Our goal is to make all the work about writing, managing, and deploying commands easier. Thus, Mokol aspires to be:

- Simple for everyone to understand
- Simple for developers to extend
- Simple for users to use

### Features for users
- Plugins! Hopefully lots of them in time.
- Fully cross-platform (written in Java)
- Persistent command history

### Features for Mokol plugin developers
- Simple, lightweight API using Java annotations makes writing commands easy. Really easy.
- ANSI support allows powerful formatting across platforms

### Upcoming features
- More plugins
- Dyamic installation of plugins at runtime
- Infrastructure to help format command output

### Why didn't you...?
- Q: Just write some Maven plugins?

	A: Maven isn't simple to understand, extend, or use. It's not a reasonable runtime environment for general-purpose command-line tools.

- Q: Use OSGi?

	A: Maybe we will, someday. But writing OSGi bundles can be complicated, and OSGi runtimes are relatively heavyweight--more than what we needed. Understanding and writing Mokol plugins is far simpler, and the runtime is flyweight by comparison.

- Q: Use BeanShell?

	A: We wanted more structure and simplicity.

- Q: Use shell scripts?

	A: We needed something that worked reliably cross-platform, was easier to debug, and that could support an ecosystem of plugins.

- Q: Use scripting environments like Python, Ruby, etc.

	A: Well, we like Java. Also, Mokol's focus is on creating structured commands with strong metadata. Metadata is critical to automation and tooling; general scripting is flexible, but less structured.
	
