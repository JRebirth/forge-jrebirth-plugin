package org.jrebirth.forge;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNotNull;
import static org.jrebirth.forge.utils.Constants.javafxDependency;
import static org.jrebirth.forge.utils.Constants.jrebirthCoreDependency;
import static org.jrebirth.forge.utils.Constants.slf4jDependency;

import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.junit.Test;

public class JRebirthPluginSetupTest extends AbstractJRebirthPluginTest {

    @Test
    public void testProjectNotNull() throws Exception {
        assertNotNull(project);
    }

    @Test
    public void testSetupHasFaced() throws Exception {
        assertTrue(project.hasFacet(JRebirthFacet.class));
    }

    @Test
    public void testSetupHasPropertiesFile() throws Exception {

        ResourceFacet resources = project.getFacet(ResourceFacet.class);
        assertTrue(resources.getResource("jrebirth.properties").exists());
    }

    @Test
    public void testHasJRebirthCoreDependency() {
        DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet.hasDirectDependency(jrebirthCoreDependency()));
    }

    @Test
    public void testHasJavaFXDependency() {
        DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet.hasDirectDependency(javafxDependency()));
    }

    @Test
    public void testHasSlf4jDependency() {
        DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet.hasDirectDependency(slf4jDependency()));
    }

    @Test
    public void testHasFontsFolderInResource() {
        final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
        assertTrue(resourceFacet.getResourceFolder().getChildDirectory("fonts").exists());
    }

    @Test
    public void testHasImagesFolderInResource() {
        final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
        assertTrue(resourceFacet.getResourceFolder().getChildDirectory("images").exists());
    }

    @Test
    public void testHasStylesFolderInResource() {
        final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
        assertTrue(resourceFacet.getResourceFolder().getChildDirectory("styles").exists());
    }

}
