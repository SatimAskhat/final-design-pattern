import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Класс монеты
 */
public class Coin extends GameObject {
    // Текстура монеты
    private BufferedImage texture;
    
    public Coin(double x, double y, double width, double height) {
        super(x, y, width, height);
        loadAnimationFrames();
    }
    
    /**
     * Загрузка текстуры монеты
     */
    private void loadAnimationFrames() {
        TextureManager textureManager = TextureManager.getInstance();
        
        // Загружаем указанную текстуру для монеты
        texture = textureManager.getTexture("204705246_w600_h600_204705246-removebg-preview");
    }
    
    /**
     * Метод обновления монеты (не используется, так как нет анимации)
     */
    public void update() {
        // Нет анимации, поэтому ничего не делаем
    }
    
    /**
     * Отрисовка монеты с текстурой (в 3 раза больше)
     */
    public void render(Graphics g) {
        // Увеличиваем размер монеты в 3 раза
        int renderWidth = (int)(width *2);
        int renderHeight = (int)(height * 2);
        // Корректируем позицию, чтобы монета осталась центрированной
        int renderX = (int)(x - (renderWidth - width) / 2);
        int renderY = (int)(y - (renderHeight - height) / 2);
        
        if (texture != null) {
            g.drawImage(texture, renderX, renderY, renderWidth, renderHeight, null);
        } else {
            // Если текстура не загружена, рисуем круг
            g.setColor(Color.YELLOW);
            g.fillOval(renderX, renderY, renderWidth, renderHeight);
        }
    }
}
