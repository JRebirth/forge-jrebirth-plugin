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

/**
 * The Class ResourceUtils.
 */
public class ResourceUtils {
    
    
    /** The Constant COLOR_RGB01_PATTERN. */
    private static final String COLOR_RGB01_PATTERN = ""; //0.0-1.0,0.0-1.0,0.0-1.0
    
    /** The Constant COLOR_HSB_PATTERN. */
    private static final String COLOR_HSB_PATTERN = ""; //0.0-360.0,0.0-1.0,0.0-1.0
    
    /** The Constant COLOR_RGB255_PATTERN. */
    private static final String COLOR_RGB255_PATTERN = ""; //0-255,0-255,0-255
    
    /** The Constant COLOR_GRAY_PATTERN. */
    private static final String COLOR_GRAY_PATTERN = ""; //[0.0-1.0]
    
    /** The Constant COLOR_WEB_PATTERN. */
    private static final String COLOR_WEB_PATTERN = ""; //[0-9A-F]{6}
    
    
    /**
     * Validate color value using type.
     * 
     * @param colorType the color type
     * @param colorValue the color value
     * @return true, if successful
     */
    public static boolean validateColorValueUsingType (final String colorType, final String colorValue) {
        //TODO: User Color pattern and check the colorValue
        return false;
    }
        

    /**
     * Add a color resource.
     * 
     * @param project the project
     * @param shell the shell
     * @param out the out
     * @param colorName the name of the color variable
     * @param colorValue the color value
     * @param colorType the color type
     * @param opacityValue the opacity value
     */
    public static void manageColorResource(final Project project, final Shell shell, final PipeOut out, final String colorName, final String colorValue, final String colorType,
            final double opacityValue) {
        DirectoryResource directory = null;
        final MetadataFacet metadata = project.getFacet(MetadataFacet.class);
        final DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();
        final String topLevelPackage = metadata.getTopLevelPackage();
        
        String type = ((colorType.equalsIgnoreCase("web")? "Web":colorType.toUpperCase() ));
        final String importString = "org.jrebirth.core.resource.color." +type+"Color";

        final String fieldDefinition = " /** Color constant for {}. */\n    ColorItem {} = create(new " + type + "Color(\"" + colorValue.toUpperCase() + "\", " + opacityValue
                + "));\n\n";

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
        
        if(jInterface.hasImport(importString) == false)
            jInterface.addImport(importString);
            

        if (jInterface.hasField(capsColorName) == false) {

            jInterface.addField(fieldDefinition.replaceAll("\\{\\}", capsColorName));

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
