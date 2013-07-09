/**
 * Get more info at : www.jrebirth.org . Copyright JRebirth.org © 2011-2013
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
package org.jrebirth.forge.completer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;
import static org.jrebirth.forge.utils.PluginUtils.messages;

/**
 * Completer for app-config command to retrieve all keys from jrebirth.properties file. 
 * 
 * @author Rajmahendra Hegde <rajmahendra@gmail.com>
 * 
 */
public class AppPropertyCompleter extends SimpleTokenCompleter {

    /** The project. */
    @Inject
    private Project project;

    /** The writer. */
    @Inject
    private ShellPrintWriter writer;

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<?> getCompletionTokens() {

        final ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);

        Properties prop = new Properties();
        try {
            prop.load(resourceFacet.getResource("jrebirth.properties").getResourceInputStream());
        } catch (IOException e) {
            ShellMessages.error(writer, messages.getMessage("unable.to.read.properties.file"));
        }

        List<String> keyName = new ArrayList<String>();

        Enumeration<Object> enu = prop.keys();
        while (enu.hasMoreElements())
            keyName.add(enu.nextElement().toString());

        return keyName;
    }

}
