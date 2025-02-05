package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

public class UIButton extends Button {
    private static final class Constants {
        static final double DEFAULT_PADDING = 8;
        static final double MENU_PADDING = 10;
        static final double MENU_BUTTON_WIDTH = 200;
        static final double BORDER_RADIUS = 5;
        static final double FONT_SIZE = 14;

        static final double DEFAULT_SHADOW_RADIUS = 5;
        static final double HOVER_SHADOW_RADIUS = 8;
        static final double PRESSED_SHADOW_RADIUS = 3;
        static final double DEFAULT_SHADOW_OFFSET = 2;
        static final double HOVER_SHADOW_OFFSET = 3;
        static final double PRESSED_SHADOW_OFFSET = 1;

        static final Duration ANIMATION_DURATION = Duration.millis(150);


        static final String FONT_FAMILY = "Bahnschrift";
    }

    private final ObjectProperty<ButtonType> type = new SimpleObjectProperty<>(ButtonType.PRIMARY);
    private final BooleanProperty isMenuButton = new SimpleBooleanProperty(false);

    private final ButtonStyle buttonStyle;
    private final ButtonAnimator buttonAnimator;

    private static final PseudoClass PRESSED = PseudoClass.getPseudoClass("pressed");
    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");

    public UIButton() {
        this("");
    }

    public UIButton(final String text) {
        super(text);
        this.buttonStyle = new ButtonStyle(this);
        this.buttonAnimator = new ButtonAnimator(this);
        initialize();
    }

    private void initialize() {
        this.getStyleClass().add("ui-button");
        this.setCursor(Cursor.HAND);

        setupListeners();
        this.buttonStyle.updateStyle();
    }

    private void setupListeners() {
        this.type.addListener((obs, oldVal, newVal) -> buttonStyle.updateStyle());
        this.isMenuButton.addListener((obs, oldVal, newVal) -> buttonStyle.updateStyle());

        this.setOnMouseEntered(e -> {
            pseudoClassStateChanged(HOVER, true);
            this.buttonAnimator.playHoverAnimation();
        });

        this.setOnMouseExited(e -> {
            pseudoClassStateChanged(HOVER, false);
            this.buttonAnimator.playDefaultAnimation();
        });

        this.setOnMousePressed(e -> {
            pseudoClassStateChanged(PRESSED, true);
            this.buttonAnimator.playPressedAnimation();
        });

        this.setOnMouseReleased(e -> {
            pseudoClassStateChanged(PRESSED, false);
            if (isHover()) {
                this.buttonAnimator.playHoverAnimation();
            } else {
                this.buttonAnimator.playDefaultAnimation();
            }
        });

        ThemeManager.addListener(palette -> this.buttonStyle.updateStyle());
    }

    private static class ButtonStyle {
        private final UIButton button;

        ButtonStyle(final UIButton button) {
            this.button = button;
        }

        void updateStyle() {
            final ColorPalette palette = ThemeManager.getCurrentPalette();
            final ButtonType currentType = button.getType();

            final String backgroundColor = getBackgroundColor(currentType, palette);
            final String textColor = getTextColor(currentType, palette);
            final String padding = button.isMenuButton() ?
                    String.format("%f %f", Constants.MENU_PADDING, Constants.MENU_PADDING * 2) :
                    String.format("%f %f", Constants.DEFAULT_PADDING, Constants.DEFAULT_PADDING * 2);

            final String style = String.format("""
                    -fx-background-color: %s;
                    -fx-text-fill: %s;
                    -fx-padding: %s;
                    -fx-background-radius: %f;
                    -fx-font-family: '%s';
                    -fx-font-size: %fpx;
                    """,
                    backgroundColor,
                    textColor,
                    padding,
                    Constants.BORDER_RADIUS,
                    Constants.FONT_FAMILY,
                    Constants.FONT_SIZE
            );

            this.button.setStyle(style);
            updateButtonWidth();
        }

        private String getBackgroundColor(final ButtonType type, final ColorPalette palette) {
            return switch (type) {
                case PRIMARY -> palette.getPrimaryHex();
                case SECONDARY -> palette.getSecondaryHex();
                case SUCCESS -> palette.getSuccessHex();
                case WARNING -> palette.getWarningHex();
                case ERROR -> palette.getErrorHex();
            };
        }

        private String getTextColor(final ButtonType type, final ColorPalette palette) {
            return switch (type) {
                case PRIMARY -> palette.getOnPrimaryHex();
                case SECONDARY -> palette.getOnSecondaryHex();
                case SUCCESS -> palette.getOnSuccessHex();
                case WARNING -> palette.getOnWarningHex();
                case ERROR -> palette.getOnErrorHex();
            };
        }

        private void updateButtonWidth() {
            if (this.button.isMenuButton()) {
                this.button.setPrefWidth(Constants.MENU_BUTTON_WIDTH);
            } else {
                this.button.setPrefWidth(Button.USE_COMPUTED_SIZE);
            }
        }
    }

    private static class ButtonAnimator {
        private final UIButton button;
        private final Timeline animation;
        private DropShadow shadowEffect;

        ButtonAnimator(final UIButton button) {
            this.button = button;
            this.animation = new Timeline();
            setupInitialShadow();
        }

        private void setupInitialShadow() {
            this.shadowEffect = new DropShadow(
                    Constants.DEFAULT_SHADOW_RADIUS,
                    0,
                    Constants.DEFAULT_SHADOW_OFFSET,
                    ThemeManager.getCurrentPalette().getOverlay()
            );
            this.button.setEffect(this.shadowEffect);
        }

        void playHoverAnimation() {
            playAnimation(
                    Constants.HOVER_SHADOW_RADIUS,
                    Constants.HOVER_SHADOW_OFFSET
            );
        }

        void playDefaultAnimation() {
            playAnimation(
                    Constants.DEFAULT_SHADOW_RADIUS,
                    Constants.DEFAULT_SHADOW_OFFSET
            );
        }

        void playPressedAnimation() {
            playAnimation(
                    Constants.PRESSED_SHADOW_RADIUS,
                    Constants.PRESSED_SHADOW_OFFSET
            );
        }

        private void playAnimation(final double targetRadius, final double targetOffset) {
            this.animation.stop();
            this.animation.getKeyFrames().clear();
            this.animation.getKeyFrames().add(
                    new KeyFrame(Constants.ANIMATION_DURATION,
                            new KeyValue(this.shadowEffect.radiusProperty(), targetRadius),
                            new KeyValue(this.shadowEffect.offsetYProperty(), targetOffset)
                    )
            );
            this.animation.play();
        }
    }

    public enum ButtonType {
        PRIMARY, SECONDARY, SUCCESS, WARNING, ERROR
    }

    public ButtonType getType() {
        return this.type.get();
    }

    public void setType(final ButtonType type) {
        this.type.set(type);
    }

    public ObjectProperty<ButtonType> typeProperty() {
        return this.type;
    }

    public boolean isMenuButton() {
        return this.isMenuButton.get();
    }

    public void setAsMenuButton(final boolean isMenuButton) {
        this.isMenuButton.set(isMenuButton);
    }

    public BooleanProperty menuButtonProperty() {
        return this.isMenuButton;
    }
}