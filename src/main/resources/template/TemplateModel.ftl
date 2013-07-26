package ${settings.getTopLevelPacakge()};

<#if settings.isBeanCreate()>
import ${settings.getImportPackage()}.beans.${settings.getName()};
import org.jrebirth.core.ui.DefaultObjectModel;
<#else>
import org.jrebirth.core.ui.DefaultModel;
</#if>




public class ${settings.getName()}Model extends Default<#if settings.isBeanCreate()>Object</#if>Model<${settings.getName()}Model, ${settings.getName()}View <#if settings.isBeanCreate()> ,${settings.getName()}</#if>>
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