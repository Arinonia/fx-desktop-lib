package fr.arinonia.fxdesktoplib.theme;

@FunctionalInterface
public interface ThemeChangeListener {
    void onThemeChanged(final ColorPalette newPalette);
}
