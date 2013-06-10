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
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
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
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
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
 * The main plugin for JRebirth.
 *
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@Alias("jrebirth")
@Help("A Forge addon to enable and work on JRebirth framework.")
@RequiresFacet({DependencyFacet.class, JavaSourceFacet.class})
@RequiresProject
public class JRebirthPlugin implements Plugin {

    /** The shell. */
    @Inject
    private ShellPrompt shell;
    
    /** The project. */
    @Inject
    private Project project;
    
    /** The install. */
    @Inject
    private Event<InstallFacets> install;
    
    /** The writer. */
    @Inject
    private ShellPrintWriter writer;
    
    /** The dependency facet. */
    private DependencyFacet dependencyFacet;

    /**
     * The Enum CreationType.
     */
    enum CreationType {

        /** The mv. */
        MV, /** The mvc. */
 MVC, /** The command. */
 COMMAND, /** The service. */
 SERVICE, /** The resource. */
 RESOURCE, /** The fxml. */
 FXML, /** The bean. */
 BEAN
    }

    static {
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");


        Velocity.init(properties);

    }

    /**
     * The setup command for JRebirth. This adds dependency to the current project
     *
     * @param out the out
     * @param moduleName the module name
     */
    @SetupCommand(help = "Installs basic setup to work with JRebirth Framework.")
    public void setup(final PipeOut out, @Option(name = "module", shortName = "m", help = "The Module name to be installed.") final String moduleName) {
        if (moduleName == null) {
            if (!project.hasFacet(JRebirthFacet.class)) {
                install.fire(new InstallFacets(JRebirthFacet.class));
            }

            if (project.hasFacet(JRebirthFacet.class)) {
                writer.println(ShellColor.GREEN, "JRebirth is configured.");
            }
        } else if (moduleName.equalsIgnoreCase("Presentation")) {

        	installDependencies(jrebirthPresentationDependency(), true);
        }

    }

    /**
     * If jrebirth command is not executed with any argument this method will be called.
     *
     * @param out the out
     */
    @DefaultCommand
    public void defaultCommand(final PipeOut out) {
        if (project.hasFacet(JRebirthFacet.class)) {
            out.println("JRebirth is installed.");
        } else {
            out.println("JRebirth is not installed. Use 'jrebirth setup' to install.");
        }
    }

    /**
     * Creates Java files for user interface mainly for Model, Controller and View.
     *
     * @param type the type
     * @param topLevelPackage the top level package
     * @param sourceFolder the source folder
     * @param name the name
     * @param out the out
     */
    private void createUiFiles(CreationType type, String topLevelPackage, DirectoryResource sourceFolder, String name, PipeOut out) {

        DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".ui"));

        if (!directory.isDirectory()) {
            out.println(ShellColor.BLUE, "The UI package does not exist. Creating it.");
            directory.mkdir();
        }

        DirectoryResource beansDirectory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".beans"));
        if (!beansDirectory.isDirectory()) {
            out.println(ShellColor.BLUE, "The beans package does not exist. Creating it.");
            beansDirectory.mkdir();
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

            if (!beansDirectory.getChild(javaStandardClassName + ".java").exists()) {
                generateFile(CreationType.BEAN, javaStandardClassName, "", topLevelPackage);

            } else {
                out.println(ShellColor.RED, "The model class " + javaStandardClassName + " Model already exists");
            }


            if (!directory.getChild(javaStandardClassName + "Model.java").exists()) {
                generateFile(type, javaStandardClassName, "Model", topLevelPackage);

            } else {
                out.println(ShellColor.RED, "The model class " + javaStandardClassName + " Model already exists");
            }

            if (!directory.getChild(javaStandardClassName + "View.java").exists()) {
                generateFile(type, javaStandardClassName, "View", topLevelPackage);
            } else {
                out.println(ShellColor.RED, "The view class " + javaStandardClassName + " View already exists");
            }

            if (type == CreationType.MVC) {
                //Create MVC Files
                if (!directory.getChild(javaStandardClassName + "Controller.java").exists()) {
                    generateFile(type, javaStandardClassName, "Controller", topLevelPackage);
                } else {
                    out.println(ShellColor.RED, "The controller class " + javaStandardClassName + "Controller already exists");
                }
            }

        } catch (FileNotFoundException e) {

            out.println(ShellColor.RED, "Could not create files.");
        } catch (Exception e) {
            out.println(ShellColor.RED, "Could not create files. Unexpected error occured");
        }
    }

    /**
     * Creates FXML and controller files.
     *
     * @param type the type
     * @param topLevelPackage the top level package
     * @param sourceFolder the source folder
     * @param name the name
     * @param out the out
     */
    private void createUiFxmlFiles(CreationType type, String topLevelPackage, DirectoryResource sourceFolder, String name, PipeOut out) {

        DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".ui.fxml"));

        if (!directory.isDirectory()) {
            out.println(ShellColor.BLUE, "The FXML UI package does not exist. Creating it.");
            directory.mkdirs();
        }

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + ".ui.fxml." + name.toLowerCase()));

        if (directory.isDirectory()) {
            out.println(ShellColor.RED, "Unable to Create package. The package '" + directory.toString() + "' is already found");
            return;
        } else {
            directory.mkdir();
        }

    }

    /**
     * Creates Java files for Command, Service etc.
     *
     * @param type the type
     * @param topLevelPackage the top level package
     * @param sourceFolder the source folder
     * @param name the name
     * @param out the out
     */
    private void createNonUiFiles(CreationType type, String topLevelPackage, DirectoryResource sourceFolder, String name, PipeOut out) {

        DirectoryResource directory = null;
        String dirSuffix = null;

        // Convert first character to upper case
        name = String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1, name.length()));
        try {
            switch (type) {
                case COMMAND:

                    dirSuffix = ".command";
                    break;

                case SERVICE:

                    dirSuffix = ".service";
                    if (!name.contains("service") && !name.contains("Service")) {
                        name = name.concat("Service");
                    }
                    break;

                case RESOURCE:

                    dirSuffix = ".resource";
                    break;
                default: ;
                    break;
            }

            if (dirSuffix != null) {

                directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + dirSuffix + "."));

                if (directory != null && !directory.isDirectory()) {
                    out.println(ShellColor.BLUE, "The " + dirSuffix + " package does not exist. Creating it.");
                    directory.mkdir();
                }

                if (directory != null && !directory.getChild(name + ".java").exists()) {
                    generateFile(type, name, "", topLevelPackage);

                } else {
                    out.println(ShellColor.RED, "The resource class " + name + " already exists");
                }

            }

        } catch (Exception e) {
            out.println(ShellColor.RED, "Could not create files.");

        }

    }

    /**
     * Creates the files.
     *
     * @param type the type
     * @param name the name
     * @param out the out
     */
    private void createFiles(CreationType type, String name, PipeOut out) {

       
        final MetadataFacet metadata = project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();

        switch (type) {
            case MV:
            case MVC:
                createUiFiles(type, metadata.getTopLevelPackage(), sourceFolder, name, out);
                break;
            case FXML:
                createUiFxmlFiles(type, metadata.getTopLevelPackage(), sourceFolder, name, out);
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

    /**
     * Command to create Model, View and Controller. 
     *
     * @param out the out
     * @param name the name
     */
    @Command(value = "mvc-create", help = "Create Model,View and Controller for the given name")
    public void createMVC(final PipeOut out, @Option(name = "name", shortName = "n", required = true, help = "Name of the MVC Group to be created.") final String name) {
        createFiles(CreationType.MVC, name, out);
    }

    /**
     * Creates the mv.
     *
     * @param out the out
     * @param name the name
     */
    @Command(value = "mv-create", help = "Create Model and View for the given name")
    public void createMV(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the MV Group to be created.") final String name) {

        createFiles(CreationType.MV, name, out);
    }

    /**
     * Creates the command.
     *
     * @param out the out
     * @param commandName the command name
     */
    @Command(value = "command-create", help = "Create a command for the given name")
    public void createCommand(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Command to be created.") final String commandName) {
        createFiles(CreationType.COMMAND, commandName, out);
    }

    /**
     * Creates the service.
     *
     * @param out the out
     * @param serviceName the service name
     */
    @Command(value = "service-create", help = "Create a service for the given name")
    public void createService(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Service to be created.") final String serviceName) {

        createFiles(CreationType.SERVICE, serviceName, out);
    }

    /* TODO: Need to see how to do this. */
    /**
     * Creates the resource.
     *
     * @param out the out
     * @param resourceName the resource name
     */
    @Command(value = "resource-create", help = "Create a resource for the given name")
    public void createResource(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Resource to be created.") final String resourceName) {
        createFiles(CreationType.RESOURCE, resourceName, out);
    }

    /**
     * Jrebirth presentation dependency.
     *
     * @return the dependency builder
     */
    private static DependencyBuilder jrebirthPresentationDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("presentation");
    }

    /**
     * Install dependencies.
     *
     * @param dependency the dependency
     * @param askVersion the ask version
     */
    private void installDependencies(DependencyBuilder dependency, boolean askVersion) {
        dependencyFacet = project.getFacet(DependencyFacet.class);

        final List<Dependency> versions = dependencyFacet.resolveAvailableVersions(dependency);
        if (askVersion) {
            final Dependency dep = shell.promptChoiceTyped("What version do you want to install?", versions);
            dependency.setVersion(dep.getVersion());
        }
        dependencyFacet.addDirectDependency(dependency);

        writer.println(ShellColor.GREEN, dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + " is added to the dependency.");

    }

    /**
     * Generate file.
     *
     * @param fileType the file type
     * @param name the name
     * @param suffix the suffix
     * @param topLevelPackage the top level package
     * @throws ResourceNotFoundException the resource not found exception
     * @throws ParseErrorException the parse error exception
     * @throws MethodInvocationException the method invocation exception
     * @throws Exception the exception
     */
    private void generateFile(CreationType fileType, String name, String suffix, String topLevelPackage) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {

        JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
        StringWriter writer = new StringWriter();

        VelocityContext context = new VelocityContext();

        context.put("name", name);
        context.put("packageImport", topLevelPackage);

        //Check fileType and change template
        switch (fileType) {
            case MV:
            case MVC:

                context.put("package", topLevelPackage + ".ui." + name.toLowerCase());

                if (suffix.equals("Model")) {

                    Velocity.mergeTemplate("TemplateModel.vtl", "UTF-8", context, writer);

                } else if (suffix.equals("View")) {

                    Velocity.mergeTemplate("TemplateView.vtl", "UTF-8", context, writer);

                } else if (suffix.equals("Controller")) {

                    Velocity.mergeTemplate("TemplateController.vtl", "UTF-8", context, writer);

                }

                break;
            case FXML:

                break;
            case COMMAND:
                context.put("package", topLevelPackage + ".command");
                Velocity.mergeTemplate("TemplateCommand.vtl", "UTF-8", context, writer);
                break;
            case SERVICE:
                context.put("package", topLevelPackage + ".service");
                Velocity.mergeTemplate("TemplateService.vtl", "UTF-8", context, writer);
                break;
            case RESOURCE:
                context.put("package", topLevelPackage + ".resource");
                Velocity.mergeTemplate("TemplateResource.vtl", "UTF-8", context, writer);
                break;
            case BEAN:
                context.put("package", topLevelPackage + ".beans");
                Velocity.mergeTemplate("TemplateBean.vtl", "UTF-8", context, writer);
                break;
            default:
                break;
        }

        JavaClass javaClass = JavaParser.parse(JavaClass.class, writer.toString());
        java.saveJavaSource(javaClass);

    }
}
