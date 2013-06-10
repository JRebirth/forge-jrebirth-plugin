package org.jrebirth.forge;

import static junit.framework.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

public class JRebirthPluginTest extends AbstractShellTest {

    @Inject
    private DependencyResolver resolver;

    @Deployment
    public static JavaArchive getDeployment() {
        return AbstractShellTest.getDeployment().addPackages(true, JRebirthPlugin.class.getPackage());
    }

    private Project initializeJRebirthFacesProject() throws Exception {
        final Project p = initializeJavaProject();

        queueInputLines("9");
        getShell().execute("rebirth setup");
        return p;
    }

    @Test
    public void testSetup() throws Exception {

        initializeJRebirthFacesProject();
        final Project project = initializeJRebirthFacesProject();

        assertNotNull(this.resolver);

        assertNotNull(project.hasFacet(JRebirthFacet.class));

    }

    @Test
    public void testCreateMV() throws Exception {

        initializeJRebirthFacesProject();
        final Project project = initializeJRebirthFacesProject();
        final MetadataFacet metadata = project.getFacet(MetadataFacet.class);

        getShell().execute("rebirth create-mv --name Student");

        final DirectoryResource sourceFolder = project.getFacet(JavaSourceFacet.class).getSourceFolder();
        final DirectoryResource directory = sourceFolder.getChildDirectory(Packages.toFileSyntax(metadata.getTopLevelPackage() + ".ui.raj"));

        System.out.println(getShell().getCurrentResource().getParent().getName());
        System.out.println(metadata.getTopLevelPackage());
        System.out.println(sourceFolder.toString());
        System.out.println(directory.isDirectory());
        System.out.println(directory.getFullyQualifiedName());

        // assertTrue(directory.isDirectory());

    }
}
