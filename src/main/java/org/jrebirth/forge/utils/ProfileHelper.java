package org.jrebirth.forge.utils;

import static org.jrebirth.forge.utils.PluginUtils.resourceBundle;

import java.io.IOException;
import java.io.StringReader;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;

public final class ProfileHelper {

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

        pom.addProperty("jrebirthVersion", resourceBundle.getString("jrebirthVersion"));

        pom.addProperty("javaVersion", resourceBundle.getString("javaJdkVersion"));

        pom.addProperty("appMainClass", topLevelPackageName + "." + projectName + "App");
        pom.addProperty("jnlpFilename", topLevelPackageName + "." + projectName + ".jnlp");

        pom.addProperty("appletWidth", resourceBundle.getString("appletWidth"));
        pom.addProperty("appletHeight", resourceBundle.getString("appletHeight"));

        pom.addProperty("deployUrl", "");
        pom.addProperty("deployPath", "");
    }

    private static Plugin addMavenJarPlugin()
    {
        final Plugin mavenJarPlugin = new Plugin();

        mavenJarPlugin.setGroupId("org.apache.maven.plugins");
        mavenJarPlugin.setArtifactId("maven-jar-plugin");
        mavenJarPlugin.setVersion(resourceBundle.getString("mavenJarVersion"));

        mavenJarPlugin.setConfiguration(buildJarPluginConfiguration());

        return mavenJarPlugin;
    }

    private static Plugin addMavenCompilerPlugin() {

        final Plugin mavenCompilePlugin = new Plugin();

        mavenCompilePlugin.setGroupId("org.apache.maven.plugins");
        mavenCompilePlugin.setArtifactId("maven-compiler-plugin");
        mavenCompilePlugin.setVersion(resourceBundle.getString("mavenCompilerVersion"));

        mavenCompilePlugin.setConfiguration(buildCompilePluginConfiguration());

        return mavenCompilePlugin;
    }

    private static Plugin addMavenWebstartPlugin()
    {
        final Plugin mavenWebstartPlugin = new Plugin();

        mavenWebstartPlugin.setGroupId("org.codehaus.mojo");
        mavenWebstartPlugin.setArtifactId("webstart-maven-plugin");
        mavenWebstartPlugin.setVersion(resourceBundle.getString("mavenWebstartVersion"));
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
