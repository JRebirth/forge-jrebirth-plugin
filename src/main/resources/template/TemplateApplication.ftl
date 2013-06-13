package ${settings.getTopLevelPacakge()};

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.jrebirth.core.application.AbstractApplication;
import org.jrebirth.core.resource.font.FontItem;
import org.jrebirth.core.ui.Model;
import org.jrebirth.core.wave.Wave;
import org.jrebirth.core.ui.DefaultModel;

/**
 * The MTApplication is the main class used to start the application in standalone mode.
 */
public final class ${settings.getName()}AppMain extends AbstractApplication<StackPane> {

    /**
     * The main method triggered by Java Runtime.
     * 
     * @param args the arguments
     */
    public static void main(final String... args) {
        Application.launch(MTApplication.class, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Model> getFirstModelClass() {
        return DefaultModel.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getApplicationTitle() {
        return "Mastering Tables - Powered by JRebirth";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void customizeStage(final Stage stage) {
        stage.setFullScreen(false);
        stage.setResizable(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void customizeScene(final Scene scene) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FontItem> getFontToPreload() {
            return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Wave> getPreBootWaveList() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Wave> getPostBootWaveList() {
        return Collections.emptyList();
    }

}
