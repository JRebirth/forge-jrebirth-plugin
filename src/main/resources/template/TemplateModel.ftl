package ${settings.getTopLevelPacakge()};

<#if settings.isBeanCreate()>
import ${settings.getImportPackage()}.beans.${settings.getName()};
</#if>

import org.jrebirth.core.ui.DefaultObjectModel;

public class ${settings.getName()}Model extends DefaultObjectModel<${settings.getName()}Model, ${settings.getName()}View <#if settings.isBeanCreate()> ,${settings.getName()}</#if>>
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initModel() 
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void bind() 
    {
    }

    

}