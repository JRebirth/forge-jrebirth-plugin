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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import freemarker.template.TemplateException;

import org.jrebirth.forge.completer.AppPropertyCompleter;
import org.jrebirth.forge.utils.PluginUtils;
import org.jrebirth.forge.utils.PluginUtils.CreationType;
import org.jrebirth.forge.utils.ResourceUtils;
import org.jrebirth.forge.utils.TemplateSettings;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
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

import static org.jrebirth.forge.utils.PluginUtils.createFullPackageIfNotExist;
import static org.jrebirth.forge.utils.PluginUtils.createJNLPConfiguration;
import static org.jrebirth.forge.utils.PluginUtils.createJavaEnumUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.createJavaFileUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.createJavaInterfaceUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.createPackageIfNotExist;
import static org.jrebirth.forge.utils.PluginUtils.createResourceFileUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.determineFileAvailabilty;
import static org.jrebirth.forge.utils.PluginUtils.determinePackageAvailability;
import static org.jrebirth.forge.utils.PluginUtils.firstLetterCaps;
import static org.jrebirth.forge.utils.PluginUtils.installDependencies;
import static org.jrebirth.forge.utils.PluginUtils.jrebirthCoreDependency;
import static org.jrebirth.forge.utils.PluginUtils.jrebirthPresentationDependency;
import static org.jrebirth.forge.utils.PluginUtils.messages;
import static org.jrebirth.forge.utils.ProfileHelper.setupMavenProjectProfiles;

/**
 * The main plugin for JRebirth.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * @author Guruprasad Shenoy <gpshenoy@gmail.com>
 */
@Alias("jrebirth")
@Help("A Forge addon to enable and work on JRebirth framework.")
@RequiresFacet({ DependencyFacet.class, JavaSourceFacet.class, ResourceFacet.class, JRebirthFacet.class })
@RequiresProject
public class JRebirthPlugin implements Plugin {

    /** The shell prompt. */
    @Inject
    private ShellPrompt shellPrompt;

    /** The shell. */
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
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TemplateException the template exception
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

            createJNLPConfiguration(this.project);
            ShellMessages.warn(out, messages.getMessage("jnlp.dependency.is.setup"));

            setupMavenProjectProfiles(this.project, metadata.getTopLevelPackage(), metadata.getProjectName());

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
     * @param allDetails the all details
     * @param jrebirthVersion the jrebirth version
     */
    @DefaultCommand
    public void defaultCommand(final PipeOut out,
            @Option(name = "all", shortName = "a", flagOnly = true, defaultValue = "false", help = "Finds and displays all the detail regardign this JRebirth project.")
            final boolean allDetails,
            @Option(name = "jrebirthVersion", shortName = "jrv", flagOnly = true, defaultValue = "false", help = "Finds and displays JRebirht version added to this project.")
            final boolean jrebirthVersion) {

        final DependencyFacet dFacet = this.project.getFacet(DependencyFacet.class);

        if (allDetails || jrebirthVersion)
        {
            ShellMessages.info(out, "JRebirth Version : " + dFacet.getDirectDependency(jrebirthCoreDependency()).getVersion());
        }
        else {
            if (this.project.hasFacet(JRebirthFacet.class)) {
                ShellMessages.info(out, messages.getMessage("jrebirth.is.installed"));
            } else {
                ShellMessages.warn(out, messages.getMessage("jrebirth.is.not.installed"));
            }
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
    public void createMVC(
            final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of the UI Group to be created.")
            final String name,
            @Option(name = "controllerGenerate", shortName = "cg", required = false, flagOnly = true, defaultValue = "true", help = "If true, Controller will be generated for the MVC.")
            final boolean controllerGenerate,
            @Option(name = "beanGenerate", shortName = "bg", required = false, flagOnly = true, defaultValue = "true", help = "If true, Bean will be generated for the MVC.")
            final boolean beanGenerate,
            @Option(name = "fxmlGenerate", shortName = "fg", required = false, flagOnly = true, defaultValue = "false", help = "If true, FXML document will be generated for the MVC in resource folder.")
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
     * @param allResource the all resource
     * @param colorGenerate the color generate
     * @param fontGenerate the font generate
     * @param imageGenerate the image generate
     */
    @Command(value = "resource-create", help = "Create a resource for the given name")
    public void createResource(final PipeOut out,
            @Option(name = "all", shortName = "a", required = true, help = "Generate all the resource")
            final boolean allResource,
            @Option(name = "colorGenerate", shortName = "cg", flagOnly = true, required = false, help = "Generate resource for Colors")
            final boolean colorGenerate,
            @Option(name = "fontGenerate", shortName = "fg", flagOnly = true, required = false, help = "Generate resource for Fonts")
            final boolean fontGenerate,
            @Option(name = "imageGenerate", shortName = "ig", flagOnly = true, required = false, help = "Generate resource for Images")
            final boolean imageGenerate) {
        createResourceFiles(out, allResource, colorGenerate, fontGenerate, imageGenerate);
    }

    /**
     * Add gray color to color resource.
     * 
     * @param out the out
     * @param colorName the color name
     * @param hexValue the hex value
     */
    @Command(value = "color-add-gray", help = "Add gray color to color resource")
    public void colorAddGray(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of color object.")
            final String colorName,
            @Option(name = "gray", shortName = "g", required = true, help = "Color's gray value [0.0-1.0]")
            final String grayValue,
            @Option(name = "opacity", shortName = "o", required = false, defaultValue = "1.0", help = "Color's opacity value")
            final double opacityValue) {

        final String fieldDefinition = "    /** Color constant for {}. */\n    ColorItem {} = create(new GrayColor(\"" + grayValue.toUpperCase() + "\", " + opacityValue + "));\n\n";
        ResourceUtils.manageColorResource(project, shell, out, colorName, fieldDefinition);
    }

    /**
     * Add HSB color to color resource.
     * 
     * @param out the out
     * @param colorName the color name
     * @param hsbValue the Hue,Saturation,Brightness values separated by comma
     */
    @Command(value = "color-add-hsb", help = "Add HSB color to color resource")
    public void colorAddHSB(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of color object.")
            final String colorName,
            @Option(name = "hsb", shortName = "h", required = true, help = "Color's hue,saturation, brightness [0.0-360.0],[0.0-1.0],[0.0-1.0]")
            final String hsbValue,
            @Option(name = "opacity", shortName = "o", required = false, defaultValue = "1.0", help = "Color's opacity value")
            final double opacityValue) {

        final String fieldDefinition = "    /** Color constant for {}. */\n    ColorItem {} = create(new HSBColor(\"" + hsbValue.toUpperCase() + "\", " + opacityValue + "));\n\n";
        ResourceUtils.manageColorResource(project, shell, out, colorName, fieldDefinition);
    }

    /**
     * Add RGB 01 color to color resource.
     * 
     * @param out the out
     * @param colorName the color name
     * @param rgb the Red,Green,Blue values separated by comma [0.0-1.0]
     */
    @Command(value = "color-add-rgb01", help = "Add RGB 01 color to color resource")
    public void colorAddRGB01(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of color object.")
            final String colorName,
            @Option(name = "rgb", shortName = "r", required = true, help = "Color's Red,Green,Blue [0.0-1.0],[0.0-1.0],[0.0-1.0]")
            final String rgbValue,
            @Option(name = "opacity", shortName = "o", required = false, defaultValue = "1.0", help = "Color's opacity value")
            final double opacityValue) {

        final String fieldDefinition = "    /** Color constant for {}. */\n    ColorItem {} = create(new RGB01Color(\"" + rgbValue.toUpperCase() + "\", " + opacityValue + "));\n\n";
        ResourceUtils.manageColorResource(project, shell, out, colorName, fieldDefinition);
    }

    /**
     * Add RGB 255 color to color resource.
     * 
     * @param out the out
     * @param colorName the color name
     * @param rgb the Red,Green,Blue values separated by comma [0-255]
     */
    @Command(value = "color-add-rgb255", help = "Add RGB 255 color to color resource")
    public void colorAddRGB255(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of color object.")
            final String colorName,
            @Option(name = "rgb", shortName = "r", required = true, help = "Color's Red,Green,Blue [0-255],[0-255],[0-255]")
            final String rgbValue,
            @Option(name = "opacity", shortName = "o", required = false, defaultValue = "1.0", help = "Color's opacity value")
            final double opacityValue) {

        final String fieldDefinition = "    /** Color constant for {}. */\n    ColorItem {} = create(new RGB255Color(\"" + rgbValue.toUpperCase() + "\", " + opacityValue + "));\n\n";
        ResourceUtils.manageColorResource(project, shell, out, colorName, fieldDefinition);
    }

    /**
     * Add web color to color resource.
     * 
     * @param out the out
     * @param colorName the color name
     * @param hexValue the hex value
     */
    @Command(value = "color-add-web", help = "Add web color to color resource")
    public void colorAddWeb(final PipeOut out,
            @Option(name = "name", shortName = "n", required = true, help = "Name of color object.")
            final String colorName,
            @Option(name = "hex", shortName = "h", required = true, help = "Color's hex value")
            final String hexValue,
            @Option(name = "opacity", shortName = "o", required = false, defaultValue = "1.0", help = "Color's opacity value")
            final double opacityValue) {

        final String field = "    /** Color constant for {}. */\n    ColorItem {} = create(new WebColor(\"" + hexValue.toUpperCase() + "\", " + opacityValue + "));\n\n";
        ResourceUtils.manageColorResource(project, shell, out, colorName, field);
    }

    /**
     * Application Configure command.
     * 
     * @param out the out
     * @param key the key
     * @param value the value
     * @param showAll the show all
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Command(value = "app-config", help = "Create a service for the given name")
    public void appConfig(final PipeOut out,
            @Option(name = "key", shortName = "k", completer = AppPropertyCompleter.class, help = "Key name in the property file.")
            final String key,
            @Option(name = "value", shortName = "v", help = "New value for the key")
            final String value,
            @Option(name = "showAll", shortName = "a", flagOnly = true, defaultValue = "false", help = "Show all the application properties")
            final boolean showAll) throws IOException {

        final ResourceFacet resourceFacet = this.project.getFacet(ResourceFacet.class);
        final Properties prop = new Properties();
        try {
            prop.load(resourceFacet.getResource("jrebirth.properties").getResourceInputStream());
        } catch (final IOException e) {
            ShellMessages.error(out, messages.getMessage("unable.to.read.properties.file"));
            return;
        }

        if (value == null && key != null) {
            out.println(key + " = " + prop.getProperty(key));
        }
        if (value != null) {

            if (key == null) {
                ShellMessages.error(out, messages.getMessage("key.is.not.set"));
                return;
            }

            if (prop.containsKey(key) == false) {
                ShellMessages.info(out, messages.getMessage("given.key.not.found"));
            }

            prop.setProperty(key, value);
            final File fxmlFile = resourceFacet.createResource(new char[0], "jrebirth.properties").getUnderlyingResourceObject();
            final FileWriter wri = new FileWriter(fxmlFile);
            prop.store(wri, "");
        }

        if (showAll) {
            final Enumeration<Object> enu = prop.keys();
            String keyName = "";
            while (enu.hasMoreElements()) {
                keyName = (String) enu.nextElement();
                out.println(keyName + " = " + prop.getProperty(keyName));
            }
        }
    }

    /**
     * Creates Java files for user interface mainly for Model, Controller and View.
     * 
     * @param out the out
     * @param type the type
     * @param name the name
     * @param controllerGenerate the controller generate
     * @param beanGenerate the bean generate .
     * @param fxmlGenerate the fxml generate
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

            createFullPackageIfNotExist(resourceDir, settings.getImportPackage() + ".ui.fxml", out);
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
     * @param commandType the command type
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
            ShellMessages.error(out, messages.getMessage("could.not.create.file"));
        }
    }

    /**
     * Creates the resource files.
     * 
     * @param out the out
     * @param allResource the all resource
     * @param colorGenerate the color generate
     * @param fontGenerate the font generate
     * @param imageGenerate the image generate
     */
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
                ShellMessages.error(out, messages.getMessage("file.already.exist", finalName));
            }

        } catch (final Exception e) {
            e.printStackTrace();
            ShellMessages.error(out, messages.getMessage("could.not.create.file"));
        }
    }

}
