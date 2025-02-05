package fr.arinonia.fxdesktoplib.theme;

import javafx.scene.paint.Color;

public class ColorPalette {
    private final String name;
    private final Color primary;
    private final Color primaryVariant;
    private final Color secondary;
    private final Color secondaryVariant;
    private final Color accent;
    private final Color background;
    private final Color surface;
    private final Color error;
    private final Color warning;
    private final Color success;
    private final Color info;
    private final Color onPrimary;
    private final Color onSecondary;
    private final Color onBackground;
    private final Color onSurface;
    private final Color onError;
    private final Color onWarning;
    private final Color onSuccess;
    private final Color onInfo;
    private final Color disabled;
    private final Color overlay;

    private ColorPalette(final Builder builder) {
        this.name = builder.name;
        this.primary = builder.primary;
        this.primaryVariant = builder.primaryVariant;
        this.secondary = builder.secondary;
        this.secondaryVariant = builder.secondaryVariant;
        this.accent = builder.accent;
        this.background = builder.background;
        this.surface = builder.surface;
        this.error = builder.error;
        this.warning = builder.warning;
        this.success = builder.success;
        this.info = builder.info;
        this.onPrimary = builder.onPrimary;
        this.onSecondary = builder.onSecondary;
        this.onBackground = builder.onBackground;
        this.onSurface = builder.onSurface;
        this.onError = builder.onError;
        this.onWarning = builder.onWarning;
        this.onSuccess = builder.onSuccess;
        this.onInfo = builder.onInfo;
        this.disabled = builder.disabled;
        this.overlay = builder.overlay;
    }

    public String getName() { return this.name; }
    public Color getPrimary() { return this.primary; }
    public Color getPrimaryVariant() { return this.primaryVariant; }
    public Color getSecondary() { return this.secondary; }
    public Color getSecondaryVariant() { return this.secondaryVariant; }
    public Color getAccent() { return this.accent; }
    public Color getBackground() { return this.background; }
    public Color getSurface() { return this.surface; }
    public Color getError() { return this.error; }
    public Color getWarning() { return this.warning; }
    public Color getSuccess() { return this.success; }
    public Color getInfo() { return this.info; }
    public Color getOnPrimary() { return this.onPrimary; }
    public Color getOnSecondary() { return this.onSecondary; }
    public Color getOnBackground() { return this.onBackground; }
    public Color getOnSurface() { return this.onSurface; }
    public Color getOnError() { return this.onError; }
    public Color getOnWarning() { return this.onWarning; }
    public Color getOnSuccess() { return this.onSuccess; }
    public Color getOnInfo() { return this.onInfo; }
    public Color getDisabled() { return this.disabled; }
    public Color getOverlay() { return this.overlay; }

    public String getPrimaryHex() { return colorToHex(this.primary); }
    public String getPrimaryVariantHex() { return colorToHex(this.primaryVariant); }
    public String getSecondaryHex() { return colorToHex(this.secondary); }
    public String getSecondaryVariantHex() { return colorToHex(this.secondaryVariant); }
    public String getAccentHex() { return colorToHex(this.accent); }
    public String getBackgroundHex() { return colorToHex(this.background); }
    public String getSurfaceHex() { return colorToHex(this.surface); }
    public String getErrorHex() { return colorToHex(this.error); }
    public String getWarningHex() { return colorToHex(this.warning); }
    public String getSuccessHex() { return colorToHex(this.success); }
    public String getInfoHex() { return colorToHex(this.info); }
    public String getOnPrimaryHex() { return colorToHex(this.onPrimary); }
    public String getOnSecondaryHex() { return colorToHex(this.onSecondary); }
    public String getOnBackgroundHex() { return colorToHex(this.onBackground); }
    public String getOnSurfaceHex() { return colorToHex(this.onSurface); }
    public String getOnErrorHex() { return colorToHex(this.onError); }
    public String getOnWarningHex() { return colorToHex(this.onWarning); }
    public String getOnSuccessHex() { return colorToHex(this.onSuccess); }
    public String getOnInfoHex() { return colorToHex(this.onInfo); }
    public String getDisabledHex() { return colorToHex(this.disabled); }
    public String getOverlayHex() { return colorToHex(this.overlay); }

    private String colorToHex(final Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static class Builder {
        private String name = "Default";
        // Main colors
        private Color primary = Color.rgb(149, 128, 255);
        private Color primaryVariant = Color.rgb(129, 108, 235);
        private Color secondary = Color.rgb(48, 25, 88);
        private Color secondaryVariant = Color.rgb(38, 15, 78);
        private Color accent = Color.rgb(255, 128, 149);

        // Background colors
        private Color background = Color.rgb(36, 17, 70);
        private Color surface = Color.rgb(48, 25, 88);

        // Status colors
        private Color error = Color.rgb(255, 69, 58);
        private Color warning = Color.rgb(255, 159, 10);
        private Color success = Color.rgb(48, 209, 88);
        private Color info = Color.rgb(100, 210, 255);

        // Text colors
        private Color onPrimary = Color.WHITE;
        private Color onSecondary = Color.WHITE;
        private Color onBackground = Color.WHITE;
        private Color onSurface = Color.WHITE;
        private Color onError = Color.WHITE;
        private Color onWarning = Color.rgb(41, 41, 41);
        private Color onSuccess = Color.rgb(41, 41, 41);
        private Color onInfo = Color.rgb(41, 41, 41);

        // Other colors
        private Color disabled = Color.rgb(128, 128, 128, 0.38);
        private Color overlay = Color.rgb(0, 0, 0, 0.5);

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder primary(final Color color) {
            this.primary = color;
            return this;
        }

        public Builder primary(final int r, final int g, final int b) {
            this.primary = Color.rgb(r, g, b);
            return this;
        }

        public Builder primary(final int r, final int g, final int b, final double opacity) {
            this.primary = Color.rgb(r, g, b, opacity);
            return this;
        }

        public Builder primaryVariant(final Color color) {
            this.primaryVariant = color;
            return this;
        }

        public Builder primaryVariant(final int r, final int g, final int b) {
            this.primaryVariant = Color.rgb(r, g, b);
            return this;
        }

        public Builder secondary(final Color color) {
            this.secondary = color;
            return this;
        }

        public Builder secondary(final int r, final int g, final int b) {
            this.secondary = Color.rgb(r, g, b);
            return this;
        }

        public Builder secondaryVariant(final Color color) {
            this.secondaryVariant = color;
            return this;
        }

        public Builder secondaryVariant(final int r, final int g, final int b) {
            this.secondaryVariant = Color.rgb(r, g, b);
            return this;
        }

        public Builder accent(final Color color) {
            this.accent = color;
            return this;
        }

        public Builder accent(final int r, final int g, final int b) {
            this.accent = Color.rgb(r, g, b);
            return this;
        }

        public Builder background(final Color color) {
            this.background = color;
            return this;
        }

        public Builder background(final int r, final int g, final int b) {
            this.background = Color.rgb(r, g, b);
            return this;
        }

        public Builder surface(final Color color) {
            this.surface = color;
            return this;
        }

        public Builder surface(final int r, final int g, final int b) {
            this.surface = Color.rgb(r, g, b);
            return this;
        }

        public Builder error(final Color color) {
            this.error = color;
            return this;
        }

        public Builder error(final int r, final int g, final int b) {
            this.error = Color.rgb(r, g, b);
            return this;
        }

        public Builder warning(final Color color) {
            this.warning = color;
            return this;
        }

        public Builder warning(final int r, final int g, final int b) {
            this.warning = Color.rgb(r, g, b);
            return this;
        }

        public Builder success(final Color color) {
            this.success = color;
            return this;
        }

        public Builder success(final int r, final int g, final int b) {
            this.success = Color.rgb(r, g, b);
            return this;
        }

        public Builder info(final Color color) {
            this.info = color;
            return this;
        }

        public Builder info(final int r, final int g, final int b) {
            this.info = Color.rgb(r, g, b);
            return this;
        }

        public Builder onPrimary(final Color color) {
            this.onPrimary = color;
            return this;
        }

        public Builder onPrimary(final int r, final int g, final int b) {
            this.onPrimary = Color.rgb(r, g, b);
            return this;
        }

        public Builder onSecondary(final Color color) {
            this.onSecondary = color;
            return this;
        }

        public Builder onSecondary(final int r, final int g, final int b) {
            this.onSecondary = Color.rgb(r, g, b);
            return this;
        }

        public Builder onBackground(final Color color) {
            this.onBackground = color;
            return this;
        }

        public Builder onBackground(final int r, final int g, final int b) {
            this.onBackground = Color.rgb(r, g, b);
            return this;
        }

        public Builder onSurface(final Color color) {
            this.onSurface = color;
            return this;
        }

        public Builder onSurface(final int r, final int g, final int b) {
            this.onSurface = Color.rgb(r, g, b);
            return this;
        }

        public Builder onError(final Color color) {
            this.onError = color;
            return this;
        }

        public Builder onError(final int r, final int g, final int b) {
            this.onError = Color.rgb(r, g, b);
            return this;
        }

        public Builder onWarning(final Color color) {
            this.onWarning = color;
            return this;
        }

        public Builder onWarning(final int r, final int g, final int b) {
            this.onWarning = Color.rgb(r, g, b);
            return this;
        }

        public Builder onSuccess(final Color color) {
            this.onSuccess = color;
            return this;
        }

        public Builder onSuccess(final int r, final int g, final int b) {
            this.onSuccess = Color.rgb(r, g, b);
            return this;
        }

        public Builder onInfo(final Color color) {
            this.onInfo = color;
            return this;
        }

        public Builder onInfo(final int r, final int g, final int b) {
            this.onInfo = Color.rgb(r, g, b);
            return this;
        }

        public Builder disabled(final Color color) {
            this.disabled = color;
            return this;
        }

        public Builder disabled(final int r, final int g, final int b, final double opacity) {
            this.disabled = Color.rgb(r, g, b, opacity);
            return this;
        }

        public Builder overlay(final Color color) {
            this.overlay = color;
            return this;
        }

        public Builder overlay(final int r, final int g, final int b, final double opacity) {
            this.overlay = Color.rgb(r, g, b, opacity);
            return this;
        }

        public ColorPalette build() {
            return new ColorPalette(this);
        }
    }
}