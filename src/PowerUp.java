import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Класс бонуса (power-up)
 */
public class PowerUp extends GameObject {
    // Типы бонусов
    public static final int TYPE_SPEED = 0;
    public static final int TYPE_JUMP = 1;
    public static final int TYPE_INVINCIBILITY = 2;
    
    private int type;
    private BufferedImage texture;
    private int duration = 10000; // Длительность эффекта в миллисекундах
    
    public PowerUp(double x, double y, double width, double height, int type) {
        super(x, y, width, height);
        this.type = type;
        loadTexture();
    }
    
    /**
     * Загрузка текстуры бонуса
     */
    private void loadTexture() {
        TextureManager textureManager = TextureManager.getInstance();
        
        // Загружаем текстуру в зависимости от типа бонуса
        switch (type) {
            case TYPE_SPEED:
                texture = textureManager.getTexture("powerup_speed");
                if (texture == null) {
                    // Если текстура не найдена, используем первую доступную
                    texture = textureManager.getTexture("tile_0000");
                }
                break;
            case TYPE_JUMP:
                texture = textureManager.getTexture("powerup_jump");
                if (texture == null) {
                    // Если текстура не найдена, используем первую доступную
                    texture = textureManager.getTexture("tile_0001");
                }
                break;
            case TYPE_INVINCIBILITY:
                texture = textureManager.getTexture("powerup_star");
                if (texture == null) {
                    // Если текстура не найдена, используем первую доступную
                    texture = textureManager.getTexture("tile_0002");
                }
                break;
        }
    }
    
    /**
     * Отрисовка бонуса
     */
    public void render(Graphics g) {
        if (texture != null) {
            g.drawImage(texture, (int)x, (int)y, (int)width, (int)height, null);
        } else {
            // Если текстура не загружена, рисуем цветной квадрат в зависимости от типа
            switch (type) {
                case TYPE_SPEED:
                    g.setColor(Color.BLUE);
                    break;
                case TYPE_JUMP:
                    g.setColor(Color.GREEN);
                    break;
                case TYPE_INVINCIBILITY:
                    g.setColor(Color.YELLOW);
                    break;
                default:
                    g.setColor(Color.MAGENTA);
            }
            g.fillRect((int)x, (int)y, (int)width, (int)height);
        }
    }
    
    /**
     * Получить тип бонуса
     */
    public int getType() {
        return type;
    }
    
    /**
     * Получить длительность эффекта
     */
    public int getDuration() {
        return duration;
    }
}
