package org.jrebirth.forge;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;

import freemarker.template.utility.Constants;

public class AbstractJRebirthPluginTest extends AbstractShellTest {
    
    protected Project project = null;

    @Deployment
    public static JavaArchive getDeployment() {
        return AbstractShellTest.getDeployment().addPackages(true,JRebirthPlugin.class.getPackage())
                .addPackages(true,Constants.class.getPackage());
    }

    @Before
    public void initializeJRebirthFacesProject() throws Exception {
        project = initializeJavaProject();
        getShell().execute("rebirth setup");
        
    }


}
