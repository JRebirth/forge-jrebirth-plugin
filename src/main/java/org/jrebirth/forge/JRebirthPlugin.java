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

import static org.jrebirth.forge.utils.PluginUtils.createJavaFileUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.createJavaInterfaceUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.createPackageIfNotExist;
import static org.jrebirth.forge.utils.PluginUtils.createResourceFileUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.determineFileAvailabilty;
import static org.jrebirth.forge.utils.PluginUtils.determinePackageAvailability;
import static org.jrebirth.forge.utils.PluginUtils.firstLetterCaps;
import static org.jrebirth.forge.utils.PluginUtils.installDependencies;
import static org.jrebirth.forge.utils.PluginUtils.jrebirthPresentationDependency;
import static org.jrebirth.forge.utils.PluginUtils.createJavaEnumUsingTemplate;
import static org.jrebirth.forge.utils.ProfileHelper.setupMavenProjectProfiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.Shell;
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
import org.jrebirth.forge.utils.PluginUtils;
import org.jrebirth.forge.utils.PluginUtils.CreationType;
import org.jrebirth.forge.utils.TemplateSettings;

import freemarker.template.TemplateException;

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

    /** The shell prompt. */
    @Inject
    private ShellPrompt shellPrompt;

    @Inject
    private Shell shell;

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
     * @throws TemplateException
     * @throws IOException
     */
    @SetupCommand(help = "Installs basic setup to work with JRebirth Framework.")
    public void setup(final PipeOut out, @Option(name = "module", shortName = "m", help = "The Module name to be installed.")
    final String moduleName) throws IOException, TemplateException {

        if (!this.project.hasFacet(JRebirthFacet.class)) {
            this.install.fire(new InstallFacets(JRebirthFacet.class));

            final MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);

            TemplateSettings settings = new TemplateSettings(firstLetterCaps(metadata.getProjectName()) + "App", metadata.getTopLevelPackage());
            final Map<String, TemplateSettings> context = new HashMap<String, TemplateSettings>();
            settings.setTopLevelPacakge(metadata.getTopLevelPackage());

            context.put("settings", settings);
            createJavaFileUsingTemplate(this.project, "TemplateApplication.ftl", context);

            final ResourceFacet resourceFacet = this.project.getFacet(ResourceFacet.class);
            final File rbPropertiesFile = resourceFacet.createResource(new char[0], "jrebirth.properties").getUnderlyingResourceObject();

            settings = new TemplateSettings("jrebirth.properties", "");

            createResourceFileUsingTemplate(this.project, "TemplateMainProperties.ftl", rbPropertiesFile, context);

            final DirectoryResource directory = resourceFacet.getResourceFolder();
            directory.getChildDirectory("fonts").mkdir();
            directory.getChildDirectory("images").mkdir();
            directory.getChildDirectory("styles").mkdir();
            
            this.project.getProjectRoot().getChildDirectory("src/main/jnlp").mkdir();

            setupMavenProjectProfiles(project,metadata.getTopLevelPackage(), metadata.getProjectName());

        }
        if (moduleName != null) {
            if ("Presentation".equalsIgnoreCase(moduleName)) {

                installDependencies(this.project, this.shellPrompt, out, jrebirthPresentationDependency(), true);
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
    @Command(value = "ui-create", help = "Create Model,View and Controller for the given name")
    public void createMVC(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the UI Group to be created.")
            final String name,
            @Option(name = "controllerGenerate", shortName = "cg", required = false, defaultValue = "true", help = "If true, Controller will be generated for the MVC.")
            final boolean controllerGenerate,
            @Option(name = "beanGenerate", shortName = "bg", required = false, defaultValue = "true", help = "If true, Bean will be generated for the MVC.")
            final boolean beanGenerate,
            @Option(name = "fxmlGenerate", shortName = "fg", required = false, defaultValue = "false", help = "If true, FXML document will be generated for the MVC in resource folder.")
            final boolean fxmlGenerate

            ) {
        createUiFiles(out, CreationType.UI, name, controllerGenerate, beanGenerate, fxmlGenerate);
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

        final int choiceIndex = this.shellPrompt.promptChoice("Which type of Command you like to create ?", PluginUtils.COMMAND_TYPES);

        createNonUiFiles(CreationType.COMMAND, commandName, out, (String) PluginUtils.COMMAND_TYPES[choiceIndex]);
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
        createNonUiFiles(CreationType.SERVICE, serviceName, out, null);
    }

    /**
     * Creates the resource.
     * 
     * @param out the out
     * @param resourceName the resource name
     */
    @Command(value = "resource-create", help = "Create a resource for the given name")
    public void createResource(final PipeOut out,
            @Option(name = "all", shortName = "a", required = true, help = "Generate all the resource")
            final boolean allResource,
            @Option(name = "colorGenerate", shortName = "cg", required = false, help = "Generate resource for Colors")
            final boolean colorGenerate,
            @Option(name = "fontGenerate", shortName = "fg", required = false, help = "Generate resource for Fonts")
            final boolean fontGenerate,
            @Option(name = "imageGenerate", shortName = "ig", required = false, help = "Generate resource for Images")
            final boolean imageGenerate) {
        createResourceFiles(out, allResource, colorGenerate, fontGenerate, imageGenerate);
    }

    /**
     * Add color to color resource
     * 
     * @param out the out
     * 
     */
    @Command(value = "color-add-web", help = "Add color to color resource")
    public void colorAddWeb(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of color object.")
            final String colorName,
            @Option(name = "hex", shortName = "h", required = true, help = "Color's hex value")
            final String hexValue) {

        DirectoryResource directory = null;
        final MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = this.project.getFacet(JavaSourceFacet.class).getSourceFolder();
        final String topLevelPackage = metadata.getTopLevelPackage();

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
        if (directory.isDirectory() == false || directory.getChild(metadata.getProjectName() + "Colors.java").exists() == false) {
            try {
                out.println(ShellColor.BLUE, "Color resources is not yet created. Creating a new one.");
                this.shell.execute("jrebirth resource-create --all false --colorGenerate true");
            } catch (final Exception e) {
                out.println(ShellColor.RED, "Unable to create resource files for colors.");
                e.printStackTrace();
            }
        }
        final JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
        final JavaInterface jInterface = JavaParser.parse(JavaInterface.class, directory.getChild(metadata.getProjectName() + "Colors.java").getResourceInputStream());
        final String capsColorName = colorName.toUpperCase();

        if (jInterface.hasField(capsColorName) == false) {
            jInterface.addField("/** Color constant for " + capsColorName + "  */ \n ColorItem " + capsColorName + " = create(new WebColor(\"" + hexValue.toUpperCase() + "\"));\n\n");

            try {
                java.saveJavaSource(jInterface);
            } catch (final FileNotFoundException e) {

                out.println(ShellColor.RED, "Unable to save the file while writing the variable (" + capsColorName + ").");
                e.printStackTrace();
            }
        }
        else {
            out.println(ShellColor.RED, "The color constant " + capsColorName + " is already exist. Skipping.");
        }

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

        if (determinePackageAvailability(directory, out) == false) {
            return;
        }

        final String javaStandardClassName = firstLetterCaps(name);

        final TemplateSettings settings = new TemplateSettings(javaStandardClassName, topLevelPackage);
        settings.setTopLevelPacakge(topLevelPackage + type.getPackageName() + "." + name.toLowerCase(Locale.ENGLISH));
        settings.setBeanCreate(beanGenerate);
        settings.setControllerCreate(controllerGenerate);
        settings.setFXMLCreate(fxmlGenerate);

        if (fxmlGenerate)
        {
            final DirectoryResource resourceDir = this.project.getFacet(ResourceFacet.class).getResourceFolder();
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
    private void createNonUiFiles(final CreationType type, final String fileName, final PipeOut out, final String commandType) {

        DirectoryResource directory = null;
        String finalName = "";

        final MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = this.project.getFacet(JavaSourceFacet.class).getSourceFolder();
        final String topLevelPackage = metadata.getTopLevelPackage();

        finalName = firstLetterCaps(fileName);

        try {

            directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + type.getPackageName() + "."));

            createPackageIfNotExist(directory, type.getPackageName(), out);

            final TemplateSettings settings = new TemplateSettings(finalName, topLevelPackage);
            settings.setTopLevelPacakge(topLevelPackage + type.getPackageName());

            if (commandType != null) {
                settings.setCommandType(commandType);
            }

            determineFileAvailabilty(this.project, directory, type, finalName, out, "", ".java", settings);

        } catch (final Exception e) {
            out.println(ShellColor.RED, "Could not create files.");
        }
    }

    private void createResourceFiles(final PipeOut out, final boolean allResource, final boolean colorGenerate, final boolean fontGenerate, final boolean imageGenerate) {

        DirectoryResource directory = null;
        String finalName = "";

        final MetadataFacet metadata = this.project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = this.project.getFacet(JavaSourceFacet.class).getSourceFolder();
        final String topLevelPackage = metadata.getTopLevelPackage();

        finalName = firstLetterCaps(metadata.getProjectName());
        final TemplateSettings settings = new TemplateSettings(finalName, topLevelPackage);
        final Map<String, TemplateSettings> context = new HashMap<String, TemplateSettings>();
        settings.setTopLevelPacakge(topLevelPackage + CreationType.RESOURCE.getPackageName());

        context.put("settings", settings);

        try {
            directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));

            createPackageIfNotExist(directory, CreationType.RESOURCE.getPackageName(), out);

            if (directory != null && directory.getChild(finalName + ".java").exists() == false) {
                // createJavaFileUsingTemplate(project, "", context);

                if (allResource || colorGenerate) {
                    createJavaInterfaceUsingTemplate(this.project, "TemplateColorResource.ftl", context);
                }
                if (allResource || fontGenerate)
                {
                    createJavaInterfaceUsingTemplate(this.project, "TemplateFontsResource.ftl", context);
                    createJavaEnumUsingTemplate(this.project, "TemplateFontNamesResource.ftl", context);
                }
                if (allResource || imageGenerate) {
                    createJavaInterfaceUsingTemplate(this.project, "TemplateImagesResource.ftl", context);
                }

            } else {
                out.println(ShellColor.RED, "The file '" + finalName + "' already exists");
            }

        } catch (final Exception e) {
            e.printStackTrace();
            out.println(ShellColor.RED, "Could not create files.");
        }
    }

}
