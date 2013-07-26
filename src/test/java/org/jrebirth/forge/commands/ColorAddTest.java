package org.jrebirth.forge.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.util.Packages;
import org.jrebirth.forge.AbstractJRebirthPluginTest;
import org.jrebirth.forge.utils.PluginUtils.CreationType;
import org.junit.Test;

/**
 * Test cases for color-add command.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * 
 */
public class ColorAddTest extends AbstractJRebirthPluginTest {

    @Test
    public void testResourceIsNotExisitInStartup() {
        DirectoryResource directory = null;
        final DirectoryResource sourceFolder = this.project.getFacet(JavaSourceFacet.class).getSourceFolder();
        directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(this.topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
        assertFalse(directory.isDirectory());
    }

    @Test
    public void testForExistanceOfResourcePackage() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        assertTrue(isResourcePackageExists(this.topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
    }

    @Test
    public void testForExistanceOfColorClass() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        DirectoryResource directory = null;
        directory = getJavaSourceDir().getChildDirectory(Packages.toFileSyntax(this.topLevelPackage + CreationType.RESOURCE.getPackageName() + "."));
        assertTrue(directory.getChild(this.projectName + "Colors.java").exists());
    }

    @Test
    public void testForExistanceOfTheVariableCreated() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        final JavaInterface jInterface = parseJavaInterface(this.topLevelPackage + CreationType.RESOURCE.getPackageName() + ".", this.projectName + "Colors.java");
        assertTrue(jInterface.hasField("WINDOW_BORDER"));
    }

    @Test
    public void testForVariableAlreadyExistWhenYes() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        queueInputLines("y");
        executeColorAddWindowBorderWebCCCCCC();
        final JavaInterface jInterface = parseJavaInterface(this.topLevelPackage + CreationType.RESOURCE.getPackageName() + ".", this.projectName + "Colors.java");
        assertTrue(jInterface.hasField("WINDOW_BORDER"));
    }

    @Test
    public void testForVariableAlreadyExistWhenNo() throws Exception {
        executeColorAddWindowBorderWebCCCCCC();
        queueInputLines("n");
        executeColorAddWindowBorderWebCCCCCC();
        final JavaInterface jInterface = parseJavaInterface(this.topLevelPackage + CreationType.RESOURCE.getPackageName() + ".", this.projectName + "Colors.java");
        assertTrue(jInterface.hasField("WINDOW_BORDER"));
    }

    /* *********************************** *************************************** */

    private void executeColorAddWindowBorderWebCCCCCC() throws Exception {
        getShell().execute("jrebirth color-add --name windowBorder --value CCCCCC --colorType web");
    }

}
