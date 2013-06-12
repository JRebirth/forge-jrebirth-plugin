package ${settings.getTopLevelPacakge()};

import org.jrebirth.core.exception.CoreException;
import org.jrebirth.core.ui.DefaultController;

public class ${settings.getName()}Controller extends DefaultController<${settings.getName()}Model, ${settings.getName()}View>
{

    /**
     * Instantiates a new question controller.
     * 
     * @param view the view
     * @throws CoreException the core exception
     */
    public ${settings.getName()}Controller(final ${settings.getName()}View view) throws CoreException 
    {
        super(view);
    }
}