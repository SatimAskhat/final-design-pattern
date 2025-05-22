import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления текстурами игры
 */
public class TextureManager {
    private static TextureManager instance;
    private Map<String, BufferedImage> textures;
    
    // Константы для анимации
    public static final int ANIMATION_FRAME_DELAY = 10; // Задержка между кадрами анимации
    
    private TextureManager() {
        textures = new HashMap<>();
    }
    
    /**
     * Получение экземпляра менеджера текстур (Singleton)
     */
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }
    
    /**
     * Загрузка всех текстур
     */
    public void loadAllTextures() {
        // Загрузка тайлов
        loadTilesTextures();
        
        // Загрузка текстур персонажей
        loadCharacterTextures();
        
        // Загрузка специальных текстур
        loadSpecialTextures();
        
        System.out.println("Загружено текстур: " + textures.size());
    }
    
    /**
     * Загрузка текстур тайлов
     */
    private void loadTilesTextures() {
        // Пробуем несколько возможных путей
        File tilesDir = new File("src/textures/Tiles");
        if (!tilesDir.exists() || !tilesDir.isDirectory()) {
            tilesDir = new File("textures/Tiles");
            if (!tilesDir.exists()) {
                tilesDir.mkdirs();
                System.out.println("Создана директория для текстур тайлов: " + tilesDir.getAbsolutePath());
            }
        }
        
        if (tilesDir.exists() && tilesDir.isDirectory()) {
            File[] tileFiles = tilesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") && name.startsWith("tile_"));
            
            if (tileFiles != null && tileFiles.length > 0) {
                for (File tileFile : tileFiles) {
                    try {
                        BufferedImage image = ImageIO.read(tileFile);
                        String textureName = tileFile.getName().replace(".png", "");
                        textures.put(textureName, image);
                    } catch (IOException e) {
                        System.err.println("Ошибка загрузки текстуры: " + tileFile.getName());
                        // Создаем пустую текстуру вместо отсутствующей
                        BufferedImage emptyImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                        String textureName = tileFile.getName().replace(".png", "");
                        textures.put(textureName, emptyImage);
                    }
                }
            } else {
                System.out.println("Текстуры тайлов не найдены в " + tilesDir.getAbsolutePath());
            }
        }
    }
    
    /**
     * Загрузка текстур персонажей
     */
    private void loadCharacterTextures() {
        // Пробуем несколько возможных путей
        File charactersDir = new File("src/textures/Tiles/Characters");
        if (!charactersDir.exists() || !charactersDir.isDirectory()) {
            charactersDir = new File("textures/Tiles/Characters");
            if (!charactersDir.exists()) {
                charactersDir.mkdirs();
                System.out.println("Создана директория для текстур персонажей: " + charactersDir.getAbsolutePath());
            }
        }
        
        if (charactersDir.exists() && charactersDir.isDirectory()) {
            File[] characterFiles = charactersDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") && name.startsWith("tile_"));
            
            if (characterFiles != null && characterFiles.length > 0) {
                for (File characterFile : characterFiles) {
                    try {
                        BufferedImage image = ImageIO.read(characterFile);
                        String textureName = "character_" + characterFile.getName().replace(".png", "");
                        textures.put(textureName, image);
                    } catch (IOException e) {
                        System.err.println("Ошибка загрузки текстуры персонажа: " + characterFile.getName());
                        // Создаем пустую текстуру вместо отсутствующей
                        BufferedImage emptyImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                        String textureName = "character_" + characterFile.getName().replace(".png", "");
                        textures.put(textureName, emptyImage);
                    }
                }
            } else {
                System.out.println("Текстуры персонажей не найдены в " + charactersDir.getAbsolutePath());
            }
        }
    }
    
    /**
     * Загрузка специальных текстур
     */
    private void loadSpecialTextures() {
        // Загрузка текстуры монеты
        String coinTextureName = "204705246_w600_h600_204705246-removebg-preview";
        boolean loaded = false;
        
        // Пробуем несколько возможных путей
        String[] possiblePaths = {
            "src/textures/Tiles/" + coinTextureName + ".png",
            "textures/Tiles/" + coinTextureName + ".png",
            "src/" + coinTextureName + ".png",
            coinTextureName + ".png"
        };
        
        for (String path : possiblePaths) {
            File coinFile = new File(path);
            if (coinFile.exists()) {
                try {
                    BufferedImage coinImage = ImageIO.read(coinFile);
                    textures.put(coinTextureName, coinImage);
                    System.out.println("Загружена специальная текстура монеты из: " + path);
                    loaded = true;
                    break;
                } catch (IOException e) {
                    System.err.println("Ошибка загрузки текстуры монеты из " + path + ": " + e.getMessage());
                }
            }
        }
        
        // Если не удалось загрузить текстуру монеты, создаем пустую текстуру
        if (!loaded) {
            System.out.println("Создаем пустую текстуру монеты");
            BufferedImage emptyImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            textures.put(coinTextureName, emptyImage);
            
            // Создаем директорию для текстур, если её нет
            File tilesDir = new File("textures/Tiles");
            if (!tilesDir.exists()) {
                tilesDir.mkdirs();
            }
        }
    }
    
    /**
     * Получение текстуры по имени
     */
    public BufferedImage getTexture(String name) {
        return textures.get(name);
    }
    
    /**
     * Проверка наличия текстуры
     */
    public boolean hasTexture(String name) {
        return textures.containsKey(name);
    }
}
