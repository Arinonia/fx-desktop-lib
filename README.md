# FxDesktopLib

A modern, theme-aware JavaFX component library designed to create beautiful desktop applications. FxDesktopLib provides a collection of pre-built UI components with consistent styling, smooth animations, and support for light/dark themes.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java Version](https://img.shields.io/badge/Java-21-orange.svg)
![JavaFX Version](https://img.shields.io/badge/JavaFX-21-green.svg)
![Build Status](https://github.com/Arinonia/fx-desktop-lib/actions/workflows/build.yml/badge.svg)
## Features

- 🎨 Multiple built-in themes (Dark, Light, Palenight, Dracula, Nord, Oceanic)
- 🔄 Smooth animations and transitions
- 📱 Modern, Material Design-inspired components
- 🌗 Easy theme switching with live updates


## Components

- `UIButton` - Enhanced button with hover effects and multiple states
- `UICard` - Container with elevation and hover animations
- `UICheckBox` - Customizable checkbox with support for indeterminate state
- `UIComboBox` - Styled dropdown with smooth transitions
- `UIDialog` - Modal dialog with backdrop blur
- `UILabel` - Text component with different styles and emphasis levels
- `UIScrollPane` - Custom scrolling container with smooth scrolling
- `UITextField` - Text input with validation and error states
- `UIToggleButton` - Toggle switch with animations

## Getting Started

### Prerequisites

- Java 21 or higher
- JavaFX 21 or higher
- Gradle 8.5 or higher

### Installation

Add the following to your `build.gradle`:

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'fr.arinonia:fx-desktop-lib:0.1.0'
}
```

### Basic Usage

```java
public class DemoApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize UI Configuration
        UIConfiguration config = new UIConfiguration()
            .setTitle("Demo App")
            .setWidth(1280)
            .setHeight(720);

        // Create UI Manager
        UIManager uiManager = new UIManager(primaryStage, config);

        // Create a button with animation
        UIButton button = new UIButton("Click Me");
        button.setType(UIButton.ButtonType.PRIMARY);

        // Create a container
        UICard card = UICard.builder()
            .hoverable(true)
            .elevation(2)
            .children(button)
            .build();

        // Set theme
        ThemeManager.setCurrentPalette(Themes.getDark());

        primaryStage.show();
    }
}
```

## Themes

FxDesktopLib comes with six built-in themes:

- Dark (default)
- Light
- Palenight
- Dracula
- Nord
- Oceanic

Switching themes is as simple as:

```java
ThemeManager.setCurrentPalette(Themes.getLight());
```

## Documentation

For detailed documentation and examples, visit our [Wiki](https://github.com/Arinonia/fx-desktop-lib/wiki).

## Building from Source

```bash
git clone https://github.com/Arinonia/fx-desktop-lib.git
cd fx-desktop-lib
./gradlew build
```

## Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Credits

Created by Arinonia with ❤️

## Support

If you find a bug or want to request a feature, please [create an issue](https://github.com/Arinonia/fx-desktop-lib/issues).
