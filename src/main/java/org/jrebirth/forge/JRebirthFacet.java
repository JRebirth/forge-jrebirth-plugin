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
package org.jrebirth.forge;

import java.util.List;
import javax.inject.Inject;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 *
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@Alias("org.jrebirth")
@RequiresFacet({DependencyFacet.class})
public class JRebirthFacet extends BaseFacet {

    @Inject
    private ShellPrompt shell;
    private DependencyFacet dependencyFacet;
    @Inject
    private ShellPrintWriter writer;

    @Override
    public boolean install() {
        dependencyFacet = project.getFacet(DependencyFacet.class);

        dependencyFacet.addRepository("JRebirth Maven Repository", "http://repo.jrebirth.org/libs-release");
        dependencyFacet.addRepository("JRebirth Maven Snapshot Repository", "http://repo.jrebirth.org/libs-snapshot");

        installDependencys(jrebirthCoreDependency(), false);
        installDependencys(javafxDependency(), false);

        return true;
    }

    @Override
    public boolean isInstalled() {
        if (project.hasFacet(JRebirthFacet.class)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean uninstall() {
        dependencyFacet = project.getFacet(DependencyFacet.class);
        dependencyFacet.removeDependency(jrebirthCoreDependency());
        dependencyFacet.removeDependency(javafxDependency());
        dependencyFacet.removeRepository("JRebirth Maven Repository");
        return true;
    }

    private void installDependencys(DependencyBuilder dependency, boolean askVersion) {

        List<Dependency> versions = dependencyFacet.resolveAvailableVersions(dependency);
        if (askVersion) {
            Dependency dep = shell.promptChoiceTyped("What version do you want to install?", versions);
            dependency.setVersion(dep.getVersion());
        }
        dependencyFacet.addDirectDependency(dependency);

        writer.println(ShellColor.GREEN, dependency.getArtifactId() + ":" + dependency.getGroupId() + ":" + dependency.getVersion() + " is added to the dependency.");

    }

    private static DependencyBuilder jrebirthCoreDependency() {
        return DependencyBuilder.create().setGroupId("org.jrebirth").setArtifactId("core").setVersion("0.7.4-SNAPSHOT");
    }

    private static DependencyBuilder javafxDependency() {
        return DependencyBuilder.create().setGroupId("javafx").setArtifactId("jfxrt").setVersion("2.2").
                setScopeType("system").setSystemPath("${java.home}/lib/jfxrt.jar");
    }
}
