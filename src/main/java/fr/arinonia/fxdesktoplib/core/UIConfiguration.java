package fr.arinonia.fxdesktoplib.core;

public class UIConfiguration {
    private String title = "Application";
    private double minWidth = 800.0D;
    private double minHeight = 600.0D;
    private double width = 1280.0D;
    private double height = 720.0D;

    public UIConfiguration() {}

    public UIConfiguration setTitle(final String title) {
        this.title = title;
        return this;
    }

    public UIConfiguration setMinWidth(final double minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public UIConfiguration setMinHeight(final double minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public UIConfiguration setWidth(final double width) {
        this.width = width;
        return this;
    }

    public UIConfiguration setHeight(final double height) {
        this.height = height;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public double getMinWidth() {
        return this.minWidth;
    }

    public double getMinHeight() {
        return this.minHeight;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}
