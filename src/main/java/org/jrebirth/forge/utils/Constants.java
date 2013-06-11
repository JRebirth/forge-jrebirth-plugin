/**
 * Get more info at : www.jrebirth.org . Copyright JRebirth.org © 2011-2013 Contact : sebastien.bordes@jrebirth.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.jrebirth.forge.utils;

import java.util.List;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;

/**
 * JRebirth Constants 
 * 
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
public class Constants {
    
    /** The Constant TEMPLATE_KEY_PACKAGE. */
    public static final String TEMPLATE_KEY_PACKAGE = "package";
    
    /** The Constant TEMPLATE_KEY_NAME. */
    public static final String TEMPLATE_KEY_NAME = "name";
    
    /** The Constant TEMPLATE_KEY_PACKAGEIMPORT. */
    public static final String TEMPLATE_KEY_PACKAGEIMPORT = "packageImport";
    
    /** The Constant TEMPLATE_UNICODE. */
    public static final String TEMPLATE_UNICODE = "UTF-8";
    
    /** The Constant PACKAGE_DELIMITER. */
    public static final String PACKAGE_DELIMITER = ".";
        
    /**
     * The Enum CreationType.
     */
    public static enum CreationType {

        /** The mv. */
        MV(".ui"), /** The mvc. */
        MVC(".ui"), /** The command. */
        COMMAND(".command"), /** The service. */
        SERVICE(".service"), /** The resource. */
        RESOURCE(".resource"), /** The fxml. */
        FXML(".ui.fxml"), /** The bean. */
        BEAN(".beans");

       private String packageName;

        private CreationType(final String packageName) {
            this.packageName = packageName;
        }

        public String getPackageName() {
            return this.packageName;
        }
    }
    
    /**
     * Jrebirth presentation dependency.
     *
     * @return the dependency builder
     */
    public static DependencyBuilder jrebirthPresentationDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("presentation");
    }
      

    /**
     * Install dependencies.
     *
     * @param project the project
     * @param shell the shell
     * @param writer the writer
     * @param dependency the dependency
     * @param askVersion the ask version
     */
    public static void installDependencies(final Project project, final ShellPrompt shell,final  ShellPrintWriter writer, final DependencyBuilder dependency, final boolean askVersion) {
        DependencyFacet dependencyFacet;
        dependencyFacet = project.getFacet(DependencyFacet.class);

        final List<Dependency> versions = dependencyFacet.resolveAvailableVersions(dependency);
        if (askVersion) {
            final Dependency dep = shell.promptChoiceTyped("What version do you want to install?", versions);
            dependency.setVersion(dep.getVersion());
        }
        dependencyFacet.addDirectDependency(dependency);

        writer.println(ShellColor.GREEN, dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion() + " is added to the dependency.");

    }
    

}
