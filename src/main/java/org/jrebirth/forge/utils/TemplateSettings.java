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

/**
 * 
 * Parameters to be passed to freemarker templates
 * 
 * @author Guruprasad Shenoy <gpshenoy@gmail.com>
 */
public class TemplateSettings {

    private boolean isControllerCreate = true;
    private boolean isBeanCreate = true;
    private boolean isFXMLCreate = false;
    private String name = null;
    private String topLevelPacakge = null;
    private String importPackage = null;

    /**
     * 
     * @param name of the template to be created
     * @param importPackage source package
     */
    public TemplateSettings(String name, String importPackage) {
        this.name = name;
        this.importPackage = importPackage;
    }

    public boolean isControllerCreate() {
        return isControllerCreate;
    }

    public void setControllerCreate(boolean isControllerCreate) {
        this.isControllerCreate = isControllerCreate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopLevelPacakge() {
        return topLevelPacakge;
    }

    public void setTopLevelPacakge(String topLevelPacakge) {
        this.topLevelPacakge = topLevelPacakge;
    }

    public String getImportPackage() {
        return importPackage;
    }

    public void setImportPackage(String importPackage) {
        this.importPackage = importPackage;
    }

    public boolean isBeanCreate() {
        return isBeanCreate;
    }

    public void setBeanCreate(boolean isBeanCreate) {
        this.isBeanCreate = isBeanCreate;
    }

    public boolean isFXMLCreate() {
        return isFXMLCreate;
    }

    public void setFXMLCreate(boolean isFXMLCreate) {
        this.isFXMLCreate = isFXMLCreate;
    }

}
