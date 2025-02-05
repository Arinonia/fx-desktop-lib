package fr.arinonia.fxdesktoplib.theme;

public class Themes {
    private static final ColorPalette DARK = new ColorPalette.Builder()
            .name("Dark")
            .primary(149, 128, 255)
            .primaryVariant(129, 108, 235)
            .secondary(48, 25, 88)
            .secondaryVariant(38, 15, 78)
            .background(24, 24, 37)
            .surface(32, 32, 45)
            .accent(255, 128, 149)
            .error(255, 69, 58)
            .warning(255, 159, 10)
            .success(48, 209, 88)
            .onBackground(255, 255, 255)
            .onSurface(255, 255, 255)
            .onPrimary(255, 255, 255)
            .onSecondary(255, 255, 255)
            .build();

    private static final ColorPalette LIGHT = new ColorPalette.Builder()
            .name("Light")
            .primary(98, 0, 238)
            .primaryVariant(55, 0, 179)
            .secondary(3, 218, 198)
            .secondaryVariant(1, 135, 134)
            .background(245, 245, 245)
            .surface(255, 255, 255)
            .accent(255, 128, 149)
            .error(176, 0, 32)
            .warning(255, 152, 0)
            .success(76, 175, 80)
            .onBackground(33, 33, 33)
            .onSurface(33, 33, 33)
            .onPrimary(255, 255, 255)
            .onSecondary(255, 255, 255)
            .build();

    private static final ColorPalette PALENIGHT = new ColorPalette.Builder()
            .name("Palenight")
            .primary(199, 146, 234)
            .primaryVariant(179, 126, 214)
            .secondary(92, 103, 153)
            .secondaryVariant(72, 83, 133)
            .background(41, 45, 62)
            .surface(34, 39, 54)
            .accent(137, 221, 255)
            .error(255, 85, 85)
            .warning(255, 198, 109)
            .success(195, 232, 141)
            .onBackground(255, 255, 255)
            .onSurface(255, 255, 255)
            .onPrimary(33, 33, 33)
            .onSecondary(255, 255, 255)
            .build();

    private static final ColorPalette DRACULA = new ColorPalette.Builder()
            .name("Dracula")
            .primary(189, 147, 249)
            .primaryVariant(169, 127, 229)
            .secondary(255, 121, 198)
            .secondaryVariant(235, 101, 178)
            .background(40, 42, 54)
            .surface(68, 71, 90)
            .accent(80, 250, 123)
            .error(255, 85, 85)
            .warning(255, 184, 108)
            .success(80, 250, 123)
            .onBackground(248, 248, 242)
            .onSurface(248, 248, 242)
            .onPrimary(33, 33, 33)
            .onSecondary(33, 33, 33)
            .build();

    private static final ColorPalette NORD = new ColorPalette.Builder()
            .name("Nord")
            .primary(136, 192, 208)
            .primaryVariant(129, 161, 193)
            .secondary(94, 129, 172)
            .secondaryVariant(76, 86, 106)
            .background(46, 52, 64)
            .surface(59, 66, 82)
            .accent(180, 142, 173)
            .error(191, 97, 106)
            .warning(235, 203, 139)
            .success(163, 190, 140)
            .onBackground(229, 233, 240)
            .onSurface(229, 233, 240)
            .onPrimary(33, 33, 33)
            .onSecondary(255, 255, 255)
            .build();

    private static final ColorPalette OCEANIC = new ColorPalette.Builder()
            .name("Oceanic")
            .primary(102, 217, 239)
            .primaryVariant(82, 197, 219)
            .secondary(79, 91, 102)
            .secondaryVariant(59, 71, 82)
            .background(27, 43, 52)
            .surface(34, 50, 59)
            .accent(199, 146, 234)
            .error(249, 38, 114)
            .warning(253, 151, 31)
            .success(166, 226, 46)
            .onBackground(237, 237, 237)
            .onSurface(237, 237, 237)
            .onPrimary(33, 33, 33)
            .onSecondary(255, 255, 255)
            .build();

    public static ColorPalette getDark() { return DARK; }
    public static ColorPalette getLight() { return LIGHT; }
    public static ColorPalette getPalenight() { return PALENIGHT; }
    public static ColorPalette getDracula() { return DRACULA; }
    public static ColorPalette getNord() { return NORD; }
    public static ColorPalette getOceanic() { return OCEANIC; }

}