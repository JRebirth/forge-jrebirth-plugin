package ${settings.getTopLevelPacakge()};

import org.jrebirth.core.exception.CoreException;
import org.jrebirth.core.ui.DefaultController;

/**
* {@inheritDoc}
*/
public class ${settings.getName()}Controller extends DefaultController<${settings.getName()}Model, ${settings.getName()}View>
{

    public ${settings.getName()}Controller(final ${settings.getName()}View view) throws CoreException 
    {
        super(view);
    }
}