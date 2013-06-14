package org.jrebirth.forge;

import static junit.framework.Assert.assertTrue;
import static org.jrebirth.forge.utils.Constants.javafxDependency;
import static org.jrebirth.forge.utils.Constants.jrebirthCoreDependency;
import static org.jrebirth.forge.utils.Constants.slf4jDependency;

import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.junit.Test;


public class JRebirthPluginSetupTest extends AbstractJRebirthPluginTest {
    
  
    @Test
    public void testSetupHasFaced() throws Exception {
        assertTrue(project.hasFacet(JRebirthFacet.class));
    }
    
    @Test
    public void testSetupHasPropertiesFile() throws Exception {
        
        ResourceFacet resources = project.getFacet(ResourceFacet.class);
        FileResource<?> applicationContext = resources.getResource("jrebirth.properties");
        assertTrue(applicationContext.exists());

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
        DirectoryResource directory = resourceFacet.getResourceFolder();
        assertTrue(directory.getChildDirectory("fonts").exists());
    }
    

}
