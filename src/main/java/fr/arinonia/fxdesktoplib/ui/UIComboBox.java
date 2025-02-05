package fr.arinonia.fxdesktoplib.ui;

import fr.arinonia.fxdesktoplib.theme.ColorPalette;
import fr.arinonia.fxdesktoplib.theme.ThemeManager;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class UIComboBox<T> extends StackPane {


    private static final double DEFAULT_BORDER_RADIUS = 5;
    private static final double DEFAULT_PADDING = 4;
    private static final double DEFAULT_CELL_PADDING = 8;
    private static final double SCROLLBAR_THUMB_RADIUS = 3;
    private static final double HOVER_COLOR_OPACITY = 0.2;


    private final ComboBox<T> comboBox;


    public UIComboBox() {
        this.comboBox = new ComboBox<>();
        initialize();
    }


    private void initialize() {
        setupComboBox();
        setupThemeListener();
        this.getChildren().add(this.comboBox);
    }

    private void setupComboBox() {
        this.comboBox.setMaxWidth(Double.MAX_VALUE);
        setupCellFactory();
        applyInitialStyle();
        setupPopupListener();
    }

    private void setupCellFactory() {
        this.comboBox.setCellFactory(listView -> {
            ListCell<T> cell = createCell();
            setupListViewListener(listView);
            return cell;
        });
        this.comboBox.setButtonCell(createCell());
    }

    private void setupListViewListener(final ListView<T> listView) {
        listView.getItems().addListener((ListChangeListener<? super T>) c ->
                updatePopupStyle(ThemeManager.getCurrentPalette()));
    }

    private void setupPopupListener() {
        this.comboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                updatePopupStyle(ThemeManager.getCurrentPalette());
            }
        });
    }

    private void applyInitialStyle() {
        updateStyle(ThemeManager.getCurrentPalette());
    }


    private ListCell<T> createCell() {
        return new StyleableListCell();
    }

    private class StyleableListCell extends ListCell<T> {
        @Override
        protected void updateItem(final T item, final boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                this.setText(null);
            } else {
                final StringConverter<T> converter = comboBox.getConverter();
                this.setText(converter != null ? converter.toString(item) : item.toString());
            }
            updateCellStyle(this);
        }

        @Override
        public void updateSelected(final boolean selected) {
            super.updateSelected(selected);
            updateCellStyle(this);
        }
    }

    private void updateStyle(final ColorPalette palette) {
        applyComboBoxStyle(palette);
        applyArrowStyle(palette);
        updateButtonCell();
    }

    private void applyComboBoxStyle(final ColorPalette palette) {
        this.comboBox.setStyle(String.format("""
                        -fx-background-color: %s;
                        -fx-text-fill: %s;
                        -fx-border-color: %s;
                        -fx-border-radius: %.1f;
                        -fx-background-radius: %.1f;
                        -fx-padding: %.1f %.1f;
                        """,
                palette.getSurfaceHex(),
                palette.getOnSurfaceHex(),
                colorWithOpacity(palette.getOnSurface(), HOVER_COLOR_OPACITY),
                DEFAULT_BORDER_RADIUS,
                DEFAULT_BORDER_RADIUS,
                DEFAULT_PADDING,
                DEFAULT_CELL_PADDING
        ));
    }

    private void applyArrowStyle(final ColorPalette palette) {
        if (this.comboBox.lookup(".arrow") != null) {
            this.comboBox.lookup(".arrow").setStyle(String.format("""
                            -fx-background-color: %s;
                            """,
                    palette.getOnSurfaceHex()
            ));
        }
    }

    private void updateButtonCell() {
        if (this.comboBox.getButtonCell() != null) {
            updateCellStyle(this.comboBox.getButtonCell());
        }
    }

    private void updatePopupStyle(final ColorPalette palette) {
        if (this.comboBox.lookup(".list-view") != null) {
            applyListViewStyle(palette);
            applyViewportStyle(palette);
            applyScrollBarStyles(palette);
            updateAllCells();
        }
    }

    private void applyListViewStyle(final ColorPalette palette) {
        this.comboBox.lookup(".list-view").setStyle(String.format("""
                        -fx-background-color: %s !important;
                        -fx-border-color: %s !important;
                        -fx-border-radius: %.1f;
                        -fx-background-radius: %.1f;
                        -fx-padding: 2;
                        """,
                palette.getSurfaceHex(),
                colorWithOpacity(palette.getOnSurface(), HOVER_COLOR_OPACITY),
                DEFAULT_BORDER_RADIUS,
                DEFAULT_BORDER_RADIUS
        ));
    }

    private void applyViewportStyle(final ColorPalette palette) {
        if (this.comboBox.lookup(".list-view .viewport") != null) {
            this.comboBox.lookup(".list-view .viewport").setStyle(String.format("""
                            -fx-background-color: %s !important;
                            """,
                    palette.getSurfaceHex()
            ));
        }
    }

    private void applyScrollBarStyles(final ColorPalette palette) {
        this.comboBox.lookupAll(".list-view .scroll-bar").forEach(node ->
                node.setStyle("-fx-background-color: transparent !important;")
        );

        this.comboBox.lookupAll(".list-view .scroll-bar .track").forEach(node ->
                node.setStyle("-fx-background-color: transparent !important;")
        );

        this.comboBox.lookupAll(".list-view .scroll-bar .thumb").forEach(node ->
                node.setStyle(String.format("""
                                -fx-background-color: %s !important;
                                -fx-background-radius: %.1f;
                                """,
                        colorWithOpacity(palette.getOnSurface(), 0.3),
                        SCROLLBAR_THUMB_RADIUS
                ))
        );
    }

    private void updateAllCells() {
        this.comboBox.lookupAll(".list-cell").forEach(node -> {
            if (node instanceof final ListCell<?> cell) {
                updateCellStyle((ListCell<T>) cell);
            }
        });
    }

    private void updateCellStyle(final ListCell<T> cell) {
        final ColorPalette palette = ThemeManager.getCurrentPalette();
        final boolean isSelected = cell.isSelected();
        applyCellStyle(cell, palette, isSelected);
        setupCellHoverHandlers(cell, palette, isSelected);
    }

    private void applyCellStyle(final ListCell<T> cell, final ColorPalette palette, final boolean isSelected) {
        final String backgroundColor = isSelected ? palette.getPrimaryHex() : "transparent";
        final String textColor = isSelected ? palette.getOnPrimaryHex() : palette.getOnSurfaceHex();

        cell.setStyle(String.format("""
                        -fx-background-color: %s !important;
                        -fx-text-fill: %s !important;
                        -fx-padding: %.1f %.1f;
                        """,
                backgroundColor,
                textColor,
                DEFAULT_PADDING,
                DEFAULT_CELL_PADDING
        ));
    }

    private void setupCellHoverHandlers(final ListCell<T> cell, final ColorPalette palette, final boolean isSelected) {
        if (!isSelected) {
            cell.setOnMouseEntered(e -> applyCellHoverStyle(cell, palette, true));
            cell.setOnMouseExited(e -> applyCellHoverStyle(cell, palette, false));
        } else {
            cell.setOnMouseEntered(null);
            cell.setOnMouseExited(null);
        }
    }

    private void applyCellHoverStyle(final ListCell<T> cell, final ColorPalette palette, final boolean isHovered) {
        final String backgroundColor = isHovered ?
                colorWithOpacity(palette.getPrimary(), HOVER_COLOR_OPACITY) : "transparent";

        cell.setStyle(String.format("""
                        -fx-background-color: %s !important;
                        -fx-text-fill: %s !important;
                        -fx-padding: %.1f %.1f;
                        """,
                backgroundColor,
                palette.getOnSurfaceHex(),
                DEFAULT_PADDING,
                DEFAULT_CELL_PADDING
        ));
    }

    private void setupThemeListener() {
        ThemeManager.addListener(palette -> {
            updateStyle(palette);
            handleThemeChange(palette);
        });
    }

    private void handleThemeChange(final ColorPalette palette) {
        if (this.comboBox.isShowing()) {
            final T selectedItem = this.comboBox.getSelectionModel().getSelectedItem();
            refreshComboBoxSkin();
            restoreComboBoxState(selectedItem, palette);
        } else {
            refreshComboBoxSkin();
        }
    }

    private void refreshComboBoxSkin() {
        this.comboBox.setSkin(null);
        this.comboBox.applyCss();
        this.comboBox.layout();
        this.comboBox.setCellFactory(listView -> createCell());
        this.comboBox.setButtonCell(createCell());
    }

    private void restoreComboBoxState(final T selectedItem, final ColorPalette palette) {
        this.comboBox.hide();
        Platform.runLater(() -> {
            this.comboBox.getSelectionModel().select(selectedItem);
            this.comboBox.show();
            updatePopupStyle(palette);
        });
    }

    //! should maybe be moved to a utility class
    private String colorWithOpacity(final Color color, final double opacity) {
        return String.format("rgba(%d, %d, %d, %.2f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                opacity
        );
    }

    public void setItems(final ObservableList<T> items) {
        this.comboBox.setItems(items);
    }

    public void setPromptText(final String text) {
        this.comboBox.setPromptText(text);
    }

    public T getSelectedItem() {
        return this.comboBox.getSelectionModel().getSelectedItem();
    }

    public void setSelectedItem(final T item) {
        this.comboBox.getSelectionModel().select(item);
    }

    public void setConverter(final StringConverter<T> converter) {
        this.comboBox.setConverter(converter);
    }

    public ComboBox<T> getComboBox() {
        return this.comboBox;
    }
}