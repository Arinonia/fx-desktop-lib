package fr.arinonia.fxdesktoplib.theme;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ThemeManager {
    private static ColorPalette currentPalette = createDefaultPalette();
    private static final List<ThemeChangeListener> listeners = new CopyOnWriteArrayList<>();

    public static ColorPalette getCurrentPalette() {
        return currentPalette;
    }

    public static void setCurrentPalette(final ColorPalette palette) {
        currentPalette = palette;
        notifyListeners();
    }

    public static void addListener(final ThemeChangeListener listener) {
        listeners.add(listener);
    }
    public static void removeListener(final ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (final ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(currentPalette);
        }
    }

    private static ColorPalette createDefaultPalette() {
        return new ColorPalette.Builder()
                .name("Default")
                .build();
    }
}