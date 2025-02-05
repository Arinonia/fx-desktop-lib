package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class UILabel extends Label {
    private static final class Constants {
        static final double TITLE_SIZE = 24;
        static final double SUBTITLE_SIZE = 18;
        static final double HEADING_SIZE = 16;
        static final double BODY_SIZE = 14;
        static final double CAPTION_SIZE = 12;

        static final String BOLD_WEIGHT = "bold";
        static final String SEMIBOLD_WEIGHT = "600";
        static final String NORMAL_WEIGHT = "normal";

        static final double HIGH_EMPHASIS = 1.0;
        static final double MEDIUM_EMPHASIS = 0.87;
        static final double LOW_EMPHASIS = 0.6;

        static final String FONT_FAMILY = "Bahnschrift";

        static final double TITLE_LINE_HEIGHT = 1.4;
        static final double NORMAL_LINE_HEIGHT = 1.5;
    }

    private final ObjectProperty<TextType> type = new SimpleObjectProperty<>(TextType.BODY);
    private final ObjectProperty<TextEmphasis> emphasis = new SimpleObjectProperty<>(TextEmphasis.MEDIUM);

    private final LabelStyle labelStyle;

    public UILabel() {
        this("");
    }

    public UILabel(final String text) {
        super(text);
        this.labelStyle = new LabelStyle(this);
        initialize();
    }

    private void initialize() {
        this.getStyleClass().add("ui-label");
        setupListeners();
        this.labelStyle.updateStyle();
    }

    private void setupListeners() {
        this.type.addListener((obs, oldType, newType) -> this.labelStyle.updateStyle());
        this.emphasis.addListener((obs, oldEmphasis, newEmphasis) -> this.labelStyle.updateStyle());
        ThemeManager.addListener(palette -> this.labelStyle.updateStyle());
    }

    private static class LabelStyle {
        private final UILabel label;

        LabelStyle(final UILabel label) {
            this.label = label;
        }

        void updateStyle() {
            final ColorPalette palette = ThemeManager.getCurrentPalette();

            final double fontSize = getFontSize();
            final double opacity = getEmphasisOpacity();
            final String fontWeight = getFontWeight();
            final double lineHeight = getLineHeight();

            final String textColor = String.format("#%02X%02X%02X%02X",
                    (int) (palette.getOnSurface().getRed() * 255),
                    (int) (palette.getOnSurface().getGreen() * 255),
                    (int) (palette.getOnSurface().getBlue() * 255),
                    (int) (opacity * 255));

            final String style = String.format("""
                    -fx-font-family: '%s';
                    -fx-text-fill: %s;
                    -fx-font-size: %.1fpx;
                    -fx-font-weight: %s;
                    -fx-line-height: %.1f;
                    """,
                    Constants.FONT_FAMILY,
                    textColor,
                    fontSize,
                    fontWeight,
                    lineHeight
            );

            this.label.setStyle(style);
            setAdditionalProperties();
        }

        private double getFontSize() {
            return switch (this.label.getType()) {
                case TITLE -> Constants.TITLE_SIZE;
                case SUBTITLE -> Constants.SUBTITLE_SIZE;
                case HEADING -> Constants.HEADING_SIZE;
                case BODY -> Constants.BODY_SIZE;
                case CAPTION -> Constants.CAPTION_SIZE;
            };
        }

        private double getEmphasisOpacity() {
            return switch (this.label.getEmphasis()) {
                case HIGH -> Constants.HIGH_EMPHASIS;
                case MEDIUM -> Constants.MEDIUM_EMPHASIS;
                case LOW -> Constants.LOW_EMPHASIS;
            };
        }

        private String getFontWeight() {
            return switch (this.label.getType()) {
                case TITLE, HEADING -> Constants.BOLD_WEIGHT;
                case SUBTITLE -> Constants.SEMIBOLD_WEIGHT;
                default -> Constants.NORMAL_WEIGHT;
            };
        }

        private double getLineHeight() {
            return this.label.getType() == TextType.TITLE ?
                    Constants.TITLE_LINE_HEIGHT : Constants.NORMAL_LINE_HEIGHT;
        }

        private void setAdditionalProperties() {
            if (this.label.getType() == TextType.TITLE ||
                    this.label.getType() == TextType.SUBTITLE ||
                    this.label.getType() == TextType.HEADING) {
                this.label.setWrapText(true);
                this.label.setTextAlignment(TextAlignment.LEFT);
            }
        }
    }

    public TextType getType() {
        return this.type.get();
    }

    public void setType(final TextType type) {
        this.type.set(type);
    }

    public ObjectProperty<TextType> typeProperty() {
        return this.type;
    }

    public TextEmphasis getEmphasis() {
        return this.emphasis.get();
    }

    public void setEmphasis(final TextEmphasis emphasis) {
        this.emphasis.set(emphasis);
    }

    public ObjectProperty<TextEmphasis> emphasisProperty() {
        return this.emphasis;
    }

    public enum TextType {
        TITLE, SUBTITLE, HEADING, BODY, CAPTION
    }

    public enum TextEmphasis {
        HIGH, MEDIUM, LOW
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UILabel label;

        private Builder() {
            this.label = new UILabel();
        }

        public Builder text(final String text) {
            this.label.setText(text);
            return this;
        }

        public Builder type(final TextType type) {
            this.label.setType(type);
            return this;
        }

        public Builder emphasis(final TextEmphasis emphasis) {
            this.label.setEmphasis(emphasis);
            return this;
        }

        public Builder alignment(final TextAlignment alignment) {
            this.label.setTextAlignment(alignment);
            return this;
        }

        public Builder wrapping(final boolean wrap) {
            this.label.setWrapText(wrap);
            return this;
        }

        public UILabel build() {
            return this.label;
        }
    }
}