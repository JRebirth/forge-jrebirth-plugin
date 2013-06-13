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

import static org.jrebirth.forge.utils.Constants.createPackageIfNotExist;
import static org.jrebirth.forge.utils.Constants.determineFileAvailabilty;
import static org.jrebirth.forge.utils.Constants.determinePackageAvailability;
import static org.jrebirth.forge.utils.Constants.installDependencies;
import static org.jrebirth.forge.utils.Constants.jrebirthPresentationDependency;

import java.util.Locale;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.util.Packages;
import org.jrebirth.forge.utils.Constants.CreationType;
import org.jrebirth.forge.utils.TemplateSettings;

/**
 * The main plugin for JRebirth.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * @author Guruprasad Shenoy <gpshenoy@gmail.com>
 */
@Alias("jrebirth")
@Help("A Forge addon to enable and work on JRebirth framework.")
@RequiresFacet({ DependencyFacet.class, JavaSourceFacet.class })
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

    /**
     * The setup command for JRebirth. This adds dependency to the current project
     * 
     * @param out the out
     * @param moduleName the module name
     */
    @SetupCommand(help = "Installs basic setup to work with JRebirth Framework.")
    public void setup(final PipeOut out, @Option(name = "module", shortName = "m", help = "The Module name to be installed.")
    final String moduleName) {

        if (!this.project.hasFacet(JRebirthFacet.class)) {
            this.install.fire(new InstallFacets(JRebirthFacet.class));
        }
        if (moduleName != null) {
            if ("Presentation".equalsIgnoreCase(moduleName)) {

                installDependencies(this.project, this.shell, out, jrebirthPresentationDependency(), true);
            }
        }
    }

    /**
     * If jrebirth command is not executed with any argument this method will be called.
     * 
     * @param out the out
     */
    @DefaultCommand
    public void defaultCommand(final PipeOut out) {
        if (this.project.hasFacet(JRebirthFacet.class)) {
            out.println("JRebirth is installed.");
        } else {
            out.println("JRebirth is not installed. Use 'jrebirth setup' to install.");
        }
    }

    /**
     * Command to create Model, View and Controller.
     * 
     * @param out the out
     * @param name the name
     * @param controllerGenerate the controller generate
     * @param beanGenerate the bean generate
     * @param fxmlGenerate the fxml generate
     */
    @Command(value = "mvc-create", help = "Create Model,View and Controller for the given name")
    public void createMVC(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the MVC Group to be created.")
            final String name,
            @Option(name = "controllerGenerate", shortName = "cg", required = false, defaultValue = "true", help = "Is the Controller to be generatted automatically? ")
            final boolean controllerGenerate,
            @Option(name = "beanGenerate", shortName = "bg", required = false, defaultValue = "true", help = "Is the Controller to be generatted automatically? ")
            final boolean beanGenerate,
            @Option(name = "fxmlGenerate", shortName = "fg", required = false, defaultValue = "false", help = "Is the Controller to be generatted automatically? ")
            final boolean fxmlGenerate

            ) {
        createUiFiles(out, CreationType.MVC, name, controllerGenerate, beanGenerate, fxmlGenerate);
    }

    /**
     * Creates the command.
     * 
     * @param out the out
     * @param commandName the command name
     */
    @Command(value = "command-create", help = "Create a command for the given name")
    public void createCommand(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Command to be created.")
            final String commandName) {
        createNonUiFiles(CreationType.COMMAND, commandName, out);
    }

    /**
     * Creates the service.
     * 
     * @param out the out
     * @param serviceName the service name
     */
    @Command(value = "service-create", help = "Create a service for the given name")
    public void createService(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Service to be created.")
            final String serviceName) {
        createNonUiFiles(CreationType.SERVICE, serviceName, out);
    }

    /**
     * Creates the resource.
     * 
     * @param out the out
     * @param resourceName the resource name
     */
    @Command(value = "resource-create", help = "Create a resource for the given name")
    public void createResource(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the Resource to be created.")
            final String resourceName) {
        createNonUiFiles(CreationType.RESOURCE, resourceName, out);
    }

    /**
     * Creates Java files for user interface mainly for Model, Controller and View.
     * 
     * @param out the out
     * @param type the type
     * @param name the name
     * @param controllerGenerate the controller generate
     * @param beanGenerate the bean generate . * @param fxmlGenerate the fxml generate
     */
    private void createUiFiles(final PipeOut out, final CreationType type, final String name, final boolean controllerGenerate, final boolean beanGenerate, final boolean fxmlGenerate) {

        final MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = this.project.getFacet(JavaSourceFacet.class).getSourceFolder();

        final String topLevelPackage = metadata.getTopLevelPackage();

        DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + type.getPackageName()));

        createPackageIfNotExist(directory, "UI", out);

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + type.getPackageName() + "." + name.toLowerCase(Locale.ENGLISH)));

        if (determinePackageAvailability(directory, out) == false)
            return;

        final String javaStandardClassName = String.valueOf(name.charAt(0)).toUpperCase().concat(name.substring(1, name.length()));

        TemplateSettings settings = new TemplateSettings(javaStandardClassName, topLevelPackage);
        settings.setTopLevelPacakge(topLevelPackage + type.getPackageName() + "." + name.toLowerCase(Locale.ENGLISH));
        settings.setBeanCreate(beanGenerate);
        settings.setControllerCreate(controllerGenerate);
        settings.setFXMLCreate(fxmlGenerate);

        if (fxmlGenerate)
        {
            DirectoryResource resourceDir = this.project.getFacet(ResourceFacet.class).getResourceFolder();
            createPackageIfNotExist(resourceDir.getChildDirectory("ui"), "", out);
            createPackageIfNotExist(resourceDir.getChildDirectory("ui").getChildDirectory("fxml"), "", out);

            determineFileAvailabilty(this.project, resourceDir, CreationType.FXML, javaStandardClassName, out, "", ".fxml", settings);
        }

        determineFileAvailabilty(this.project, directory, type, javaStandardClassName, out, "Model", "Model.java", settings);

        determineFileAvailabilty(this.project, directory, type, javaStandardClassName, out, "View", "View.java", settings);
        if (controllerGenerate) {
            determineFileAvailabilty(this.project, directory, type, javaStandardClassName, out, "Controller", "Controller.java", settings);
        }

        if (beanGenerate)
        {
            final DirectoryResource beansDirectory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + CreationType.BEAN.getPackageName()));

            createPackageIfNotExist(beansDirectory, "beans", out);

            determineFileAvailabilty(this.project, beansDirectory, CreationType.BEAN, javaStandardClassName, out, "", ".java", settings);
        }
    }

    /**
     * Creates Java files for Command, Service etc.
     * 
     * @param type the type
     * @param fileName the file name
     * @param out the out
     */
    private void createNonUiFiles(final CreationType type, final String fileName, final PipeOut out) {

        DirectoryResource directory = null;
        String finalName = "";

        final MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = this.project.getFacet(JavaSourceFacet.class).getSourceFolder();
        final String topLevelPackage = metadata.getTopLevelPackage();

        finalName = String.valueOf(fileName.charAt(0)).toUpperCase().concat(fileName.substring(1, fileName.length()));

        try {

            directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + type.getPackageName() + "."));

            if (directory != null && !directory.isDirectory()) {
                out.println(ShellColor.BLUE, "The " + type.getPackageName() + " package does not exist. Creating it.");
                directory.mkdir();
            }

            TemplateSettings settings = new TemplateSettings(finalName, topLevelPackage);
            settings.setTopLevelPacakge(topLevelPackage + type.getPackageName());
            determineFileAvailabilty(this.project, directory, type, finalName, out, "", ".java", settings);

        } catch (final Exception e) {
            out.println(ShellColor.RED, "Could not create files.");
        }
    }

}
