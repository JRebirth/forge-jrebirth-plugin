/**
 * Get more info at : www.jrebirth.org . Copyright JRebirth.org © 2011-2013
 * Contact : sebastien.bordes@jrebirth.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jrebirth.forge;

import java.io.FileNotFoundException;
import java.util.List;
import javax.inject.Inject;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.plugins.DefaultCommand;
import javax.enterprise.event.Event;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.util.Packages;

/**
 *
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@Alias("jrebirth")
@Help("A Forge addon to enable and work on JRebirth framework.")
@RequiresFacet({DependencyFacet.class, JavaSourceFacet.class})
@RequiresProject
public class JRebirthPlugin implements Plugin {

    @Inject
    private ShellPrompt shell;
    @Inject
    private Project project;
    @Inject
    private Event<InstallFacets> install;
    @Inject
    private ShellPrintWriter writer;
    private JavaSourceFacet javaSourceFacet;
    private DependencyFacet dependencyFacet;

    enum CreationType {

        MV, MVC, COMMAND, SERVICE, RESOURCE
    }

    @SetupCommand(help = "Installs basic setup to work with JRebirth Framework.")
    public void setup(PipeOut out, @Option(name = "module", shortName = "m", help = "The Module name to be installed.") final String moduleName) {
        if (moduleName == null) {
            if (!project.hasFacet(JRebirthFacet.class)) {
                install.fire(new InstallFacets(JRebirthFacet.class));
            }

            if (project.hasFacet(JRebirthFacet.class)) {
                writer.println(ShellColor.GREEN, "JRebirth is configured.");
            }
        } else if (moduleName.equalsIgnoreCase("Presentation")) {

            installDependencys(jrebirthPresentationDependency(), true);
        }

    }

    @DefaultCommand
    public void defaultCommand(final PipeOut out) {
        if (project.hasFacet(JRebirthFacet.class)) {
            out.println("JRebirth is installed.");
        } else {
            out.println("JRebirth is not installed. Use 'jrebirth setup' to install.");
        }
    }

    private void createUiFiles(CreationType type, String topLevelPackage, DirectoryResource sourceFolder, String name, PipeOut out) {

        DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".ui"));

        if (!directory.isDirectory()) {
            out.println(ShellColor.BLUE, "The UI package is not exists. Creating it.");
            directory.mkdir();
        }

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".ui." + name.toLowerCase()));

        if (directory.isDirectory()) {
            out.println(ShellColor.RED, "Unable to Create package. The package '" + directory.toString() + "' is already found");
            return;
        } else {
            directory.mkdir();
        }

        try {
            String javaStandardClassName = String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1, name.length()));

            if (!directory.getChild(javaStandardClassName + "Model" + ".java").exists()) {
                project.getFacet(JavaSourceFacet.class).saveJavaSource(JavaParser.create(JavaClass.class).setPackage(topLevelPackage + ".ui." + name).setName(javaStandardClassName + "Model").getOrigin());
            } else {
                out.println(ShellColor.BLUE, "The model class " + javaStandardClassName + "Model already exists");
            }
            if (!directory.getChild(javaStandardClassName + "View" + ".java").exists()) {
                project.getFacet(JavaSourceFacet.class).saveJavaSource(JavaParser.create(JavaClass.class).setPackage(topLevelPackage + ".ui." + name).setName(javaStandardClassName + "View").getOrigin());
            } else {
                out.println(ShellColor.BLUE, "The view class " + javaStandardClassName + "View already exists");
            }

            if (type == CreationType.MVC) {
                //Create MVC Files
                if (!directory.getChild(javaStandardClassName + "Controller" + ".java").exists()) {
                    project.getFacet(JavaSourceFacet.class).saveJavaSource(JavaParser.create(JavaClass.class).setPackage(topLevelPackage + ".ui." + name).setName(javaStandardClassName + "Controller").getOrigin());
                } else {
                    out.println(ShellColor.BLUE, "The controller class " + javaStandardClassName + "Controller already exists");
                }
            } else {
                //TODO: Create MV Files                 
            }

        } catch (FileNotFoundException e) {

            out.println(ShellColor.RED, "Could not create files.");
        }
    }

    private void createNonUiFiles(CreationType type, String topLevelPackage, DirectoryResource sourceFolder, String name, PipeOut out) {

        DirectoryResource directory = null;
        //  sourceFolder.getChildDirectory(Packages.toFileSyntax("ui." + name.toLowerCase()));
        String fileType = null;
        JavaClass javaClass = null;
        // Convert first character to upper case
        name = String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1, name.length()));
        switch (type) {
            case COMMAND:
                directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".command."));

                fileType = "Command";

                if (!directory.getChild(name + ".java").exists()) {
                    javaClass = JavaParser.create(JavaClass.class).setPackage(topLevelPackage + ".command").setName(name).setSuperType("DefaultPoolCommand");
                    Method executeStub = javaClass.addMethod().setName("execute").setProtected().setReturnTypeVoid().setParameters("final Wave wave").setBody("");
                    executeStub.addAnnotation("Override");
                    javaClass.addImport("org.jrebirth.core.command.DefaultPoolCommand");
                    javaClass.addImport("org.jrebirth.core.wave.Wave");
                } else {
                    out.println(ShellColor.BLUE, "The command class " + name + " already exists");
                }

                break;
            case SERVICE:
                directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".service."));

                fileType = "Service";

                if (!directory.getChild(name + ".java").exists()) {
                    javaClass = JavaParser.create(JavaClass.class).setPackage(topLevelPackage + ".service").setName(name).setSuperType("ServiceBase");
                    Method readyStub = javaClass.addMethod().setName("ready").setPublic().setReturnTypeVoid().setBody("").addThrows("CoreException");
                    readyStub.addAnnotation("Override");
                    javaClass.addImport("org.jrebirth.core.exception.CoreException");
                    javaClass.addImport("org.jrebirth.core.service.ServiceBase");
                } else {
                    out.println(ShellColor.BLUE, "The service class " + name + " already exists");
                }
                break;
            case RESOURCE:
                directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".resource."));

                fileType = "Resource";
                if (!directory.getChild(name + ".java").exists()) {
                    javaClass = JavaParser.create(JavaClass.class).setPackage(topLevelPackage + ".resource").setName(name);
                } else {
                    out.println(ShellColor.BLUE, "The resource class " + name + " already exists");
                }

                break;
            default:
                fileType = "";
                break;
        }

        if (directory != null && !directory.isDirectory()) {
            out.println(ShellColor.BLUE, "The " + fileType + " package is not exists. Creating it.");
            directory.mkdir();
        }

        try {
            //Create Command/Service/Resource File         
            if (javaClass != null) {
                project.getFacet(JavaSourceFacet.class).saveJavaSource(javaClass);
            }
        } catch (FileNotFoundException ex) {
            out.println(ShellColor.RED, "Could not create files.");
        }


    }

    private void createFiles(CreationType type, String name, PipeOut out) {

        if (name == null || name.equals("")) {
            out.println(ShellColor.RED, "Provide a proper name.");
            return;
        }
        MetadataFacet metadata = project.getFacet(MetadataFacet.class);
        DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();

        switch (type) {
            case MV:
            case MVC:
                createUiFiles(type, metadata.getTopLevelPackage(), sourceFolder, name, out);
                break;
            case COMMAND:
            case SERVICE:
            case RESOURCE:
                createNonUiFiles(type, metadata.getTopLevelPackage(), sourceFolder, name, out);
                break;
            default:
                break;
        }

    }

    @Command(value = "create-mvc", help = "Create Model,View and Controller for the given name")
    public void createMVC(PipeOut out, @Option(name = "name", shortName = "n", required = true, help = "Name of the MVC Group to be created.") final String name) {
        createFiles(CreationType.MVC, name, out);
    }

    @Command(value = "create-mv", help = "Create Model and View for the given name")
    public void createMV(PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the MV Group to be created.") final String name) {

        createFiles(CreationType.MV, name, out);
    }

    @Command(value = "create-command", help = "Create a command for the given name")
    public void createCommand(PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Command to be created.") final String commandName) {
        createFiles(CreationType.COMMAND, commandName, out);
    }

    @Command(value = "create-service", help = "Create a service for the given name")
    public void createService(PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Service to be created.") final String serviceName) {

        createFiles(CreationType.SERVICE, serviceName, out);
    }

    /* TODO: Need to see how to do this. */
    @Command(value = "create-resource", help = "Create a resource for the given name")
    public void createResource(PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Resource to be created.") final String resourceName) {
        createFiles(CreationType.RESOURCE, resourceName, out);
    }

    private static DependencyBuilder jrebirthPresentationDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("presentation");
    }

    private void installDependencys(DependencyBuilder dependency, boolean askVersion) {
        dependencyFacet = project.getFacet(DependencyFacet.class);

        List<Dependency> versions = dependencyFacet.resolveAvailableVersions(dependency);
        if (askVersion) {
            Dependency dep = shell.promptChoiceTyped("What version do you want to install?", versions);
            dependency.setVersion(dep.getVersion());
        }
        dependencyFacet.addDirectDependency(dependency);

        writer.println(ShellColor.GREEN, dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + " is added to the dependency.");

    }
}
