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

/**
 * The class <strong>StringUtils</strong>.
 * 
 * @author Sébastien Bordes
 */
public class StringHelper {

    /**
     * Convert aStringUnderscored into A_STRING_UNDESCORED.
     * 
     * @param camelCaseString the string to convert
     * 
     * @return the underscored string
     */
    public static String camelCaseToUnderscore(final String camelCaseString) {

        StringBuilder sb = new StringBuilder();
        for (String camelPart : camelCaseString.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            if (sb.length() > 0) {
                sb.append("_");
            }
            sb.append(camelPart.toUpperCase());
        }
        return sb.toString();
    }
}
