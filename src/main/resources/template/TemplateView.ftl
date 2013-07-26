package ${settings.getTopLevelPacakge()};

import javafx.scene.Node;
import org.jrebirth.core.exception.CoreException;

<#if !settings.isControllerCreate() >
import org.jrebirth.core.ui.DefaultController;
</#if>
import org.jrebirth.core.ui.DefaultView;
<#if settings.isFXMLCreate() >
import org.jrebirth.core.ui.fxml.FXMLComponent;
import org.jrebirth.core.ui.fxml.FXMLUtils;
</#if>


/**
* {@inheritDoc}
*/
public class ${settings.getName()}View extends DefaultView<${settings.getName()}Model, Node <#if settings.isControllerCreate() > ,${settings.getName()}Controller<#else> ,DefaultController<${settings.getName()}Model, ${settings.getName()}View></#if>> 
{


    public ${settings.getName()}View(final ${settings.getName()}Model model) throws CoreException {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initView() 
    {
    }
}