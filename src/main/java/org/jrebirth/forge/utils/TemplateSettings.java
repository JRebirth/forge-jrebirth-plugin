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
 * Parameters to be passed to freemarker templates.
 *
 * @author Guruprasad Shenoy <gpshenoy@gmail.com>
 */
public final class TemplateSettings {

    /** The is controller create. */
    private boolean isControllerCreate = true;
    
    /** The is bean create. */
    private boolean isBeanCreate = true;
    
    /** The is fxml create. */
    private boolean isFXMLCreate = false;
    
    /** The name. */
    private String name = null;
    
    /** The top level pacakge. */
    private String topLevelPacakge = null;
    
    /** The import package. */
    private String importPackage = null;
    
    /** The command type. */
    private String commandType = null;

    /**
     * Instantiates a new template settings.
     *
     * @param name of the template to be created
     * @param importPackage source package
     */
    public TemplateSettings(String name, String importPackage) {
        this.name = name;
        this.importPackage = importPackage;
    }

    /**
     * Checks if is controller create.
     *
     * @return true, if is controller create
     */
    public boolean isControllerCreate() {
        return isControllerCreate;
    }

    /**
     * Sets the controller create.
     *
     * @param isControllerCreate the new controller create
     */
    public void setControllerCreate(boolean isControllerCreate) {
        this.isControllerCreate = isControllerCreate;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the top level pacakge.
     *
     * @return the top level pacakge
     */
    public String getTopLevelPacakge() {
        return topLevelPacakge;
    }

    /**
     * Sets the top level pacakge.
     *
     * @param topLevelPacakge the new top level pacakge
     */
    public void setTopLevelPacakge(String topLevelPacakge) {
        this.topLevelPacakge = topLevelPacakge;
    }

    /**
     * Gets the import package.
     *
     * @return the import package
     */
    public String getImportPackage() {
        return importPackage;
    }

    /**
     * Sets the import package.
     *
     * @param importPackage the new import package
     */
    public void setImportPackage(String importPackage) {
        this.importPackage = importPackage;
    }

    /**
     * Checks if is bean create.
     *
     * @return true, if is bean create
     */
    public boolean isBeanCreate() {
        return isBeanCreate;
    }

    /**
     * Sets the bean create.
     *
     * @param isBeanCreate the new bean create
     */
    public void setBeanCreate(boolean isBeanCreate) {
        this.isBeanCreate = isBeanCreate;
    }

    /**
     * Checks if is fXML create.
     *
     * @return true, if is fXML create
     */
    public boolean isFXMLCreate() {
        return isFXMLCreate;
    }

    /**
     * Sets the fXML create.
     *
     * @param isFXMLCreate the new fXML create
     */
    public void setFXMLCreate(boolean isFXMLCreate) {
        this.isFXMLCreate = isFXMLCreate;
    }

    /**
     * Gets the command type.
     *
     * @return the command type
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Sets the command type.
     *
     * @param commandType the new command type
     */
    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    
    
}
