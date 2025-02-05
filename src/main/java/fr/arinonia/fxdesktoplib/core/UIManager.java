package fr.arinonia.fxdesktoplib.core;

import fr.arinonia.fxdesktoplib.panel.IPanel;
import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;


public class UIManager {
    private final Stage primaryStage;
    private final Scene scene;
    private IPanel currentPanel;
    private final Map<Class<? extends IPanel>, IPanel> panels;

    public UIManager(final Stage primaryStage, final UIConfiguration config) {
        this.primaryStage = primaryStage;
        this.panels = new HashMap<>();

        this.scene = new Scene(new Region());
        this.scene.setFill(ThemeManager.getCurrentPalette().getBackground());

        this.primaryStage.setTitle(config.getTitle());
        this.primaryStage.setMinWidth(config.getMinWidth());
        this.primaryStage.setMinHeight(config.getMinHeight());
        this.primaryStage.setWidth(config.getWidth());
        this.primaryStage.setHeight(config.getHeight());
        this.primaryStage.setScene(this.scene);

        ThemeManager.addListener(this::handleThemeChange);
    }

    public void addPanel(final IPanel panel) {
        this.panels.put(panel.getClass(), panel);
        panel.init(this);
        //LOGGER.info("Panel added: {}", panel.getClass().getSimpleName());
    }

    public void showPanel(final Class<? extends IPanel> panelClass) {
        final IPanel panel = this.panels.get(panelClass);
        if (panel == null) {
            System.err.printf("Panel not found: %s%n", panelClass.getSimpleName());
            return;
        }

        if (this.currentPanel != null) {
            this.currentPanel.onHide();
        }

        this.scene.setRoot(panel.getLayout());
        this.currentPanel = panel;
        panel.onShow();
        //LOGGER.info("Showing panel: {}", panelClass.getSimpleName());
    }

    private void handleThemeChange(final ColorPalette newPalette) {
        this.scene.setFill(newPalette.getBackground());
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public Scene getScene() {
        return this.scene;
    }

    public IPanel getCurrentPanel() {
        return this.currentPanel;
    }
}