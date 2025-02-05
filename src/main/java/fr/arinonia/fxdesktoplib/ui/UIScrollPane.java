package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class UIScrollPane extends ScrollPane {

    private static final class StyleConstants {
        static final class ScrollBar {
            static final double WIDTH = 8;
            static final double RADIUS = 4;
            static final double OPACITY = 0.5;
            static final double HOVER_OPACITY = 0.8;
        }

        static final class Scroll {
            static final double SPEED = 0.015;
            static final Duration DURATION = Duration.millis(200);
            static final double MIN_DELTA = 0.1;
        }
    }

    private final ScrollBarManager scrollBarManager;
    private final SmoothScrollManager smoothScrollManager;
    private final StyleManager styleManager;

    public UIScrollPane() {
        this(null);
    }

    public UIScrollPane(final Node content) {
        super(content);
        this.scrollBarManager = new ScrollBarManager(this);
        this.smoothScrollManager = new SmoothScrollManager(this);
        this.styleManager = new StyleManager(this);
        initialize();
    }

    private void initialize() {
        this.setFitToWidth(true);
        this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setPadding(new Insets(0));

        setupListeners();

        this.styleManager.updateStyle();
    }

    private void setupListeners() {
        this.setOnScroll(event -> {
            this.smoothScrollManager.handleScroll(event.getDeltaY(), event.getDeltaX());
            event.consume();
        });

        ThemeManager.addListener(palette -> this.styleManager.updateStyle());

        this.skinProperty().addListener((obs, old, newSkin) -> {
            if (newSkin != null) {
                this.scrollBarManager.initialize();
            }
        });

        this.contentProperty().addListener((obs, old, newContent) -> {
            if (newContent != null) {
                this.styleManager.updateStyle();
            }
        });

        this.viewportBoundsProperty().addListener((obs, old, newBounds) -> {
            this.styleManager.updateStyle();
        });

        this.sceneProperty().addListener((obs, old, newScene) -> {
            if (newScene != null) {
                this.styleManager.updateStyle();
            }
        });
    }

    private class SmoothScrollManager {
        private final UIScrollPane scrollPane;

        SmoothScrollManager(final UIScrollPane scrollPane) {
            this.scrollPane = scrollPane;
        }

        void handleScroll(final double deltaY, final double deltaX) {
            animateScroll(deltaY, true);
            animateScroll(deltaX, false);
        }

        private void animateScroll(final double delta, final boolean vertical) {
            if (Math.abs(delta) <= StyleConstants.Scroll.MIN_DELTA) return;

            final double scrollDelta = -delta * StyleConstants.Scroll.SPEED;
            final double currentValue = vertical ? getVvalue() : getHvalue();
            final double targetValue = Math.min(Math.max(currentValue + scrollDelta, 0), 1);

            final Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(vertical ? vvalueProperty() : hvalueProperty(), currentValue)),
                    new KeyFrame(StyleConstants.Scroll.DURATION,
                            new KeyValue(vertical ? vvalueProperty() : hvalueProperty(),
                                    targetValue, Interpolator.EASE_BOTH))
            );
            timeline.play();
        }
    }

    private class ScrollBarManager {
        private final UIScrollPane scrollPane;

        ScrollBarManager(final UIScrollPane scrollPane) {
            this.scrollPane = scrollPane;
        }

        void initialize() {
            for (final Node node : this.scrollPane.lookupAll(".scroll-bar")) {
                if (node instanceof final ScrollBar scrollBar) {
                    setupScrollBar(scrollBar);
                }
            }
        }

        private void setupScrollBar(final ScrollBar scrollBar) {
            styleScrollBar(scrollBar);
            hideScrollBarButtons(scrollBar);
        }

        private void hideScrollBarButtons(final ScrollBar scrollBar) {
            for (final Node button : scrollBar.lookupAll(".increment-button, .decrement-button")) {
                button.setStyle("-fx-background-color: transparent;");
                button.setVisible(false);
                button.setManaged(false);
            }
        }

        private void styleScrollBar(final ScrollBar scrollBar) {
            final ColorPalette palette = ThemeManager.getCurrentPalette();

            scrollBar.setStyle(createScrollBarStyle(palette));

            styleTrack(scrollBar, palette);

            styleThumb(scrollBar, palette);
        }

        private String createScrollBarStyle(final ColorPalette palette) {
            return String.format("""
                -fx-background-color: %s;
                -fx-pref-width: %fpx;
                -fx-max-width: %fpx;
                -fx-pref-height: %fpx;
                -fx-max-height: %fpx;
                """,
                    palette.getSurfaceHex(),
                    StyleConstants.ScrollBar.WIDTH,
                    StyleConstants.ScrollBar.WIDTH,
                    StyleConstants.ScrollBar.WIDTH,
                    StyleConstants.ScrollBar.WIDTH
            );
        }

        private void styleTrack(final ScrollBar scrollBar, final ColorPalette palette) {
            final Region track = (Region) scrollBar.lookup(".track");
            if (track != null) {
                track.setStyle(String.format("""
                    -fx-background-color: %s;
                    -fx-opacity: 0.1;
                    -fx-background-radius: %f;
                    -fx-background-insets: 0;
                    """,
                        palette.getSurfaceHex(),
                        StyleConstants.ScrollBar.RADIUS
                ));
            }
        }

        private void styleThumb(final ScrollBar scrollBar, final ColorPalette palette) {
            final Region thumb = (Region) scrollBar.lookup(".thumb");
            if (thumb != null) {
                setupThumbStyles(thumb, palette);
            }
        }

        private void setupThumbStyles(final Region thumb, final ColorPalette palette) {
            final String normalStyle = createThumbStyle(palette, StyleConstants.ScrollBar.OPACITY);
            final String hoverStyle = createThumbStyle(palette, StyleConstants.ScrollBar.HOVER_OPACITY);

            thumb.setStyle(normalStyle);
            thumb.setOnMouseEntered(e -> thumb.setStyle(hoverStyle));
            thumb.setOnMouseExited(e -> thumb.setStyle(normalStyle));
        }

        private String createThumbStyle(final ColorPalette palette, final double opacity) {
            return String.format("""
                -fx-background-color: %s !important;
                -fx-background-radius: %fpx;
                -fx-opacity: %f;
                -fx-background-insets: 2;
                """,
                    palette.getPrimaryHex(),
                    StyleConstants.ScrollBar.RADIUS,
                    opacity
            );
        }
    }

    private class StyleManager {
        private final UIScrollPane scrollPane;

        StyleManager(final UIScrollPane scrollPane) {
            this.scrollPane = scrollPane;
        }

        void updateStyle() {
            ColorPalette palette = ThemeManager.getCurrentPalette();

            applyBaseStyle(palette);
            applyViewportStyle(palette);
            applyContentStyle(palette);
            this.scrollPane.scrollBarManager.initialize();
        }

        private void applyBaseStyle(final ColorPalette palette) {
            this.scrollPane.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-padding: 0;
                -fx-background-insets: 0;
                """, palette.getSurfaceHex()));
        }

        private void applyViewportStyle(final ColorPalette palette) {
            final Node viewport = this.scrollPane.lookup(".viewport");
            if (viewport != null) {
                viewport.setStyle(String.format("""
                    -fx-background-color: %s;
                    -fx-padding: 0;
                    -fx-background-insets: 0;
                    """, palette.getSurfaceHex()));
            }
        }

        private void applyContentStyle(final ColorPalette palette) {
            final String[] selectors = {
                    ".viewport > *",
                    ".scroll-pane > .viewport > *",
                    ".scroll-pane > .viewport > .content",
                    ".viewport .content"
            };

            for (final String selector : selectors) {
                final Node node = scrollPane.lookup(selector);
                if (node != null) {
                    node.setStyle(String.format("""
                        -fx-background-color: %s;
                        """, palette.getBackgroundHex()));
                }
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UIScrollPane scrollPane;

        private Builder() {
            this.scrollPane = new UIScrollPane();
        }

        public Builder content(final Node content) {
            this.scrollPane.setContent(content);
            return this;
        }

        public Builder fitToWidth(final boolean fit) {
            this.scrollPane.setFitToWidth(fit);
            return this;
        }

        public Builder fitToHeight(final boolean fit) {
            this.scrollPane.setFitToHeight(fit);
            return this;
        }

        public UIScrollPane build() {
            return this.scrollPane;
        }
    }
}