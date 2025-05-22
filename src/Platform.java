import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Класс платформы
 */
public class Platform extends GameObject {
    private BufferedImage texture;
    private BufferedImage groundTexture; // Текстура для земли под травой
    private static final int TILE_SIZE = 16; // Размер тайла в пикселях
    private boolean isGround = false; // Является ли платформа землей
    
    /**
     * Конструктор для обычной платформы
     */
    public Platform(double x, double y, double width, double height) {
        super(x, y, width, height);
        loadTexture();
    }
    
    /**
     * Конструктор с указанием типа платформы (земля или обычная)
     */
    public Platform(double x, double y, double width, double height, boolean isGround) {
        super(x, y, width, height);
        this.isGround = isGround;
        loadTexture();
    }
    
    /**
     * Загрузка текстуры платформы
     */
    private void loadTexture() {
        TextureManager textureManager = TextureManager.getInstance();
        
        if (isGround) {
            // Для земли используем текстуру травы и земли под ней
            texture = textureManager.getTexture("tile_0001");     // Текстура травы сверху
            groundTexture = textureManager.getTexture("tile_0004"); // Текстура земли под травой
        } else {
            // Для обычных платформ используем стандартную текстуру
            texture = textureManager.getTexture("tile_0001"); 
        }
    }
    
    /**
     * Отрисовка платформы с текстурой
     */
    public void render(Graphics g) {
        if (isGround && texture != null && groundTexture != null) {
            // Для земли рисуем слой травы сверху и землю под ней
            
            // Рисуем слой земли (всю платформу кроме верхних 16 пикселей)
            for (int x = 0; x < width; x += TILE_SIZE) {
                for (int y = TILE_SIZE; y < height; y += TILE_SIZE) {
                    g.drawImage(groundTexture, 
                              (int) this.x + x, 
                              (int) this.y + y, 
                              Math.min(TILE_SIZE, (int) width - x), 
                              Math.min(TILE_SIZE, (int) height - y), 
                              null);
                }
            }
            
            // Рисуем слой травы только сверху
            for (int x = 0; x < width; x += TILE_SIZE) {
                g.drawImage(texture, 
                          (int) this.x + x, 
                          (int) this.y, 
                          Math.min(TILE_SIZE, (int) width - x), 
                          TILE_SIZE, 
                          null);
            }
        } else if (texture != null) {
            // Для обычных платформ рисуем стандартную текстуру
            for (int x = 0; x < width; x += TILE_SIZE) {
                for (int y = 0; y < height; y += TILE_SIZE) {
                    g.drawImage(texture, 
                              (int) this.x + x, 
                              (int) this.y + y, 
                              Math.min(TILE_SIZE, (int) width - x), 
                              Math.min(TILE_SIZE, (int) height - y), 
                              null);
                }
            }
        } else {
            // Если текстура не найдена, используем цвет
            g.setColor(new Color(139, 69, 19)); // Коричневый цвет (Saddle Brown)
            g.fillRect((int) x, (int) y, (int) width, (int) height);
        }
    }
}
