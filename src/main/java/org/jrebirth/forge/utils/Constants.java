/**
 * Get more info at : www.jrebirth.org . Copyright JRebirth.org © 2011-2013 Contact : sebastien.bordes@jrebirth.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.jrebirth.forge.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.PipeOut;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * JRebirth Constants.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * @author Guruprasad Shenoy <gpshenoy@gmail.com>
 */
public final class Constants {

    /** The Constant TEMPLATE_KEY_PACKAGE. */
    public static final String TEMPLATE_KEY_PACKAGE = "package";

    /** The Constant TEMPLATE_KEY_NAME. */
    public static final String TEMPLATE_KEY_NAME = "name";

    /** The Constant TEMPLATE_KEY_PACKAGEIMPORT. */
    public static final String TEMPLATE_KEY_PACKAGEIMPORT = "packageImport";

    /** The Constant TEMPLATE_UNICODE. */
    public static final String TEMPLATE_UNICODE = "UTF-8";

    /** The Constant PACKAGE_DELIMITER. */
    public static final String PACKAGE_DELIMITER = ".";

    /** The resource bundle. */
    public static ResourceBundle resourceBundle = ResourceBundle.getBundle("ResourceBundle");

    /**
     * The Enum CreationType.
     */
    public static enum CreationType {

        /** The mv. */
        MV(".ui"), /** The mvc. */
        MVC(".ui"), /** The command. */
        COMMAND(".command"), /** The service. */
        SERVICE(".service"), /** The resource. */
        RESOURCE(".resource"), /** The fxml. */
        FXML(".ui.fxml"), /** The bean. */
        BEAN(".beans");

        /** The package name. */
        private String packageName;

        /**
         * Instantiates a new creation type.
         * 
         * @param packageName the package name
         */
        private CreationType(final String packageName) {
            this.packageName = packageName;
        }

        /**
         * Gets the package name.
         * 
         * @return the package name
         */
        public String getPackageName() {
            return this.packageName;
        }
    }

    /**
     * Private constructor.
     */
    private Constants() {
    }

    /**
     * Jrebirth presentation dependency.
     * 
     * @return the dependency builder
     */
    public static DependencyBuilder jrebirthPresentationDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("presentation");
    }

    /**
     * Jrebirth core dependency.
     * 
     * @return the dependency builder
     */
    public static DependencyBuilder jrebirthCoreDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("core").setVersion("0.7.4-SNAPSHOT");
    }

    /**
     * Javafx dependency.
     * 
     * @return the dependency builder
     */
    public static DependencyBuilder javafxDependency() {
        return DependencyBuilder.create().setGroupId("javafx").setArtifactId("jfxrt").setVersion("2.2").
                setScopeType("system").setSystemPath("${java.home}/lib/jfxrt.jar");
    }

    /**
     * Install dependencies.
     * 
     * @param project the project
     * @param shell the shell
     * @param writer the writer
     * @param dependency the dependency
     * @param askVersion the ask version
     */
    public static void installDependencies(final Project project, final ShellPrompt shell, final ShellPrintWriter writer, final DependencyBuilder dependency, final boolean askVersion) {
        DependencyFacet dependencyFacet;
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
     * Determine package availability.
     * 
     * @param beansDirectory the beans directory
     * @param out the out
     */
    public static boolean determinePackageAvailability(final DirectoryResource beansDirectory, final PipeOut out) {
        if (beansDirectory.isDirectory()) {
            out.println(ShellColor.RED, "Unable to Create package. The package '" + beansDirectory.toString() + "' is already found");
            return false;
        } else {
            beansDirectory.mkdir();
        }
        return true;
    }

    /**
     * Creates the package if not exist.
     * 
     * @param directory the directory
     * @param packageType the package type
     * @param out the out
     */
    public static void createPackageIfNotExist(final DirectoryResource directory, final String packageType, final PipeOut out) {

        if (directory.isDirectory() == false) {
            out.println(ShellColor.BLUE, "The " + packageType + " package does not exist. Creating it.");
            directory.mkdir();
        }
    }

    /**
     * Determine file availability.
     * 
     * @param project the project
     * @param directory the directory
     * @param type the type
     * @param finalName the final name
     * @param out the out
     * @param suffix the suffix
     * @param fileEnding the file ending
     * @param settings parameters of the template
     */
    public static void determineFileAvailabilty(final Project project, final DirectoryResource directory, final CreationType type, final String finalName,
            final PipeOut out, final String suffix, final String fileEnding, TemplateSettings settings) {
        if (directory != null && directory.getChild(finalName + fileEnding).exists() == false) {
            generateFile(project, type, suffix, settings);

        } else {
            out.println(ShellColor.RED, "The file '" + finalName + "' already exists");
        }
    }

    /**
     * Generate file.
     * 
     * @param project the project
     * @param fileType the file type
     * @param suffix the suffix
     * @param settings the parameters to be set in the template
     */
    public static void generateFile(final Project project, final CreationType fileType, final String suffix, TemplateSettings settings)
    {

        final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
        final StringWriter writer = new StringWriter();
        FileWriter fileWriter = null;
        Template template = null;

        Configuration cfg = new Configuration();

        cfg.setClassForTemplateLoading(Constants.class, "../../../../template");
        cfg.setObjectWrapper(new DefaultObjectWrapper());

        Map<String, Object> context = new HashMap<String, Object>();

        try {
            switch (fileType) {

                case MVC:

                    if ("Model".equals(suffix)) {

                        template = cfg.getTemplate("TemplateModel.ftl");

                    } else if ("View".equals(suffix)) {

                        template = cfg.getTemplate("TemplateView.ftl");

                    } else if ("Controller".equals(suffix)) {

                        template = cfg.getTemplate("TemplateController.ftl");

                    }

                    break;
                case FXML:

                    template = cfg.getTemplate("TemplateFXML.ftl");

                    break;
                case COMMAND:

                    template = cfg.getTemplate("TemplateCommand.ftl");

                    break;
                case SERVICE:

                    if (!settings.getName().contains("service") && !settings.getName().contains("Service")) {
                        settings.setName(settings.getName().concat("Service"));
                    }

                    template = cfg.getTemplate("TemplateService.ftl");

                    break;
                case RESOURCE:

                    template = cfg.getTemplate("TemplateResource.ftl");

                    break;
                case BEAN:

                    settings.setTopLevelPacakge(settings.getImportPackage() + ".beans");

                    template = cfg.getTemplate("TemplateBean.ftl");

                    break;
                default:
                    break;
            }

            context.put("settings", settings);

            if (fileType.equals(CreationType.FXML))
            {

                ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
                File fxmlFile = resourceFacet.createResource(new char[0], "/ui/fxml/" + settings.getName() + ".fxml").getUnderlyingResourceObject();

                fileWriter = new FileWriter(fxmlFile);
                template.process(context, fileWriter);
                fileWriter.flush();

            } else {
                template.process(context, writer);
                writer.flush();

                final JavaClass javaClass = JavaParser.parse(JavaClass.class, writer.toString());
                java.saveJavaSource(javaClass);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TemplateException te) {
            te.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ioe) {

                }
            }

        }

    }

}
