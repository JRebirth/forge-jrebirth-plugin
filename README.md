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

Commands
========
JRebirth executible command `jrebirth`. Add following sub commands. 

* `setup` - Instals basic dependency for to work with JRebirth. This instals JRebirth core and JavaFX Jar to your dependency.
    + Setup also provides option to install JRebirth modules. Currenlty _Presentation_ module is supported. 
    + To add _Presentation_ to your project execute following command `jrebirth setup --module presentation`
* `mvc-create` - Creates Model, View and Controller calsses for given name. Use `--name` or `-n` to provide name.
* `mv-create` - Creates Model and View calsses for given name. Use `--name` or `-n` to provide name.
* `fxml-create` - Creates FXML JRebirth source files for given name. Use `--name` or `-n` to provide name.

NOTE: For best practice, above commad creates sub-package and Class inside `ui.[name]` or `ui.fxml.[name]` package in top level package.

* `command-create` - Creates Command calss for given name. Use `--name` or `-n` to provide name.
* `service-create` - Creates Service class for given name. Use `--name` or `-n` to provide name.
* `resource-create` - Creates Resource class for given name. Use `--name` or `-n` to provide name.

NOTE: For best practice, above commad creates the class inside `command`, `service` or `resource` package in top level package.

Useful Links
============
* [JRebirth MainSite](http://jrebirth.org/)
* [JRebirth Source Repo](https://github.com/JRebirth)
* [JRebirth MailingList](https://groups.google.com/forum/?fromgroups#!forum/jrebirth-users)
* [Forge](http://forge.jboss.org/)
* [JRebirth Forge Wiki](https://github.com/JRebirth/forge-jrebirth-plugin/wiki)
* [JRebirth Forge Issues](https://github.com/JRebirth/forge-jrebirth-plugin/issues)
* [JRebirth Forge CI](http://ci.jrebirth.org/job/JRebirth-Forge-master/)

Contributors
============
* Sébastien Bordes
* Rajmahendra Hegde
* Guruprasad Shenoy

License
=======

 Get more info at : www.jrebirth.org . Copyright JRebirth.org © 2011-2013 Contact : sebastien.bordes@jrebirth.org

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 specific language governing permissions and limitations under the License.
