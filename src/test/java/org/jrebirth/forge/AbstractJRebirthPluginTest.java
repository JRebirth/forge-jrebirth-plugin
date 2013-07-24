package org.jrebirth.forge;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jrebirth.forge.utils.PluginUtils;
import org.junit.Before;

/**
 * Abstract TestCase for JRebirth
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * 
 */
public abstract class AbstractJRebirthPluginTest extends AbstractShellTest {

    protected Project project = null;

    @Deployment
    public static JavaArchive getDeployment() {
        return AbstractShellTest.getDeployment().addPackage(JRebirthPlugin.class.getPackage())
                .addPackage(PluginUtils.class.getPackage());
    }

    @Before
    public void initializeJRebirthFacesProject() throws Exception {
        project = initializeJavaProject();
        getShell().execute("jrebirth setup");
    }

}
