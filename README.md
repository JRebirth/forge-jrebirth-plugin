JRebirth Plugin for Forge
=========================

This project is a effort to create a plugin for Forge to work easily with JRebirth JavaFX Framework. 
This Plugin makes life easy to work with Maven + JavaFX with JRebirth Framework. 

    This project is currently in progress. You may not get full features currenlty.
    Stay tuned.
    
[![Build Status](http://ci.jrebirth.org/job/JRebirth-Forge-master/badge/icon)](http://ci.jrebirth.org/job/JRebirth-Forge-master/)
Prerequisite
=============
* [JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [JBoss Forge](http://forge.jboss.org)
* IDE that supports Maven (Eclipse, NetBeans, IntelliJ etc.)

Installing
==========
* Run the forge
* Type following command in forge shell to install the plugin
      `forge git-plugin https://github.com/JRebirth/forge-jrebirth-plugin.git`
* Some simple flow using the jrebirth 
      new-project --named jrebirthsample
      jrebirth setup
      jrebirth ui-create --name Intro
      jrebirth resource-create --all
      jrebirth color-add-web --name window_background --hex CCCCCC
      jrebirth app-config --key developerMode --value true
      jrebirth app-config --key developerMode
      jrebirth service-create --name RestCall

Commands
========
JRebirth executible command `jrebirth`. Add following sub commands. 

* `setup` - Instals basic dependency for JRebirth.(core, JAVA_HOME javafx runtime, slf4j).
    + Setup also creates jrebirth.properties, MainApp, and resource folders for fonts, images and styles. 
    + `setup --module presentation` - adds _Presentation_ module to your project
* `ui-create` - Creates Model, View and Controller calsses for given name. Use `--name` to provide name.
    + `--controllerGenerate` - default _true_ - Creates Controller
    + `--beanGenerate` - default _true_ - Created Bean
    + `--fxmlGenerate` - default _false_ - Create FXML
    
NOTE: For best practice, above commad creates sub-package and classes inside `ui.[name]` or `ui.fxml.[name]` package in top level package.

* `command-create` - Creates Command calss for given name. Use `--name` to provide name.
* `service-create` - Creates Service class for given name. Use `--name` to provide name.
* `resource-create` - Creates Resource class for given name. Use `--name` to provide name.
    + `--all` - default _true_ - Creates all resource for the application. 
    + `--colorGenerate` - default _false_ - flag - Creates Color 
    + `--fontGenerate` - default _false_ - flag - Creates Font 
    + `--imageGenerate` - default _false_ - flag -Creates Image 

NOTE: For best practice, above commad creates the classes inside `command`, `service` or `resource` package in top level package.

* `color-add-web` - Creates a constant in Color resource interface using colorname/value
    + `--name` - Color constant name
    + `--hex` - Color indicated by hexa decimal value
    
* `app-config` - updates the jrebirth.properties file 
    + `--key` - use <tab> to see all the keys you can update.
    + `--value` - sets value to a key
    + `--showAll` - default _false_ - flag -Displays all the proeprties value of the app.

NOTE: If you provide only the key then it will display the value of the key. If key is new it sets a new key.

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
