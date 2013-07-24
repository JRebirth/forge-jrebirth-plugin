package org.jrebirth.forge;

import static org.jrebirth.forge.utils.PluginUtils.javafxDependency;
import static org.jrebirth.forge.utils.PluginUtils.jrebirthCoreDependency;
import static org.jrebirth.forge.utils.PluginUtils.slf4jDependency;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Test cases for JRebirth Setup command.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * 
 */
@FixMethodOrder(MethodSorters.DEFAULT)
public class JRebirthPluginSetupTest extends AbstractJRebirthPluginTest {

    @Test
    public void testProjectNotNull() throws Exception {
        assertNotNull(this.project);
    }

    @Test
    public void testSetupHasFaced() throws Exception {
        assertTrue(this.project.hasFacet(JRebirthFacet.class));
    }

    @Test
    public void testSetupHasPropertiesFile() throws Exception {

        final ResourceFacet resources = this.project.getFacet(ResourceFacet.class);
        assertTrue(resources.getResource("jrebirth.properties").exists());
    }

    @Test
    public void testHasJRebirthCoreDependency() {
        final DependencyFacet dependencyFacet = this.project
                .getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet
                .hasDirectDependency(jrebirthCoreDependency()));
    }

    @Test
    public void testHasJavaFXDependency() {
        final DependencyFacet dependencyFacet = this.project
                .getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet.hasDirectDependency(javafxDependency()));
    }

    @Test
    public void testHasSlf4jDependency() {
        final DependencyFacet dependencyFacet = this.project
                .getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet.hasDirectDependency(slf4jDependency()));
    }

    @Test
    public void testHasFontsFolderInResource() {
        final ResourceFacet resourceFacet = this.project
                .getFacet(ResourceFacet.class);
        assertTrue(resourceFacet.getResourceFolder().getChildDirectory("fonts")
                .exists());
    }

    @Test
    public void testHasImagesFolderInResource() {
        final ResourceFacet resourceFacet = this.project
                .getFacet(ResourceFacet.class);
        assertTrue(resourceFacet.getResourceFolder()
                .getChildDirectory("images").exists());
    }

    @Test
    public void testHasStylesFolderInResource() {
        final ResourceFacet resourceFacet = this.project
                .getFacet(ResourceFacet.class);
        assertTrue(resourceFacet.getResourceFolder()
                .getChildDirectory("styles").exists());
    }

    @Test
    public void testHasJnlpFolder() {
        final DirectoryResource jnlpDirResource = this.project.getProjectRoot()
                .getChildDirectory("src/main/jnlp");
        assertTrue(jnlpDirResource.exists());
    }

    @Test
    public void testSnapshotRepoNotPresent() {
        final DependencyFacet dependencyFacet = this.project
                .getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet
                .hasRepository("http://repo.jrebirth.org/libs-snapshot") == false);

    }

    @Test
    public void testSnapshotRepoExistsAfterRunningAddSnapshotRepoCommand() throws Exception {
        executeSetupWithAddSnapshotRepo();
        final DependencyFacet dependencyFacet = this.project
                .getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet
                .hasRepository("http://repo.jrebirth.org/libs-snapshot") == true);
    }

    @Test
    public void testSnapshotRepoNotExistsAfterRunningAddAndRemoveSnapshotRepoCommand() throws Exception {
        executeSetupWithAddSnapshotRepo();
        executeSetupWithRemoveSnapshotRepo();
        final DependencyFacet dependencyFacet = this.project
                .getFacet(DependencyFacet.class);
        assertTrue(dependencyFacet
                .hasRepository("http://repo.jrebirth.org/libs-snapshot") == false);
    }

    private void executeSetupWithAddSnapshotRepo() throws Exception {
        getShell().execute("jrebirth setup --addSnapshotRepository");
    }

    private void executeSetupWithRemoveSnapshotRepo() throws Exception {
        getShell().execute("jrebirth setup --removeSnapshotRepository");
    }

}
