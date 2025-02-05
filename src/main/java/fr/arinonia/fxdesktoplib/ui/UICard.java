package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class UICard extends VBox {
    private static final class Constants {
        static final double DEFAULT_PADDING = 15;
        static final double DEFAULT_SPACING = 10;
        static final double DEFAULT_RADIUS = 10;
        static final double DEFAULT_ELEVATION = 2;

        static final double SHADOW_OFFSET_Y = 2;
        static final double HOVER_SHADOW_OFFSET_Y = 4;
        static final double HOVER_ELEVATION_MULTIPLIER = 1.5;

        static final Duration ANIMATION_DURATION = Duration.millis(200);

        static final double HOVER_TRANSLATE_Y = -2;
    }

    private final BooleanProperty hoverable = new SimpleBooleanProperty(false);
    private final DoubleProperty elevation = new SimpleDoubleProperty(Constants.DEFAULT_ELEVATION);
    private Timeline hoverAnimation;
    private DropShadow defaultShadow;
    private DropShadow hoverShadow;

    public UICard() {
        initialize();
    }

    public UICard(final Node... children) {
        super(children);
        initialize();
    }

    private void initialize() {
        this.getStyleClass().add("ui-card");
        this.setPadding(new Insets(Constants.DEFAULT_PADDING));
        this.setSpacing(Constants.DEFAULT_SPACING);

        setupShadows();
        setupEffects();
        setupListeners();
        updateStyle();
    }

    private void setupShadows() {
        final ColorPalette palette = ThemeManager.getCurrentPalette();
        this.defaultShadow = createShadow(getElevation(), Constants.SHADOW_OFFSET_Y, palette);
        this.hoverShadow = createShadow(
                getElevation() * Constants.HOVER_ELEVATION_MULTIPLIER,
                Constants.HOVER_SHADOW_OFFSET_Y,
                palette
        );
        this.setEffect(this.defaultShadow);
    }

    private void setupEffects() {
        this.hoverAnimation = new Timeline();
        this.hoverAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(translateYProperty(), 0),
                        new KeyValue(this.defaultShadow.radiusProperty(), this.defaultShadow.getRadius())
                )
        );
    }

    private void setupListeners() {
        this.hoverable.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                setupHoverHandlers();
            } else {
                removeHoverHandlers();
            }
        });

        this.elevation.addListener((obs, oldVal, newVal) -> {
            setupShadows();
        });

        ThemeManager.addListener(this::onThemeChanged);
    }

    private void setupHoverHandlers() {
        this.setOnMouseEntered(e -> playHoverAnimation(true));
        this.setOnMouseExited(e -> playHoverAnimation(false));
    }

    private void removeHoverHandlers() {
        this.setOnMouseEntered(null);
        this.setOnMouseExited(null);
        resetState();
    }

    private void resetState() {
        this.hoverAnimation.stop();
        this.setTranslateY(0);
        this.setEffect(this.defaultShadow);
    }

    private void playHoverAnimation(final boolean hovering) {
        this.hoverAnimation.stop();

        final double targetTranslateY = hovering ? Constants.HOVER_TRANSLATE_Y : 0;
        final DropShadow targetShadow = hovering ? this.hoverShadow : this.defaultShadow;

        this.hoverAnimation.getKeyFrames().setAll(
                new KeyFrame(Constants.ANIMATION_DURATION,
                        new KeyValue(translateYProperty(), targetTranslateY),
                        new KeyValue(((DropShadow)getEffect()).radiusProperty(), targetShadow.getRadius()),
                        new KeyValue(((DropShadow)getEffect()).offsetYProperty(), targetShadow.getOffsetY())
                )
        );

        this.hoverAnimation.play();
    }

    private void updateStyle() {
        final ColorPalette palette = ThemeManager.getCurrentPalette();
        final String style = String.format("""
                -fx-background-color: %s;
                -fx-background-radius: %f;
                """,
                palette.getSurfaceHex(),
                Constants.DEFAULT_RADIUS
        );
        this.setStyle(style);
        setupShadows();
    }

    private void onThemeChanged(final ColorPalette newPalette) {
        updateStyle();
    }

    private static DropShadow createShadow(final double elevation, final double offsetY, final ColorPalette palette) {
        final DropShadow shadow = new DropShadow();
        shadow.setRadius(elevation);
        shadow.setOffsetY(offsetY);
        shadow.setColor(palette.getOverlay());
        return shadow;
    }

    public void setHoverable(final boolean hoverable) {
        this.hoverable.set(hoverable);
    }

    public boolean isHoverable() {
        return this.hoverable.get();
    }

    public BooleanProperty hoverableProperty() {
        return this.hoverable;
    }

    public void setElevation(final double elevation) {
        this.elevation.set(elevation);
    }

    public double getElevation() {
        return this.elevation.get();
    }

    public DoubleProperty elevationProperty() {
        return this.elevation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UICard card;

        private Builder() {
            this.card = new UICard();
        }

        public Builder hoverable(final boolean hoverable) {
            this.card.setHoverable(hoverable);
            return this;
        }

        public Builder elevation(final double elevation) {
            this.card.setElevation(elevation);
            return this;
        }

        public Builder children(final Node... children) {
            this.card.getChildren().addAll(children);
            return this;
        }

        public UICard build() {
            return this.card;
        }
    }
}