package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UITextField extends VBox {
    private static final class Constants {
        static final double FIELD_HEIGHT = 40;
        static final double HORIZONTAL_PADDING = 12;
        static final double VERTICAL_PADDING = 8;
        static final double BORDER_RADIUS = 5;
        static final double MAX_WIDTH = 300;
        static final double SPACING = 4;

        static final double INPUT_FONT_SIZE = 14;
        static final double PLACEHOLDER_FONT_SIZE = 14;
        static final double ERROR_FONT_SIZE = 12;

        static final Duration ERROR_ANIMATION_DURATION = Duration.millis(500);
        static final double ERROR_SHAKE_DISTANCE = 10;
        static final int ERROR_SHAKE_CYCLES = 5;

        static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
        static final Pattern NUMBER_PATTERN = Pattern.compile("\\d*");

        static final double BORDER_OPACITY = 0.2;
        static final double PLACEHOLDER_OPACITY = 0.6;
        static final double DISABLED_OPACITY = 0.6;

    }

    private final TextField textField;
    private final StackPane textFieldContainer;
    private final Text placeholder;
    private final Text errorMessage;

    private final StringProperty placeholderText = new SimpleStringProperty("");
    private final StringProperty errorText = new SimpleStringProperty("");
    private final BooleanProperty isValid = new SimpleBooleanProperty(true);
    private final ObjectProperty<Predicate<String>> validator = new SimpleObjectProperty<>();
    private final BooleanProperty editable = new SimpleBooleanProperty(true);

    private final InputStyle inputStyle;
    private final ValidationHandler validationHandler;
    private final ErrorAnimator errorAnimator;

    public UITextField() {
        this("");
    }

    public UITextField(final String placeholder) {
        this.textField = new TextField();
        this.textFieldContainer = new StackPane();
        this.placeholder = new Text();
        this.errorMessage = new Text();

        this.inputStyle = new InputStyle(this);
        this.validationHandler = new ValidationHandler(this);
        this.errorAnimator = new ErrorAnimator(this);

        setPlaceholder(placeholder);
        initialize();
    }

    private void initialize() {
        this.getStyleClass().add("ui-text-field");
        this.setMaxWidth(Constants.MAX_WIDTH);
        this.setSpacing(Constants.SPACING);

        setupComponents();
        setupLayout();
        setupListeners();
        setupEditableState();
        this.inputStyle.updateStyle();
    }

    private void setupComponents() {
        this.textFieldContainer.setAlignment(Pos.CENTER_LEFT);

        this.placeholder.getStyleClass().add("placeholder");
        this.placeholder.setMouseTransparent(true);
        this.placeholder.setManaged(false);

        this.errorMessage.getStyleClass().add("error-message");
        this.errorMessage.setManaged(true);
        this.errorMessage.setVisible(false);
    }

    private void setupLayout() {
        this.placeholder.setTranslateX(Constants.HORIZONTAL_PADDING);
        this.textFieldContainer.getChildren().addAll(this.textField, this.placeholder);
        getChildren().addAll(this.textFieldContainer, this.errorMessage);
    }

    private void setupListeners() {
        this.textField.textProperty().addListener((obs, oldText, newText) -> {
            this.placeholder.setVisible(newText.isEmpty());
            this.validationHandler.validateInput(newText);
        });

        this.textField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            this.inputStyle.updateStyle();
        });

        this.validator.addListener((obs, oldValidator, newValidator) -> {
            if (newValidator != null) {
                this.validationHandler.validateInput(this.textField.getText());
            }
        });

        ThemeManager.addListener(palette -> this.inputStyle.updateStyle());
    }

    private void setupEditableState() {
        this.textField.editableProperty().bind(this.editable);
        this.editable.addListener((obs, oldVal, newVal) -> this.inputStyle.updateStyle());
    }

    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }

    public boolean isEditable() {
        return this.editable.get();
    }

    public BooleanProperty editableProperty() {
        return this.editable;
    }

    private class InputStyle {
        private final UITextField parent;

        InputStyle(final UITextField parent) {
            this.parent = parent;
        }

        void updateStyle() {
            final ColorPalette palette = ThemeManager.getCurrentPalette();
            final boolean isEditableNow = editable.get();

            final String textFieldStyle = String.format("""
                -fx-background-color: %s;
                -fx-text-fill: %s;
                -fx-prompt-text-fill: %s;
                -fx-border-color: %s;
                -fx-border-radius: %f;
                -fx-background-radius: %f;
                -fx-padding: %f %f;
                -fx-font-size: %fpx;
                """,
                    palette.getSurfaceHex(),
                    palette.getOnSurfaceHex(),
                    colorWithOpacity(palette.getOnSurface(), Constants.PLACEHOLDER_OPACITY),
                    isValid.get() ? colorWithOpacity(palette.getOnSurface(), Constants.BORDER_OPACITY)
                            : palette.getErrorHex(),
                    Constants.BORDER_RADIUS,
                    Constants.BORDER_RADIUS,
                    Constants.VERTICAL_PADDING,
                    Constants.HORIZONTAL_PADDING,
                    Constants.INPUT_FONT_SIZE,
                    isEditableNow ? 1.0 : Constants.DISABLED_OPACITY
            );
            textField.setStyle(textFieldStyle);

            final String placeholderStyle = String.format("""
                -fx-fill: %s;
                -fx-font-size: %fpx;
                """,
                    colorWithOpacity(palette.getOnSurface(), Constants.PLACEHOLDER_OPACITY),
                    Constants.PLACEHOLDER_FONT_SIZE
            );
            placeholder.setStyle(placeholderStyle);

            final String errorStyle = String.format("""
                -fx-fill: %s;
                -fx-font-size: %fpx;
                """,
                    palette.getErrorHex(),
                    Constants.ERROR_FONT_SIZE
            );
            errorMessage.setStyle(errorStyle);

            textField.setPrefHeight(Constants.FIELD_HEIGHT);
            textField.setPrefWidth(Region.USE_COMPUTED_SIZE);
        }
    }

    private class ValidationHandler {
        private final UITextField parent;

        ValidationHandler(final UITextField parent) {
            this.parent = parent;
        }

        void validateInput(final String text) {
            if (validator.get() != null) {
                boolean currentlyValid = validator.get().test(text);
                isValid.set(currentlyValid);
                errorMessage.setVisible(!currentlyValid);

                if (!currentlyValid) {
                    errorAnimator.playErrorAnimation();
                }
            }
        }
    }

    private class ErrorAnimator {
        private final UITextField parent;
        private final Timeline errorAnimation;

        ErrorAnimator(final UITextField parent) {
            this.parent = parent;
            this.errorAnimation = new Timeline();
            setupErrorAnimation();
        }

        private void setupErrorAnimation() {
            final double cycleTime = Constants.ERROR_ANIMATION_DURATION.toMillis() / Constants.ERROR_SHAKE_CYCLES;

            for (int i = 0; i < Constants.ERROR_SHAKE_CYCLES; i++) {
                final double startTime = i * cycleTime;

                this.errorAnimation.getKeyFrames().addAll(
                        new KeyFrame(Duration.millis(startTime),
                                new KeyValue(textFieldContainer.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(startTime + cycleTime / 2),
                                new KeyValue(textFieldContainer.translateXProperty(),
                                        i % 2 == 0 ? Constants.ERROR_SHAKE_DISTANCE : -Constants.ERROR_SHAKE_DISTANCE)),
                        new KeyFrame(Duration.millis(startTime + cycleTime),
                                new KeyValue(textFieldContainer.translateXProperty(), 0))
                );
            }
        }

        void playErrorAnimation() {
            this.errorAnimation.stop();
            this.errorAnimation.play();
        }
    }

    private static String colorWithOpacity(final Color color, final double opacity) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                opacity);
    }

    public void setPlaceholder(final String text) {
        this.placeholderText.set(text);
        this.placeholder.setText(text);
        this.placeholder.setVisible(this.textField.getText().isEmpty());
    }

    public void setErrorMessage(final String message) {
        this.errorText.set(message);
        this.errorMessage.setText(message);
    }

    public void setValidator(final Predicate<String> validator, final String errorMessage) {
        this.validator.set(validator);
        setErrorMessage(errorMessage);
    }

    public void setEmailValidator(final String errorMessage) {
        setValidator(text -> Constants.EMAIL_PATTERN.matcher(text).matches(), errorMessage);
    }

    public void setNumberValidator(final String errorMessage) {
        setValidator(text -> Constants.NUMBER_PATTERN.matcher(text).matches(), errorMessage);
    }

    public void setRequired(final String errorMessage) {
        setValidator(text -> !text.isEmpty(), errorMessage);
    }

    public void setMaxLength(final int maxLength, final String errorMessage) {
        this.textField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && newText.length() > maxLength) {
                this.textField.setText(oldText);
                setErrorMessage(errorMessage);
                this.errorAnimator.playErrorAnimation();
            }
        });
    }

    public String getText() {
        return this.textField.getText();
    }

    public void setText(final String text) {
        this.textField.setText(text);
    }

    public StringProperty textProperty() {
        return this.textField.textProperty();
    }

    public BooleanProperty validProperty() {
        return this.isValid;
    }

    public boolean isValid() {
        return this.isValid.get();
    }

    public TextField getTextField() {
        return this.textField;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UITextField textField;

        private Builder() {
            this.textField = new UITextField();
        }

        public Builder placeholder(final String placeholder) {
            this.textField.setPlaceholder(placeholder);
            return this;
        }

        public Builder required(final String errorMessage) {
            this.textField.setRequired(errorMessage);
            return this;
        }

        public Builder emailValidator(final String errorMessage) {
            this.textField.setEmailValidator(errorMessage);
            return this;
        }

        public Builder numberValidator(final String errorMessage) {
            this.textField.setNumberValidator(errorMessage);
            return this;
        }

        public Builder maxLength(final int maxLength, final String errorMessage) {
            this.textField.setMaxLength(maxLength, errorMessage);
            return this;
        }

        public Builder validator(final Predicate<String> validator, final String errorMessage) {
            this.textField.setValidator(validator, errorMessage);
            return this;
        }

        public Builder text(final String text) {
            this.textField.setText(text);
            return this;
        }

        public Builder editable(final boolean editable) {
            this.textField.setEditable(editable);
            return this;
        }

        public UITextField build() {
            return this.textField;
        }
    }
}