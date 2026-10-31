import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Splits 1.20-style connected-texture sheets ({@code <name>_connected.png}) into the per-tile sprites Create Fly
 * loads ({@code <name>_connected/<index>.png}, index 0 being the base texture itself), then deletes the sheet.
 *
 * <p>The tile -> sheet cell tables were derived by pixel-matching Create Fly's split tiles against the sheets they
 * replaced (Create Fly commit 48e17b9e "Update CTModel sprite"; andesite/brass casing for omnidirectional,
 * (creative) fluid tank for rectangle). 2x2 sheets (horizontal kryppers, vertical, Railways' vertical pinkmachine)
 * keep their 1.20 indices, so tile N is cell N.</p>
 *
 * <p>D&D: the dyed container sheets ({@code <name>_(top|front|side|bottom)_(medium|large).png}, no {@code _connected}
 * suffix) use Create Fly's item vault CT types; their tables were derived from those types' flag maps and upstream
 * RECTANGLE's cell rule (x: none 0, right 1, both 2, left 3; y: down 0, both 1, up 2, none 3).</p>
 *
 * <p>Usage, from the repo root: {@code java porting-tools/SplitConnectedTextures.java src/main/resources/assets/dndecor/textures}</p>
 */
public class SplitConnectedTextures {
    private static final int[] TWO_BY_TWO = {0, 1, 2, 3};
    // AllCTTypes.RECTANGLE (4x4 sheet)
    private static final int[] RECTANGLE = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15};
    // AllCTTypes.OMNIDIRECTIONAL (8x8 sheet)
    private static final int[] OMNIDIRECTIONAL = {-1,
        1, 2, 3, 8, 9, 10, 11, 12, 13, 16, 17, 18, 19, 20, 21, 24, 25, 26, 27, 28, 29, 30,
        32, 33, 34, 35, 36, 37, 38, 40, 41, 42, 43, 44, 45, 46, 48, 49, 50, 51, 52, 53, 54, 56, 57, 58};

    // D&D containers, as Create Fly's vault: RECTANGLE_WITHOUT_VERTICAL_MIDDLE, RECTANGLE_WITHOUT_HORIZONTAL_SINGLE, SQUARE,
    // RECTANGLE_WITHOUT_SINGLE (index 0 unused: tile 0 is the _small base texture)
    private static final int[] WITHOUT_VERTICAL_MIDDLE = {-1, 0, 1, 3, 4, 5, 7, 8, 9, 11, 13, 15};
    private static final int[] WITHOUT_HORIZONTAL_SINGLE = {-1, 1, 2, 3, 5, 6, 7, 9, 10, 11, 13, 14, 15};
    private static final int[] SQUARE = {-1, 1, 3, 9, 11};
    private static final int[] WITHOUT_SINGLE = {-1, 1, 2, 3, 5, 6, 7, 9, 10, 11};
    private static final java.util.regex.Pattern CONTAINER = java.util.regex.Pattern.compile("(.*)(top|front|side|bottom)_(medium|large)[.]png");

    static int[] containerMap(String face, String size) {
        boolean large = size.equals("large");
        if (face.equals("front")) return large ? WITHOUT_SINGLE : SQUARE;
        return large ? WITHOUT_HORIZONTAL_SINGLE : WITHOUT_VERTICAL_MIDDLE;
    }

    static int splitContainers(Path root) throws IOException {
        List<Path> sheets;
        try (Stream<Path> files = Files.walk(root)) {
            sheets = files.filter(p -> p.toString().replace(java.io.File.separatorChar, '/').contains("/container") && CONTAINER.matcher(p.getFileName().toString()).matches()).sorted().toList();
        }
        for (Path sheetPath : sheets) {
            var m = CONTAINER.matcher(sheetPath.getFileName().toString());
            m.matches();
            Path base = sheetPath.resolveSibling(m.group(1) + m.group(2) + "_small.png");
            if (!Files.exists(base))
                throw new IllegalStateException("no small texture for " + sheetPath);
            BufferedImage sheet = ImageIO.read(sheetPath.toFile());
            int size = ImageIO.read(base.toFile()).getWidth();
            if (sheet.getWidth() / size != 4)
                throw new IllegalStateException(sheetPath + ": expected a 4x4 sheet");
            int[] map = containerMap(m.group(2), m.group(3));
            Path dir = sheetPath.resolveSibling(sheetPath.getFileName().toString().replace(".png", ""));
            Files.createDirectories(dir);
            for (int tile = 1; tile < map.length; tile++) {
                int cell = map[tile];
                ImageIO.write(sheet.getSubimage((cell % 4) * size, (cell / 4) * size, size, size), "png", dir.resolve(tile + ".png").toFile());
            }
            Files.delete(sheetPath);
        }
        return sheets.size();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("split " + splitContainers(Path.of(args[0])) + " container sheets");
        List<Path> sheets;
        try (Stream<Path> files = Files.walk(Path.of(args[0]))) {
            sheets = files.filter(p -> p.getFileName().toString().endsWith("_connected.png")).sorted().toList();
        }
        for (Path sheetPath : sheets) {
            String name = sheetPath.getFileName().toString();
            Path base = sheetPath.resolveSibling(name.replace("_connected.png", ".png"));
            if (!Files.exists(base))
                throw new IllegalStateException("no base texture for " + sheetPath);
            BufferedImage sheet = ImageIO.read(sheetPath.toFile());
            int size = ImageIO.read(base.toFile()).getWidth();
            int cells = sheet.getWidth() / size;
            int[] map = switch (cells) {
                case 2 -> TWO_BY_TWO;
                case 4 -> RECTANGLE;
                case 8 -> OMNIDIRECTIONAL;
                default -> throw new IllegalStateException(sheetPath + ": unexpected " + cells + "x" + cells + " sheet");
            };

            Path dir = sheetPath.resolveSibling(name.replace(".png", ""));
            Files.createDirectories(dir);
            for (int tile = 1; tile < map.length; tile++) {
                int cell = map[tile];
                BufferedImage out = sheet.getSubimage((cell % cells) * size, (cell / cells) * size, size, size);
                ImageIO.write(out, "png", dir.resolve(tile + ".png").toFile());
            }
            Files.delete(sheetPath);
        }
        System.out.println("split " + sheets.size() + " connected texture sheets");
    }
}
