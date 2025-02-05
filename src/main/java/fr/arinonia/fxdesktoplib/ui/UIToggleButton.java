package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Duration;

public class UIToggleButton extends StackPane {
    private static final class Constants {
        static final double DEFAULT_WIDTH = 120;
        static final double DEFAULT_HEIGHT = 36;
        static final double CORNER_RADIUS = 4;

        static final Duration ANIMATION_DURATION = Duration.millis(150);
        static final Duration RIPPLE_DURATION = Duration.millis(400);

        static final double RIPPLE_OPACITY = 0.12;
        static final double HOVER_OPACITY = 0.08;

        static final double FONT_SIZE = 14;
        static final String FONT_FAMILY = "Bahnschrift";
    }

    private final BooleanProperty selected;
    private final BooleanProperty disabled;

    private final Label label;
    private final Region rippleOverlay;
    private final Region hoverOverlay;

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass DISABLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("disabled");
    private static final PseudoClass HOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("hover");

    public UIToggleButton() {
        this("");
    }

    public UIToggleButton(final String text) {
        this.selected = new SimpleBooleanProperty(false);
        this.disabled = new SimpleBooleanProperty(false);

        this.label = new Label(text);
        this.rippleOverlay = new Region();
        this.hoverOverlay = new Region();

        initialize();
    }

    private void initialize() {
        this.getStyleClass().add("ui-toggle-button");
        setupComponents();
        setupListeners();
        updateStyle();
    }

    private void setupComponents() {
        setPrefSize(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT);
        setCursor(Cursor.HAND);
        setAlignment(Pos.CENTER);

        this.label.setMouseTransparent(true);

        this.rippleOverlay.setMouseTransparent(true);
        this.rippleOverlay.setOpacity(0);

        this.hoverOverlay.setMouseTransparent(true);
        this.hoverOverlay.setOpacity(0);

        this.getChildren().addAll(this.hoverOverlay, this.rippleOverlay, this.label);
    }

    private void setupListeners() {
        this.selected.addListener((obs, wasSelected, isSelected) -> {
            pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected);
            updateStyle();
        });

        this.disabled.addListener((obs, wasDisabled, isDisabled) -> {
            pseudoClassStateChanged(DISABLED_PSEUDO_CLASS, isDisabled);
            setCursor(isDisabled ? Cursor.DEFAULT : Cursor.HAND);
            updateStyle();
        });

        setOnMouseEntered(event -> {
            if (!this.disabled.get()) {
                pseudoClassStateChanged(HOVER_PSEUDO_CLASS, true);
                playHoverAnimation(true);
            }
        });

        setOnMouseExited(event -> {
            pseudoClassStateChanged(HOVER_PSEUDO_CLASS, false);
            playHoverAnimation(false);
        });

        setOnMousePressed(event -> {
            if (!this.disabled.get()) {
                playRippleAnimation();
            }
        });

        setOnMouseClicked(event -> {
            if (!this.disabled.get()) {
                this.selected.set(!this.selected.get());
            }
        });

        ThemeManager.addListener(palette -> updateStyle());
    }

    private void playRippleAnimation() {
        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(this.rippleOverlay.opacityProperty(), 0),
                        new KeyValue(this.rippleOverlay.scaleXProperty(), 0.6),
                        new KeyValue(this.rippleOverlay.scaleYProperty(), 0.6)
                ),
                new KeyFrame(Constants.RIPPLE_DURATION.multiply(0.3),
                        new KeyValue(this.rippleOverlay.opacityProperty(), Constants.RIPPLE_OPACITY)
                ),
                new KeyFrame(Constants.RIPPLE_DURATION,
                        new KeyValue(this.rippleOverlay.opacityProperty(), 0),
                        new KeyValue(this.rippleOverlay.scaleXProperty(), 1),
                        new KeyValue(this.rippleOverlay.scaleYProperty(), 1)
                )
        );
        timeline.play();
    }

    private void playHoverAnimation(final boolean hovering) {
        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Constants.ANIMATION_DURATION,
                        new KeyValue(this.hoverOverlay.opacityProperty(),
                                hovering ? Constants.HOVER_OPACITY : 0)
                )
        );
        timeline.play();
    }

    private void updateStyle() {
        final ColorPalette palette = ThemeManager.getCurrentPalette();

        String backgroundColor;
        String textColor;

        if (this.disabled.get()) {
            if (this.selected.get()) {
                backgroundColor = String.format("rgba(%d, %d, %d, 0.12)",
                        (int) (palette.getPrimary().getRed() * 255),
                        (int) (palette.getPrimary().getGreen() * 255),
                        (int) (palette.getPrimary().getBlue() * 255));
            } else {
                backgroundColor = "transparent";
            }
            textColor = String.format("rgba(%d, %d, %d, %.2f)",
                    (int) (palette.getOnSurface().getRed() * 255),
                    (int) (palette.getOnSurface().getGreen() * 255),
                    (int) (palette.getOnSurface().getBlue() * 255),
                    0.38);
        } else if (this.selected.get()) {
            backgroundColor = palette.getPrimaryHex();
            textColor = palette.getOnPrimaryHex();
        } else {
            backgroundColor = "transparent";
            textColor = palette.getOnSurfaceHex();
        }

        this.hoverOverlay.setBackground(new Background(new BackgroundFill(
                this.selected.get() ? palette.getOnPrimary() : palette.getPrimary(),
                new CornerRadii(Constants.CORNER_RADIUS),
                Insets.EMPTY
        )));

        this.rippleOverlay.setBackground(new Background(new BackgroundFill(
                this.selected.get() ? palette.getOnPrimary() : palette.getPrimary(),
                new CornerRadii(Constants.CORNER_RADIUS),
                Insets.EMPTY
        )));

        String borderColor;
        if (this.disabled.get()) {
            if (this.selected.get()) {
                borderColor = String.format("rgba(%d, %d, %d, 0.12)",
                        (int) (palette.getPrimary().getRed() * 255),
                        (int) (palette.getPrimary().getGreen() * 255),
                        (int) (palette.getPrimary().getBlue() * 255));
            } else {
                borderColor = String.format("rgba(%d, %d, %d, 0.12)",
                        (int) (palette.getOnSurface().getRed() * 255),
                        (int) (palette.getOnSurface().getGreen() * 255),
                        (int) (palette.getOnSurface().getBlue() * 255));
            }
        } else {
            borderColor = this.selected.get() ? palette.getPrimaryHex() :
                    String.format("rgba(%d, %d, %d, 0.23)",
                            (int) (palette.getOnSurface().getRed() * 255),
                            (int) (palette.getOnSurface().getGreen() * 255),
                            (int) (palette.getOnSurface().getBlue() * 255));
        }

        this.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-border-color: %s;
            -fx-border-width: 1;
            -fx-border-radius: %f;
            -fx-background-radius: %f;
            """,
                backgroundColor,
                borderColor,
                Constants.CORNER_RADIUS,
                Constants.CORNER_RADIUS
        ));

        this.label.setStyle(String.format("""
            -fx-text-fill: %s;
            -fx-font-family: '%s';
            -fx-font-size: %fpx;
            """,
                textColor,
                Constants.FONT_FAMILY,
                Constants.FONT_SIZE
        ));

        this.hoverOverlay.setPrefSize(getPrefWidth(), getPrefHeight());
        this.rippleOverlay.setPrefSize(getPrefWidth(), getPrefHeight());
    }

    public boolean isSelected() {
        return this.selected.get();
    }

    public void setSelected(final boolean selected) {
        this.selected.set(selected);
    }

    public BooleanProperty selectedProperty() {
        return this.selected;
    }

    public boolean isToggleDisabled() {
        return this.disabled.get();
    }

    public void setToggleDisabled(final boolean disabled) {
        this.disabled.set(disabled);
        setDisable(disabled);
    }

    public BooleanProperty toggleDisabledProperty() {
        return this.disabled;
    }

    public String getText() {
        return this.label.getText();
    }

    public void setText(final String text) {
        this.label.setText(text);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UIToggleButton toggleButton;

        private Builder() {
            this.toggleButton = new UIToggleButton();
        }

        public Builder text(final String text) {
            this.toggleButton.setText(text);
            return this;
        }

        public Builder selected(final boolean selected) {
            this.toggleButton.setSelected(selected);
            return this;
        }

        public Builder disabled(final boolean disabled) {
            this.toggleButton.setToggleDisabled(disabled);
            return this;
        }

        public UIToggleButton build() {
            return this.toggleButton;
        }
    }
}