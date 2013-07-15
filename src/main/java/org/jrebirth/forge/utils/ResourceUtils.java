package org.jrebirth.forge.utils;

import java.io.FileNotFoundException;

import org.jrebirth.forge.utils.PluginUtils.CreationType;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.util.Packages;

import static org.jrebirth.forge.utils.PluginUtils.messages;

public class ResourceUtils {

    /**
     * Add a color resource.
     * 
     * @param out the out
     * @param colorName the name of the color variable
     * @param fieldString the field definition
     */
    public static void manageColorResource(final Project project, final Shell shell, final PipeOut out, final String colorName, final String fieldString) {
        DirectoryResource directory = null;
        final MetadataFacet metadata = project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();
        final String topLevelPackage = metadata.getTopLevelPackage();

        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
        if (directory.isDirectory() == false || directory.getChild(metadata.getProjectName() + "Colors.java").exists() == false) {
            try {
                ShellMessages.info(out, messages.getMessage("color.is.not.created"));
                shell.execute("jrebirth resource-create --all false --colorGenerate");
            } catch (final Exception e) {
                ShellMessages.error(out, messages.getMessage("unable.to.create.color"));
            }
        }
        final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
        final JavaInterface jInterface = JavaParser.parse(JavaInterface.class, directory.getChild(metadata.getProjectName() + "Colors.java").getResourceInputStream());

        final String capsColorName = StringUtils.camelCaseToUnderscore(colorName);

        if (jInterface.hasField(capsColorName) == false) {

            jInterface.addField(fieldString.replaceAll("\\{\\}", capsColorName));

            try {
                java.saveJavaSource(jInterface);
            } catch (final FileNotFoundException e) {
                ShellMessages.error(out, messages.getMessage("unable.to.save.file", capsColorName));
            }
        }
        else {
            ShellMessages.warn(out, messages.getMessage("color.constant.already.exist", capsColorName));
        }
    }
}
