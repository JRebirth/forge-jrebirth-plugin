package org.jrebirth.forge;

import static org.jrebirth.forge.utils.PluginUtils.firstLetterCaps;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jrebirth.forge.completer.AppPropertyCompleter;
import org.jrebirth.forge.helper.MavenProfilePluginHelper;
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
    protected MetadataFacet metadata;

    protected String topLevelPackage = null;
    protected String projectName = null;

    @Deployment
    public static JavaArchive getDeployment() {
        return AbstractShellTest.getDeployment().addPackage(JRebirthPlugin.class.getPackage())
                .addPackage(PluginUtils.class.getPackage())
                .addPackage(MavenProfilePluginHelper.class.getPackage())
                .addPackage(AppPropertyCompleter.class.getPackage());
    }

    @Before
    public void initializeJRebirthFacesProject() throws Exception {
        this.project = initializeJavaProject();
        this.metadata = this.project.getFacet(MetadataFacet.class);
        this.topLevelPackage = this.metadata.getTopLevelPackage();
        this.projectName = firstLetterCaps(this.metadata.getProjectName());
        getShell().execute("jrebirth setup");
    }

    protected DirectoryResource getJavaSourceDirResource() {
        return this.project.getFacet(JavaSourceFacet.class).getSourceFolder();
    }

    protected boolean isResourcePackageExists(final String packageName) {
        return getJavaSourceDirResource().getChildDirectory(Packages.toFileSyntax(packageName)).isDirectory();
    }

    protected JavaInterface parseJavaInterface(final String packageName, final String javaFileName) {
        DirectoryResource directory = null;
        directory = getJavaSourceDirResource().getChildDirectory(Packages.toFileSyntax(packageName));
        return JavaParser.parse(JavaInterface.class, directory.getChild(javaFileName).getResourceInputStream());
    }

}
