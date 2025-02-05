package fr.arinonia.fxdesktoplib.panel;

import fr.arinonia.fxdesktoplib.core.UIManager;
import javafx.scene.layout.Region;

public interface IPanel {
    Region getLayout();
    void init(final UIManager uiManager);
    default void onShow() {}
    default void onHide() {}
}
