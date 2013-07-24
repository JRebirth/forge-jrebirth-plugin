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

import static org.jrebirth.forge.helper.MavenProfilePluginHelper.setupMavenProjectProfiles;
import static org.jrebirth.forge.utils.PluginUtils.createJNLPConfiguration;
import static org.jrebirth.forge.utils.PluginUtils.createJavaFileUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.createResourceFileUsingTemplate;
import static org.jrebirth.forge.utils.PluginUtils.firstLetterCaps;
import static org.jrebirth.forge.utils.PluginUtils.installDependencies;
import static org.jrebirth.forge.utils.PluginUtils.javafxDependency;
import static org.jrebirth.forge.utils.PluginUtils.jrebirthCoreDependency;
import static org.jrebirth.forge.utils.PluginUtils.messages;
import static org.jrebirth.forge.utils.PluginUtils.slf4jDependency;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jrebirth.forge.utils.TemplateSettings;

import freemarker.template.TemplateException;

/**
 * JRebirth Facet.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 */
@Alias("org.jrebirth")
@RequiresFacet({ DependencyFacet.class })
public class JRebirthFacet extends BaseFacet {

    /** The shell. */
    @Inject
    private ShellPrompt shell;

    /** The dependency facet. */
    private DependencyFacet dependencyFacet;

    /** The writer. */
    @Inject
    private ShellPrintWriter writer;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean install() {
        this.dependencyFacet = this.project.getFacet(DependencyFacet.class);

        this.dependencyFacet.addRepository(messages.getKeyValue("jrebirthReleaseRepoName"), messages.getKeyValue("jrebirthReleaseRepoUrl"));

        installDependencies(this.project, this.shell, this.writer, jrebirthCoreDependency(), false);
        installDependencies(this.project, this.shell, this.writer, javafxDependency(), false);
        installDependencies(this.project, this.shell, this.writer, slf4jDependency(), false);

        final MetadataFacet metadata = this.project
                .getFacet(MetadataFacet.class);

        TemplateSettings settings = new TemplateSettings(
                firstLetterCaps(metadata.getProjectName()) + "App",
                metadata.getTopLevelPackage());
        final Map<String, TemplateSettings> context = new HashMap<String, TemplateSettings>();
        settings.setTopLevelPacakge(metadata.getTopLevelPackage());

        context.put("settings", settings);
        try {
            createJavaFileUsingTemplate(this.project,
                    "TemplateApplication.ftl", context);
        } catch (IOException | TemplateException e) {
            ShellMessages.error(writer, messages.getMessage("unable.to.create.mainapp"));
        }

        final ResourceFacet resourceFacet = this.project
                .getFacet(ResourceFacet.class);
        final File rbPropertiesFile = resourceFacet.createResource(
                new char[0], "jrebirth.properties")
                .getUnderlyingResourceObject();

        settings = new TemplateSettings("jrebirth.properties", "");

        try {
            createResourceFileUsingTemplate(this.project,
                    "TemplateMainProperties.ftl", rbPropertiesFile, context);
        } catch (IOException | TemplateException e) {
            ShellMessages.error(writer, messages.getMessage("unable.to.create.jrproperties"));
        }

        final DirectoryResource directory = resourceFacet
                .getResourceFolder();
        directory.getChildDirectory("fonts").mkdir();
        directory.getChildDirectory("images").mkdir();
        directory.getChildDirectory("styles").mkdir();

        try {
            createJNLPConfiguration(this.project);
            ShellMessages.warn(writer,
                    messages.getMessage("jnlp.dependency.is.setup"));
        } catch (IOException | TemplateException e) {
            ShellMessages.error(writer, messages.getMessage("unable.to.create.jnlp"));
        }

        setupMavenProjectProfiles(this.project,
                metadata.getTopLevelPackage(), metadata.getProjectName());

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInstalled() {

        final DependencyFacet dFacet = this.project.getFacet(DependencyFacet.class);

        if (dFacet.hasDirectDependency(jrebirthCoreDependency()) && dFacet.hasRepository(messages.getKeyValue("jrebirthReleaseRepoUrl"))) {
            return true;
        }

        return false;
    }

    /**
     * Adds the snapshot repository.
     */
    public void addSnapshotRepository() {
        if (this.dependencyFacet.hasRepository(messages.getKeyValue("jrebirthSnapshotRepoUrl")) == false) {
            this.dependencyFacet.addRepository(messages.getKeyValue("jrebirthSnapshotRepoName"), messages.getKeyValue("jrebirthSnapshotRepoUrl"));
            ShellMessages.info(this.writer, messages.getMessage("jrebirth.snapshot.repo.added"));
        }

    }

    /**
     * Removes the snapshot repository.
     */
    public void removeSnapshotRepository() {
        if (this.dependencyFacet.hasRepository(messages.getKeyValue("jrebirthSnapshotRepoUrl")) == true) {
            this.dependencyFacet.removeRepository(messages.getKeyValue("jrebirthSnapshotRepoUrl"));
            ShellMessages.info(this.writer, messages.getMessage("jrebirth.snapshot.repo.removed"));
        }

    }

}
