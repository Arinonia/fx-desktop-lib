package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class UICheckBox extends HBox {
    private static final class Constants {
        static final double DEFAULT_BOX_SIZE = 18;
        static final double DEFAULT_SPACING = 8;
        static final double CORNER_RADIUS = 3;
        static final double RIPPLE_SIZE = 40;

        static final Duration ANIMATION_DURATION = Duration.millis(150);
        static final Duration RIPPLE_DURATION = Duration.millis(400);
        static final double RIPPLE_MAX_OPACITY = 0.12;

        static final String CHECK_MARK_PATH = "M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z";
        static final String INDETERMINATE_PATH = "M2 10h20v4H2z";
        static final double ICON_SCALE = 0.7;

        static final String FONT_FAMILY = "Bahnschrift";
        static final double FONT_SIZE = 14;
    }

    private final BooleanProperty selected;
    private final BooleanProperty disabled;
    private final BooleanProperty readOnly;
    private final BooleanProperty indeterminate;
    private final DoubleProperty boxSize;
    private final ObjectProperty<Node> customIcon;
    private final ObjectProperty<Pos> labelPosition;
    private final StringProperty validationMessage;

    private final Region box;
    private final SVGPath checkMark;
    private final SVGPath indeterminateMark;
    private final Label label;
    private final Region ripple;
    private final StackPane boxContainer;
    private final Label validationLabel;

    private final Timeline selectAnimation;
    private final Timeline rippleAnimation;

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass HOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("hover");
    private static final PseudoClass DISABLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("disabled");
    private static final PseudoClass INDETERMINATE_PSEUDO_CLASS = PseudoClass.getPseudoClass("indeterminate");
    private static final PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");
    private UICheckBox[] children = new UICheckBox[0];
    private final ChangeListener<Boolean> parentSelectedListener;
    public UICheckBox() {
        this("");
    }

    public UICheckBox(final String text) {
        this.selected = new SimpleBooleanProperty(false);
        this.disabled = new SimpleBooleanProperty(false);
        this.readOnly = new SimpleBooleanProperty(false);
        this.indeterminate = new SimpleBooleanProperty(false);
        this.parentSelectedListener = (obs, oldVal, newVal) -> {
            if (!this.indeterminate.get()) {
                for (UICheckBox child : children) {
                    child.setSelected(newVal);
                }
            }
        };
        this.boxSize = new SimpleDoubleProperty(Constants.DEFAULT_BOX_SIZE);
        this.customIcon = new SimpleObjectProperty<>();
        this.labelPosition = new SimpleObjectProperty<>(Pos.CENTER_LEFT);
        this.validationMessage = new SimpleStringProperty("");

        this.box = new Region();
        this.checkMark = new SVGPath();
        this.indeterminateMark = new SVGPath();
        this.label = new Label(text);
        this.ripple = new Region();
        this.boxContainer = new StackPane();
        this.validationLabel = new Label();

        this.selectAnimation = new Timeline();
        this.rippleAnimation = new Timeline();

        initialize();
    }

    private void initialize() {
        setupLayout();
        setupComponents();
        setupAnimations();
        setupListeners();
        setupKeyboardSupport();
        updateStyle();
    }

    private void setupLayout() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(Constants.DEFAULT_SPACING);
        this.setCursor(Cursor.HAND);

        this.boxContainer.getChildren().addAll(this.ripple, this.box, this.checkMark, this.indeterminateMark);
        updateLabelPosition();
    }

    private void updateLabelPosition() {
        this.getChildren().clear();
        if (this.labelPosition.get() == Pos.CENTER_LEFT) {
            this.getChildren().addAll(this.boxContainer, this.label, this.validationLabel);
        } else {
            this.getChildren().addAll(this.label, this.boxContainer, this.validationLabel);
        }
    }

    private void setupComponents() {
        this.box.prefWidthProperty().bind(this.boxSize);
        this.box.prefHeightProperty().bind(this.boxSize);
        this.box.minWidthProperty().bind(this.boxSize);
        this.box.minHeightProperty().bind(this.boxSize);
        this.box.maxWidthProperty().bind(this.boxSize);
        this.box.maxHeightProperty().bind(this.boxSize);

        this.checkMark.setContent(Constants.CHECK_MARK_PATH);
        this.checkMark.setScaleX(Constants.ICON_SCALE);
        this.checkMark.setScaleY(Constants.ICON_SCALE);
        this.checkMark.setOpacity(0);

        this.indeterminateMark.setContent(Constants.INDETERMINATE_PATH);
        this.indeterminateMark.setScaleX(Constants.ICON_SCALE);
        this.indeterminateMark.setScaleY(Constants.ICON_SCALE);
        this.indeterminateMark.setOpacity(0);

        this.validationLabel.getStyleClass().add("validation-label");
        this.validationLabel.setVisible(false);
        this.validationLabel.setManaged(false);

        this.ripple.setPrefSize(Constants.RIPPLE_SIZE, Constants.RIPPLE_SIZE);
        this.ripple.setMinSize(Constants.RIPPLE_SIZE, Constants.RIPPLE_SIZE);
        this.ripple.setMaxSize(Constants.RIPPLE_SIZE, Constants.RIPPLE_SIZE);
        this.ripple.setOpacity(0);

        this.boxContainer.setAlignment(Pos.CENTER);
        this.boxContainer.setPrefSize(Constants.RIPPLE_SIZE, Constants.RIPPLE_SIZE);
    }

    private void setupAnimations() {
        setupSelectAnimation();
        setupRippleAnimation();
    }

    private void setupSelectAnimation() {
        this.selectAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(this.checkMark.opacityProperty(), 0),
                        new KeyValue(this.indeterminateMark.opacityProperty(), 0)
                ),
                new KeyFrame(Constants.ANIMATION_DURATION,
                        new KeyValue(this.checkMark.opacityProperty(), 1),
                        new KeyValue(this.indeterminateMark.opacityProperty(), 1)
                )
        );
    }

    private void setupRippleAnimation() {
        this.rippleAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(this.ripple.opacityProperty(), Constants.RIPPLE_MAX_OPACITY)
                ),
                new KeyFrame(Constants.RIPPLE_DURATION,
                        new KeyValue(this.ripple.opacityProperty(), 0)
                )
        );
    }

    private void setupListeners() {
        this.selected.addListener((obs, wasSelected, isSelected) -> {
            if (!this.indeterminate.get()) {
                pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected);
                playSelectAnimation();
                updateStyle();
            }
        });

        this.disabled.addListener((obs, wasDisabled, isDisabled) -> {
            pseudoClassStateChanged(DISABLED_PSEUDO_CLASS, isDisabled);
            this.setCursor(isDisabled ? Cursor.DEFAULT : Cursor.HAND);
            updateStyle();
        });

        this.indeterminate.addListener((obs, wasIndeterminate, isIndeterminate) -> {
            pseudoClassStateChanged(INDETERMINATE_PSEUDO_CLASS, isIndeterminate);
            if (isIndeterminate) {
                this.selected.set(false);
            }
            updateStyle();
        });

        this.setOnMouseClicked(event -> {
            if (!this.disabled.get() && !this.readOnly.get()) {
                if (this.indeterminate.get()) {
                    this.setIndeterminate(false);
                    this.setSelected(true);
                } else {
                    toggleState();
                }
                playRippleAnimation();
            }
        });

        this.setOnMouseEntered(event -> {
            if (!this.disabled.get()) {
                pseudoClassStateChanged(HOVER_PSEUDO_CLASS, true);
                updateStyle();
            }
        });

        this.setOnMouseExited(event -> {
            pseudoClassStateChanged(HOVER_PSEUDO_CLASS, false);
            updateStyle();
        });

        this.labelPosition.addListener((obs, oldPos, newPos) -> updateLabelPosition());

        this.validationMessage.addListener((obs, oldMsg, newMsg) -> {
            boolean hasError = !newMsg.isEmpty();
            this.validationLabel.setText(newMsg);
            this.validationLabel.setVisible(hasError);
            this.validationLabel.setManaged(hasError);
            pseudoClassStateChanged(INVALID_PSEUDO_CLASS, hasError);
            updateStyle();
        });

        ThemeManager.addListener(palette -> updateStyle());
    }

    private void setupKeyboardSupport() {
        this.setFocusTraversable(true);
        this.setOnKeyPressed(event -> {
            if ((event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER)
                    && !this.disabled.get() && !this.readOnly.get()) {
                toggleState();
                playRippleAnimation();
                event.consume();
            }
        });
    }

    private void toggleState() {
        if (this.indeterminate.get()) {
            this.indeterminate.set(false);
            this.selected.set(true);
        } else {
            this.selected.set(!this.selected.get());
        }
    }

    public void setupAsParentOf(final UICheckBox... children) {
        if (children == null || children.length == 0) return;

        this.children = children;
        this.selected.removeListener(this.parentSelectedListener);
        this.selected.addListener(this.parentSelectedListener);

        for (final UICheckBox child : children) {
            child.selectedProperty().addListener((obs, oldVal, newVal) -> updateParentState());
        }

        updateParentState();
    }


    private void updateParentState() {
        int selectedCount = 0;
        for (final UICheckBox child : children) {
            if (child.isSelected()) selectedCount++;
        }

        this.selected.removeListener(this.parentSelectedListener);

        if (selectedCount == 0) {
            this.selected.set(false);
            this.indeterminate.set(false);
        } else if (selectedCount == this.children.length) {
            this.indeterminate.set(false);
            this.selected.set(true);
        } else {
            this.selected.set(false);
            this.indeterminate.set(true);
        }

        this.selected.addListener(this.parentSelectedListener);
    }

    private void updateStyle() {
        final ColorPalette palette = ThemeManager.getCurrentPalette();

        String boxBorderColor;
        String boxBackgroundColor;

        if (this.disabled.get()) {
            boxBorderColor = String.format("rgba(%d, %d, %d, 0.38)",
                    (int) (palette.getOnSurface().getRed() * 255),
                    (int) (palette.getOnSurface().getGreen() * 255),
                    (int) (palette.getOnSurface().getBlue() * 255));
            boxBackgroundColor = "transparent";
        } else if (this.selected.get() || this.indeterminate.get()) {
            boxBorderColor = this.validationMessage.get().isEmpty() ?
                    palette.getPrimaryHex() : palette.getErrorHex();
            boxBackgroundColor = boxBorderColor;
        } else {
            boxBorderColor = String.format("rgba(%d, %d, %d, 0.6)",
                    (int) (palette.getOnSurface().getRed() * 255),
                    (int) (palette.getOnSurface().getGreen() * 255),
                    (int) (palette.getOnSurface().getBlue() * 255));
            boxBackgroundColor = "transparent";
        }

        this.box.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-border-color: %s;
            -fx-border-width: 2;
            -fx-border-radius: %f;
            -fx-background-radius: %f;
            """,
                boxBackgroundColor,
                boxBorderColor,
                Constants.CORNER_RADIUS,
                Constants.CORNER_RADIUS
        ));

        final String iconColor = this.disabled.get() ?
                String.format("rgba(%d, %d, %d, 0.38)",
                        (int) (palette.getOnSurface().getRed() * 255),
                        (int) (palette.getOnSurface().getGreen() * 255),
                        (int) (palette.getOnSurface().getBlue() * 255)) :
                palette.getOnPrimaryHex();

        this.checkMark.setStyle(String.format("-fx-fill: %s;", iconColor));
        this.indeterminateMark.setStyle(String.format("-fx-fill: %s;", iconColor));

        final String textColor = this.disabled.get() ?
                String.format("rgba(%d, %d, %d, 0.38)",
                        (int) (palette.getOnSurface().getRed() * 255),
                        (int) (palette.getOnSurface().getGreen() * 255),
                        (int) (palette.getOnSurface().getBlue() * 255)) :
                palette.getOnSurfaceHex();

        this.label.setStyle(String.format("""
            -fx-text-fill: %s;
            -fx-font-family: '%s';
            -fx-font-size: %f;
            """,
                textColor,
                Constants.FONT_FAMILY,
                Constants.FONT_SIZE
        ));

        this.validationLabel.setStyle(String.format("""
            -fx-text-fill: %s;
            -fx-font-family: '%s';
            -fx-font-size: %f;
            """,
                palette.getErrorHex(),
                Constants.FONT_FAMILY,
                Constants.FONT_SIZE - 2
        ));

        final String rippleColor = String.format("rgba(%d, %d, %d, %.2f)",
                (int) (palette.getPrimary().getRed() * 255),
                (int) (palette.getPrimary().getGreen() * 255),
                (int) (palette.getPrimary().getBlue() * 255),
                isHover() ? Constants.RIPPLE_MAX_OPACITY : 0);

        this.ripple.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-background-radius: %f;
            """,
                rippleColor,
                Constants.RIPPLE_SIZE / 2
        ));
    }

    private void playSelectAnimation() {
        this.selectAnimation.stop();
        this.selectAnimation.getKeyFrames().clear();

        final Node targetIcon = this.indeterminate.get() ? this.indeterminateMark : this.checkMark;
        final Node otherIcon = this.indeterminate.get() ? this.checkMark : this.indeterminateMark;

        otherIcon.setOpacity(0);

        this.selectAnimation.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(targetIcon.opacityProperty(), targetIcon.getOpacity())
                ),
                new KeyFrame(Constants.ANIMATION_DURATION,
                        new KeyValue(targetIcon.opacityProperty(), this.selected.get() || this.indeterminate.get() ? 1 : 0)
                )
        );

        this.selectAnimation.play();
    }

    private void playRippleAnimation() {
        this.rippleAnimation.stop();
        this.rippleAnimation.play();
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

    public boolean isCheckboxDisabled() {
        return this.disabled.get();
    }

    public void setCheckboxDisabled(final boolean disabled) {
        this.disabled.set(disabled);
        setDisable(disabled);
    }

    public BooleanProperty checkboxDisabledProperty() {
        return this.disabled;
    }

    public boolean isReadOnly() {
        return this.readOnly.get();
    }

    public void setReadOnly(final boolean readOnly) {
        this.readOnly.set(readOnly);
    }

    public BooleanProperty readOnlyProperty() {
        return this.readOnly;
    }

    public boolean isIndeterminate() {
        return this.indeterminate.get();
    }

    public void setIndeterminate(final boolean indeterminate) {
        this.indeterminate.set(indeterminate);
    }

    public BooleanProperty indeterminateProperty() {
        return this.indeterminate;
    }

    public double getBoxSize() {
        return this.boxSize.get();
    }

    public void setBoxSize(final double size) {
        this.boxSize.set(size);
    }

    public DoubleProperty boxSizeProperty() {
        return this.boxSize;
    }

    public Node getCustomIcon() {
        return this.customIcon.get();
    }

    public void setCustomIcon(final Node icon) {
        this.customIcon.set(icon);
    }

    public ObjectProperty<Node> customIconProperty() {
        return this.customIcon;
    }

    public Pos getLabelPosition() {
        return this.labelPosition.get();
    }

    public void setLabelPosition(final Pos position) {
        this.labelPosition.set(position);
    }

    public ObjectProperty<Pos> labelPositionProperty() {
        return this.labelPosition;
    }

    public String getText() {
        return this.label.getText();
    }

    public void setText(final String text) {
        this.label.setText(text);
    }

    public String getValidationMessage() {
        return this.validationMessage.get();
    }

    public void setValidationMessage(final String message) {
        this.validationMessage.set(message);
    }

    public StringProperty validationMessageProperty() {
        return this.validationMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UICheckBox checkbox;

        private Builder() {
            this.checkbox = new UICheckBox();
        }

        public Builder text(final String text) {
            this.checkbox.setText(text);
            return this;
        }

        public Builder selected(final boolean selected) {
            this.checkbox.setSelected(selected);
            return this;
        }

        public Builder disabled(final boolean disabled) {
            this.checkbox.setDisabled(disabled);
            return this;
        }

        public Builder readOnly(final boolean readOnly) {
            this.checkbox.setReadOnly(readOnly);
            return this;
        }

        public Builder indeterminate(final boolean indeterminate) {
            this.checkbox.setIndeterminate(indeterminate);
            return this;
        }

        public Builder boxSize(final double size) {
            this.checkbox.setBoxSize(size);
            return this;
        }

        public Builder customIcon(final Node icon) {
            this.checkbox.setCustomIcon(icon);
            return this;
        }

        public Builder labelPosition(final Pos position) {
            this.checkbox.setLabelPosition(position);
            return this;
        }

        public Builder validationMessage(final String message) {
            this.checkbox.setValidationMessage(message);
            return this;
        }

        public UICheckBox build() {
            return this.checkbox;
        }
    }
}