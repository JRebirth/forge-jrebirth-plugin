JRebirth Plugin for Forge
=========================

This project is a effort to create a plugin for Forge to work easily with JRebirth JavaFX Framework. 
This Plugin makes life easy to work with Maven + JavaFX with JRebirth Framework. 

    This project is currently in progress. You may not get full features currenlty.
    Stay tuned.

Prerequisite
=============
* [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [JBoss Forge](http://forge.jboss.org)
* IDE that supports Maven (Eclipse, NetBeans, IntelliJ etc.)

Currently Eclipse supports a Forge IDE. 

Commands
========
JRebirth executible command `jrebirth`. Add following sub commands. 

* `setup` - Instals basic dependency for to work with JRebirth. This instals JRebirth core and JavaFX Jar to your dependency.
    + Setup also provides option to install JRebirth modules. Currenlty `Presentation` module is supported. 
    + To add `Presentation` to your project execute following command `jrebirth setup --module presentation`
* `create-mvc` - Creates Model, View and Controller calsses for given name. Use `--name` or `--n` to provide name.
* `create-mv` - Creates Model and View calsses for given name. Use `--name` or `--n` to provide name.

NOTE: For best practice, above commad creates sub-package and Class inside `ui.[name]` package in top level package.

* `create-command` - Creates Command calss for given name. Use `--name` or `--n` to provide name.
* `create-service` - Creates Service class for given name. Use `--name` or `--n` to provide name.
* `create-resource` - Creates Resource class for given name. Use `--name` or `--n` to provide name.

NOTE: For best practice, above commad creates the class inside `command`, `service` or `resource` package in top level package.



