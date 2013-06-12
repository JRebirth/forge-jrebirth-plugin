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

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.PipeOut;

/**
 * JRebirth Constants.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
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
     * Determine file availabilty.
     *
     * @param project the project
     * @param directory the directory
     * @param type the type
     * @param finalName the final name
     * @param topLevelPackage the top level package
     * @param out the out
     * @param suffix the suffix
     * @param fileEnding the file ending
     */
    public static void determineFileAvailabilty(final Project project, final DirectoryResource directory, final CreationType type, final String finalName, final String topLevelPackage,
            final PipeOut out, final String suffix, final String fileEnding) {
        if (directory != null && directory.getChild(finalName + fileEnding).exists() == false) {
            generateFile(project, type, finalName, suffix, topLevelPackage);

        } else {
            out.println(ShellColor.RED, "The file '" + finalName + "' already exists");
        }
    }

    /**
     * Generate file.
     *
     * @param project the project
     * @param fileType the file type
     * @param name the name
     * @param suffix the suffix
     * @param topLevelPackage the top level package
     * @throws ResourceNotFoundException the resource not found exception
     */
    public static void generateFile(final Project project, final CreationType fileType, final String name, final String suffix, final String topLevelPackage) throws ResourceNotFoundException
    {

        final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
        final StringWriter writer = new StringWriter();

        final VelocityContext context = new VelocityContext();

        context.put("name", name);
        context.put("packageImport", topLevelPackage);

        switch (fileType) {
            case MV:
            case MVC:

                context.put("package", topLevelPackage + fileType.getPackageName() + "." + name.toLowerCase(Locale.ENGLISH));

                if ("Model".equals(suffix)) {
                    Velocity.mergeTemplate("TemplateModel.vtl", TEMPLATE_UNICODE, context, writer);
                } else if ("View".equals(suffix)) {
                    Velocity.mergeTemplate("TemplateView.vtl", TEMPLATE_UNICODE, context, writer);
                } else if ("Controller".equals(suffix)) {
                    Velocity.mergeTemplate("TemplateController.vtl", TEMPLATE_UNICODE, context, writer);
                }

                break;
            case FXML:

                break;
            case COMMAND:
                context.put("package", topLevelPackage + fileType.getPackageName());
                Velocity.mergeTemplate("TemplateCommand.vtl", TEMPLATE_UNICODE, context, writer);
                break;
            case SERVICE:
                context.put("package", topLevelPackage + fileType.getPackageName());
                Velocity.mergeTemplate("TemplateService.vtl", TEMPLATE_UNICODE, context, writer);
                break;
            case RESOURCE:
                context.put("package", topLevelPackage + ".resource");
                Velocity.mergeTemplate("TemplateResource.vtl", TEMPLATE_UNICODE, context, writer);
                break;
            case BEAN:
                context.put("package", topLevelPackage + ".beans");
                Velocity.mergeTemplate("TemplateBean.vtl", TEMPLATE_UNICODE, context, writer);
                break;
            default:
                break;
        }

        final JavaClass javaClass = JavaParser.parse(JavaClass.class, writer.toString());
        try {
            java.saveJavaSource(javaClass);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
