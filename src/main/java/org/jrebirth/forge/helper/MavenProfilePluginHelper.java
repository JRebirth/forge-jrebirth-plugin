/**
 * Get more info at : www.jrebirth.org . 
 * Copyright JRebirth.org © 2011-2013
 * Contact : sebastien.bordes@jrebirth.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jrebirth.forge.helper;

import static org.jrebirth.forge.utils.PluginUtils.messages;

import java.io.IOException;
import java.io.StringReader;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jrebirth.forge.utils.PluginUtils;

/**
 * Class for handling Maven Profile Plugin settings.
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * 
 */
public final class MavenProfilePluginHelper {

    public static void setupMavenProjectProfiles(final Project project, final String topLevelPackageName, final String projectName) {
        final MavenCoreFacet facet = project.getFacet(MavenCoreFacet.class);
        final Model pom = facet.getPOM();

        final String projName = PluginUtils.firstLetterCaps(projectName);

        final Build build;

        build = ((pom.getBuild() == null) ? new Build() : pom.getBuild());

        addMavenDefaultProperties(pom, topLevelPackageName, projName);

        build.addPlugin(addMavenJarPlugin());
        build.addPlugin(addMavenCompilerPlugin());
        build.addPlugin(addMavenWebstartPlugin());

        pom.setBuild(build);

        facet.setPOM(pom);
    }

    private static void addMavenDefaultProperties(final Model pom, final String topLevelPackageName,
            final String projectName)
    {
        pom.addProperty("project.build.sourceEncoding", "UTF-8");

        pom.addProperty("jrebirthVersion", messages.getKeyValue("jrebirthVersion"));

        pom.addProperty("javaVersion", messages.getKeyValue("javaJdkVersion"));

        pom.addProperty("appMainClass", topLevelPackageName + "." + projectName + "App");
        pom.addProperty("jnlpFilename", projectName + ".jnlp");

        pom.addProperty("appletWidth", messages.getKeyValue("appletWidth"));
        pom.addProperty("appletHeight", messages.getKeyValue("appletHeight"));

        pom.addProperty("deployUrl", "");
        pom.addProperty("deployPath", "");
    }

    private static Plugin addMavenJarPlugin()
    {
        final Plugin mavenJarPlugin = new Plugin();

        mavenJarPlugin.setGroupId("org.apache.maven.plugins");
        mavenJarPlugin.setArtifactId("maven-jar-plugin");
        mavenJarPlugin.setVersion(messages.getKeyValue("mavenJarVersion"));

        mavenJarPlugin.setConfiguration(buildJarPluginConfiguration());

        return mavenJarPlugin;
    }

    private static Plugin addMavenCompilerPlugin() {

        final Plugin mavenCompilePlugin = new Plugin();

        mavenCompilePlugin.setGroupId("org.apache.maven.plugins");
        mavenCompilePlugin.setArtifactId("maven-compiler-plugin");
        mavenCompilePlugin.setVersion(messages.getKeyValue("mavenCompilerVersion"));

        mavenCompilePlugin.setConfiguration(buildCompilePluginConfiguration());

        return mavenCompilePlugin;
    }

    private static Plugin addMavenWebstartPlugin()
    {
        final Plugin mavenWebstartPlugin = new Plugin();
        PluginExecution pluginExecution = new PluginExecution();

        mavenWebstartPlugin.setGroupId("org.codehaus.mojo");
        mavenWebstartPlugin.setArtifactId("webstart-maven-plugin");
        mavenWebstartPlugin.setVersion(messages.getKeyValue("mavenWebstartVersion"));
        
        pluginExecution.addGoal("jnlp");
        pluginExecution.setPhase("package");
        
        mavenWebstartPlugin.addExecution(pluginExecution);
        
        mavenWebstartPlugin.setConfiguration(buildWebstartPluginConfiguration());

        return mavenWebstartPlugin;
    }

    private static Object buildCompilePluginConfiguration() {

        final StringBuffer buffer = new StringBuffer();
        buffer.append("<configuration>\n");
        buffer.append("<source>${javaVersion}</source>\n");
        buffer.append("<target>${javaVersion}</target>\n");
        buffer.append(" <encoding>UTF-8</encoding>\n");
        buffer.append(" <debug>false</debug>\n");
        buffer.append("<showDeprecation>true</showDeprecation>\n");
        buffer.append("</configuration>\n");

        return convertFromStringToXpp3Dom(buffer.toString());
    }

    private static Object buildJarPluginConfiguration() {

        final StringBuffer buffer = new StringBuffer();

        buffer.append("<configuration>\n");
        buffer.append("<archive>\n");
        buffer.append("<manifestEntries>\n");
        buffer.append("<JavaFX-Version>2.0</JavaFX-Version>\n");
        buffer.append(" <Main-Class>${appMainClass}</Main-Class>\n");
        buffer.append("<JavaFX-Application-Class>${appMainClass}</JavaFX-Application-Class>\n");
        buffer.append("<implementation-version>1.0</implementation-version>\n");
        buffer.append("<JavaFX-Class-Path></JavaFX-Class-Path>\n");
        buffer.append("</manifestEntries>\n");
        buffer.append("<manifest><addClasspath>true</addClasspath></manifest>\n");
        buffer.append("</archive>\n");
        buffer.append("</configuration>\n");

        return convertFromStringToXpp3Dom(buffer.toString());
    }
    
    private static Object buildWebstartPluginConfiguration() {
        final StringBuffer buffer = new StringBuffer();
        
        
        buffer.append("<configuration>");
        buffer.append("<jnlpFiles>${jrebirth.jnlp.filename}</jnlpFiles>");
        buffer.append("<excludeTransitive>false</excludeTransitive>");
        buffer.append("<libPath>lib</libPath>");
        buffer.append("<resourcesDirectory>${project.basedir}/src/main/jnlp/resources</resourcesDirectory>");
        buffer.append("<codebase>${deployUrl}/${deployPath}</codebase>");
        buffer.append(" <jnlp>");
        buffer.append("<outputFile>${jnlpFilename}</outputFile>");
        buffer.append("<mainClass>${appMainClass}</mainClass>");
        buffer.append("<offlineAllowed>true</offlineAllowed>");
        buffer.append("</jnlp>");
        buffer.append(" <sign>");
        buffer.append(" <keystore></keystore>");
        buffer.append("<keypass></keypass>");
        buffer.append("<storepass></storepass>");
        buffer.append("<alias></alias>");
        buffer.append("<validity>360</validity>");
        buffer.append("<dnameCn></dnameCn>");
        buffer.append("<dnameOu></dnameOu><dnameO></dnameO><dnameL></dnameL><dnameSt></dnameSt>");
        buffer.append("<dnameC></dnameC><verify>true</verify>");
        buffer.append("<keystoreConfig><delete>true</delete><gen>true</gen></keystoreConfig></sign>");
        buffer.append(" <pack200>true</pack200><gzip>true</gzip><outputJarVersions>false</outputJarVersions>");
        buffer.append("<install>false</install><verbose>true</verbose>");
        buffer.append("</configuration>");
              
        
        return convertFromStringToXpp3Dom(buffer.toString());
    }

    private static Object convertFromStringToXpp3Dom(final String xml) {
        try {
            return Xpp3DomBuilder.build(new StringReader(xml));
        } catch (final XmlPullParserException e) {
            throw new IllegalStateException(e);
        } catch (final IOException e) {
            throw new java.lang.IllegalStateException(e);
        }
    }

}
