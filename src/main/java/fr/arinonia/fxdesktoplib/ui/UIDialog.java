package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class UIDialog extends StackPane {
    private static final class Constants {
        static final Duration ANIMATION_DURATION = Duration.millis(250);
        static final double MAX_WIDTH = 560;
        static final double MAX_HEIGHT = 400;
        static final double BLUR_RADIUS = 10;
        static final double INITIAL_SCALE = 0.8;
        static final double FINAL_SCALE = 1.0;
        static final Insets HEADER_PADDING = new Insets(24, 24, 0, 24);
        static final Insets CONTENT_PADDING = new Insets(8, 24, 8, 24);
        static final Insets ACTIONS_PADDING = new Insets(8, 24, 16, 24);
        static final double ACTIONS_SPACING = 16;
        static final String OVERLAY_STYLE = "-fx-background-color: rgba(0, 0, 0, 0.5);";
    }

    private final DialogLayout layout;
    private final DialogAnimator animator;
    private final Region overlay;
    private Node blurTarget;
    private Runnable onCloseCallback;

    private UIDialog() {
        this.overlay = createOverlay();
        this.layout = new DialogLayout();
        this.animator = new DialogAnimator(this);
        initialize();
    }

    private void initialize() {
        this.setAlignment(Pos.CENTER);
        this.setVisible(false);
        this.setManaged(false);
        this.getChildren().addAll(this.overlay, this.layout);

        setupOverlayBehavior();
        updateStyle();
        ThemeManager.addListener(palette -> updateStyle());
    }

    private Region createOverlay() {
        final Region overlay = new Region();
        overlay.setStyle(Constants.OVERLAY_STYLE);
        overlay.setOpacity(0);
        return overlay;
    }

    private void setupOverlayBehavior() {
        this.overlay.setOnMouseClicked(e -> {
            if (this.onCloseCallback != null) {
                this.onCloseCallback.run();
            }
            hide();
        });
    }

    private void updateStyle() {
        final ColorPalette palette = ThemeManager.getCurrentPalette();
        this.layout.updateStyle(palette);
    }

    public void show() {
        this.animator.playShowAnimation();
    }

    public void hide() {
        this.animator.playHideAnimation();
    }

    private static class DialogLayout extends VBox {
        private final VBox headerContainer;
        private final StackPane contentContainer;
        private final HBox actionsContainer;

        DialogLayout() {
            this.headerContainer = new VBox();
            this.contentContainer = new StackPane();
            this.actionsContainer = new HBox(Constants.ACTIONS_SPACING);
            initialize();
        }

        private void initialize() {
            this.setMaxWidth(Constants.MAX_WIDTH);
            this.setMaxHeight(Region.USE_PREF_SIZE);
            this.setSpacing(16);
            this.setScaleX(Constants.INITIAL_SCALE);
            this.setScaleY(Constants.INITIAL_SCALE);
            this.setOpacity(0);

            setupContainers();
            this.getChildren().addAll(this.headerContainer, this.contentContainer, this.actionsContainer);
        }

        private void setupContainers() {
            this.headerContainer.setSpacing(4);
            this.headerContainer.setPadding(Constants.HEADER_PADDING);

            this.contentContainer.setPadding(Constants.CONTENT_PADDING);

            this.actionsContainer.setAlignment(Pos.CENTER_RIGHT);
            this.actionsContainer.setPadding(Constants.ACTIONS_PADDING);
        }

        void setTitle(final Node titleNode) {
            this.headerContainer.getChildren().clear();
            this.headerContainer.getChildren().add(titleNode);
        }

        void setContent(final Node content) {
            final Node wrappedContent = wrapContentIfNeeded(content);
            this.contentContainer.getChildren().setAll(wrappedContent);
        }

        private Node wrapContentIfNeeded(final Node content) {
            if (content.getLayoutBounds().getHeight() > Constants.MAX_HEIGHT) {
                final ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefViewportHeight(Constants.MAX_HEIGHT);
                scrollPane.setStyle("-fx-background-color: transparent;");
                return scrollPane;
            }
            return content;
        }

        void setActions(final Node... actions) {
            this.actionsContainer.getChildren().setAll(actions);
        }

        void updateStyle(final ColorPalette palette) {
            this.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-background-radius: 4;
                -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 24, 0.2, 0, 8);
                """,
                    palette.getSurfaceHex()
            ));
        }
    }

    private static class DialogAnimator {
        private final UIDialog dialog;
        private final Timeline showAnimation;
        private final Timeline hideAnimation;

        DialogAnimator(final UIDialog dialog) {
            this.dialog = dialog;
            this.showAnimation = createShowAnimation();
            this.hideAnimation = createHideAnimation();
        }

        private Timeline createShowAnimation() {
            final Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(this.dialog.overlay.opacityProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(this.dialog.layout.scaleXProperty(), Constants.INITIAL_SCALE, Interpolator.EASE_OUT),
                            new KeyValue(this.dialog.layout.scaleYProperty(), Constants.INITIAL_SCALE, Interpolator.EASE_OUT),
                            new KeyValue(this.dialog.layout.opacityProperty(), 0, Interpolator.EASE_BOTH)
                    ),
                    new KeyFrame(Constants.ANIMATION_DURATION,
                            new KeyValue(this.dialog.overlay.opacityProperty(), 1, Interpolator.EASE_BOTH),
                            new KeyValue(this.dialog.layout.scaleXProperty(), Constants.FINAL_SCALE, Interpolator.EASE_OUT),
                            new KeyValue(this.dialog.layout.scaleYProperty(), Constants.FINAL_SCALE, Interpolator.EASE_OUT),
                            new KeyValue(this.dialog.layout.opacityProperty(), 1, Interpolator.EASE_BOTH)
                    )
            );
            return timeline;
        }

        private Timeline createHideAnimation() {
            final Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(this.dialog.overlay.opacityProperty(), 1, Interpolator.EASE_BOTH),
                            new KeyValue(this.dialog.layout.scaleXProperty(), Constants.FINAL_SCALE, Interpolator.EASE_IN),
                            new KeyValue(this.dialog.layout.scaleYProperty(), Constants.FINAL_SCALE, Interpolator.EASE_IN),
                            new KeyValue(this.dialog.layout.opacityProperty(), 1, Interpolator.EASE_BOTH)
                    ),
                    new KeyFrame(Constants.ANIMATION_DURATION,
                            new KeyValue(this.dialog.overlay.opacityProperty(), 0, Interpolator.EASE_BOTH),
                            new KeyValue(this.dialog.layout.scaleXProperty(), Constants.INITIAL_SCALE, Interpolator.EASE_IN),
                            new KeyValue(this.dialog.layout.scaleYProperty(), Constants.INITIAL_SCALE, Interpolator.EASE_IN),
                            new KeyValue(this.dialog.layout.opacityProperty(), 0, Interpolator.EASE_BOTH)
                    )
            );

            timeline.setOnFinished(e -> {
                this.dialog.setVisible(false);
                this.dialog.setManaged(false);
                if (this.dialog.blurTarget != null) {
                    this.dialog.blurTarget.setEffect(null);
                }
            });

            return timeline;
        }

        void playShowAnimation() {
            this.hideAnimation.stop();
            if (this.dialog.blurTarget != null) {
                final GaussianBlur blur = new GaussianBlur(0);
                this.dialog.blurTarget.setEffect(blur);
                animateBlur(blur, 0, Constants.BLUR_RADIUS);
            }
            this.dialog.setVisible(true);
            this.dialog.setManaged(true);
            this.showAnimation.play();
        }

        void playHideAnimation() {
            this.showAnimation.stop();
            if (this.dialog.blurTarget != null && this.dialog.blurTarget.getEffect() instanceof GaussianBlur blur) {
                animateBlur(blur, Constants.BLUR_RADIUS, 0);
            }
            this.hideAnimation.play();
        }

        private void animateBlur(final GaussianBlur blur, final double fromRadius, final double toRadius) {
            final Timeline blurAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(blur.radiusProperty(), fromRadius)
                    ),
                    new KeyFrame(Constants.ANIMATION_DURATION,
                            new KeyValue(blur.radiusProperty(), toRadius)
                    )
            );
            blurAnimation.play();
        }
    }

    public static class Builder {
        private final UIDialog dialog;
        private String title;
        private Node content;
        private Node[] actions;
        private Node blurTarget;
        private Runnable onCloseCallback;

        public Builder() {
            this.dialog = new UIDialog();
        }

        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        public Builder content(final Node content) {
            this.content = content;
            return this;
        }

        public Builder actions(final Node... actions) {
            this.actions = actions;
            return this;
        }

        public Builder blurTarget(final Node blurTarget) {
            this.blurTarget = blurTarget;
            return this;
        }

        public Builder onClose(final Runnable callback) {
            this.onCloseCallback = callback;
            return this;
        }

        public UIDialog build() {
            if (this.title != null) {
                UILabel titleLabel = new UILabel(this.title);
                titleLabel.setType(UILabel.TextType.HEADING);
                this.dialog.layout.setTitle(titleLabel);
            }

            if (this.content != null) {
                this.dialog.layout.setContent(this.content);
            }

            if (this.actions != null) {
                this.dialog.layout.setActions(this.actions);
            }

            if (this.blurTarget != null) {
                this.dialog.blurTarget = this.blurTarget;
            }

            if (this.onCloseCallback != null) {
                this.dialog.onCloseCallback = this.onCloseCallback;
            }

            return dialog;
        }
    }

    public void updateTitle(final String title) {
        final UILabel titleLabel = new UILabel(title);
        titleLabel.setType(UILabel.TextType.HEADING);
        this.layout.setTitle(titleLabel);
    }

    public void updateTitle(final Node titleNode) {
        this.layout.setTitle(titleNode);
    }

    public void updateContent(final Node content) {
        this.layout.setContent(content);
    }

    public void updateActions(final Node... actions) {
        this.layout.setActions(actions);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static UIDialog createConfirmationDialog(final String title, final String message,
                                                    final String confirmText, final String cancelText,
                                                    final Runnable onConfirm, final Runnable onCancel) {
        return builder()
                .title(title)
                .content(new UILabel(message) {{
                    setType(UILabel.TextType.BODY);
                    setEmphasis(UILabel.TextEmphasis.MEDIUM);
                }})
                .actions(
                        new UIButton(cancelText) {{
                            setType(UIButton.ButtonType.SECONDARY);
                            setOnAction(e -> {
                                if (onCancel != null) onCancel.run();
                            });
                        }},
                        new UIButton(confirmText) {{
                            setType(UIButton.ButtonType.PRIMARY);
                            setOnAction(e -> {
                                if (onConfirm != null) onConfirm.run();
                            });
                        }}
                )
                .build();
    }
}