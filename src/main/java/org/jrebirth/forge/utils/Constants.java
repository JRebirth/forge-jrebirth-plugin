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
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaInterface;
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

    private static Configuration cfg = new Configuration();
    static {
        cfg.setClassForTemplateLoading(Constants.class, "../../../../template");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }

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

        FileWriter fileWriter = null;
        String template = "";
        Map<String, TemplateSettings> context = new HashMap<String, TemplateSettings>();

        context.put("settings", settings);

        try {
            switch (fileType) {

                case MVC:

                    if ("Model".equals(suffix)) {
                        template = "TemplateModel.ftl";
                    } else if ("View".equals(suffix)) {
                        template = "TemplateView.ftl";
                    } else if ("Controller".equals(suffix)) {
                        template = "TemplateController.ftl";
                    }
                    createJavaFileUsingTemplate(project, template, context);
                    break;
                case FXML:
                    final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
                    File fxmlFile = resourceFacet.createResource(new char[0], settings.getImportPackage().replaceAll(".", "/") + "/ui/" + settings.getName() + ".fxml").getUnderlyingResourceObject();
                    createResourceFileUsingTemplate(project, "TemplateFXML.ftl", fxmlFile, context);
                    break;
                case COMMAND:
                    createJavaFileUsingTemplate(project, "TemplateCommand.ftl", context);
                    break;
                case SERVICE:
                    if (!settings.getName().contains("service") && !settings.getName().contains("Service")) {
                        context.get("settings").setName(settings.getName().concat("Service"));
                    }
                    createJavaFileUsingTemplate(project, "TemplateService.ftl", context);
                    break;
                case RESOURCE:
                    createJavaFileUsingTemplate(project, "TemplateResource.ftl", context);
                    break;
                case BEAN:
                    context.get("settings").setTopLevelPacakge(settings.getImportPackage() + ".beans");
                    createJavaFileUsingTemplate(project, "TemplateBean.ftl", context);
                    break;
                default:
                    break;
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

    public static void createResourceFileUsingTemplate(final Project project, final String templateFileName, final File fileObj, final Map<String, TemplateSettings> context) throws IOException,
            TemplateException {

        Template template = null;
        template = cfg.getTemplate(templateFileName);

        FileWriter fileWriter = new FileWriter(fileObj);
        template.process(context, fileWriter);
        fileWriter.flush();

    }

    public static void createJavaFileUsingTemplate(final Project project, final String templateFileName, final Map<String, TemplateSettings> context) throws IOException,
            TemplateException {
        final StringWriter writer = new StringWriter();
        Template template = null;
        template = cfg.getTemplate(templateFileName);

        final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
        template.process(context, writer);
        writer.flush();

        final JavaClass javaClass = JavaParser.parse(JavaClass.class, writer.toString());
        java.saveJavaSource(javaClass);

    }

    public static void createJavaInterfaceUsingTemplate(final Project project, final String templateFileName, final Map<String, TemplateSettings> context) throws IOException,
            TemplateException {
        final StringWriter writer = new StringWriter();
        Template template = null;
        template = cfg.getTemplate(templateFileName);

        final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
        template.process(context, writer);
        writer.flush();

        final JavaInterface javaInterface = JavaParser.parse(JavaInterface.class, writer.toString());
        java.saveJavaSource(javaInterface);

    }

    public static void createJavaEnumUsingTemplate(final Project project, final String templateFileName, final Map<String, TemplateSettings> context) throws IOException,
            TemplateException {
        final StringWriter writer = new StringWriter();
        Template template = null;
        template = cfg.getTemplate(templateFileName);

        final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
        template.process(context, writer);
        writer.flush();

        final JavaEnum javaEnum = JavaParser.parse(JavaEnum.class, writer.toString());
        java.saveJavaSource(javaEnum);

    }

}
