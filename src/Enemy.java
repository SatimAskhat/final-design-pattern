import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Класс врага
 */
public class Enemy extends GameObject {
    private double velX;
    private boolean alive = true;
    
    // Анимация
    private boolean facingRight = true;
    private BufferedImage[] animationFramesRight;
    private BufferedImage[] animationFramesLeft;
    private BufferedImage[] animationFrames;
    private BufferedImage currentFrame;
    private int frameIndex = 0;
    private int frameDelay = 0;
    private final int FRAME_DELAY_MAX = TextureManager.ANIMATION_FRAME_DELAY;
    
    public Enemy(double x, double y, double width, double height, double direction) {
        super(x, y, width, height);
        this.velX = direction * 2; // Скорость и направление
        facingRight = direction > 0;
        loadAnimationFrames();
    }
    
    /**
     * Загрузка кадров анимации
     */
    private void loadAnimationFrames() {
        TextureManager textureManager = TextureManager.getInstance();
        
        // Загружаем кадры анимации для движения вправо
        animationFramesRight = new BufferedImage[2];
        animationFramesRight[0] = textureManager.getTexture("character_tile_0024");
        animationFramesRight[1] = textureManager.getTexture("character_tile_0025");
        
        // Загружаем кадры анимации для движения влево
        animationFramesLeft = new BufferedImage[2];
        animationFramesLeft[0] = textureManager.getTexture("character_tile_0026");
        animationFramesLeft[1] = textureManager.getTexture("character_tile_0025");
        
        // Устанавливаем начальные кадры в зависимости от направления
        animationFrames = facingRight ? animationFramesRight : animationFramesLeft;
        currentFrame = animationFrames[0];
    }
    
    /**
     * Обновление состояния врага
     */
    public void update() {
        // Проверяем, не выходит ли враг за границы карты
        if (x <= 0 || x + width >= GamePanel.WIDTH) {
            // Если враг достиг границы карты, меняем направление
            changeDirection();
            
            // Корректируем позицию, чтобы враг не выходил за границы
            if (x <= 0) {
                x = 1;
            } else if (x + width >= GamePanel.WIDTH) {
                x = GamePanel.WIDTH - width - 1;
            }
        } else {
            // Обновляем позицию
            x += velX;
        }
        
        // Обновляем анимацию
        frameDelay++;
        if (frameDelay >= FRAME_DELAY_MAX) {
            frameDelay = 0;
            frameIndex = (frameIndex + 1) % animationFrames.length;
            currentFrame = animationFrames[frameIndex];
        }
    }
    
    /**
     * Изменение направления движения врага
     */
    private void changeDirection() {
        velX = -velX;
        facingRight = velX > 0;
        animationFrames = facingRight ? animationFramesRight : animationFramesLeft;
    }
    
    /**
     * Обработка столкновения с платформой
     */
    public void handleCollision(Platform platform) {
        // Получаем границы объектов
        double enemyLeft = x;
        double enemyRight = x + width;
        double enemyTop = y;
        double enemyBottom = y + height;
        
        double platformLeft = platform.getX();
        double platformRight = platform.getX() + platform.getWidth();
        double platformTop = platform.getY();
        double platformBottom = platform.getY() + platform.getHeight();
        
        // Проверяем столкновение с любой частью платформы
        boolean collisionFromLeft = enemyRight >= platformLeft && enemyRight < platformLeft + 10 && 
                                    enemyBottom > platformTop + 5 && enemyTop < platformBottom - 5;
                                    
        boolean collisionFromRight = enemyLeft <= platformRight && enemyLeft > platformRight - 10 && 
                                     enemyBottom > platformTop + 5 && enemyTop < platformBottom - 5;
        
        // Проверяем столкновение с краем платформы
        boolean reachedPlatformEdge = enemyRight >= platformRight || enemyLeft <= platformLeft;
        
        // Если есть любое столкновение, меняем направление
        if (collisionFromLeft || collisionFromRight || reachedPlatformEdge) {
            // Меняем направление
            changeDirection();
            
            // Корректируем позицию, чтобы враг не застрял в стене или на краю платформы
            if (collisionFromLeft) {
                x = platformLeft - width - 1;
            } else if (collisionFromRight) {
                x = platformRight + 1;
            } else if (enemyRight >= platformRight) {
                x = platformRight - width - 1;
            } else if (enemyLeft <= platformLeft) {
                x = platformLeft + 1;
            }
        }
    }
    
    /**
     * Проверка, жив ли враг
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Установка состояния жизни врага
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    /**
     * Отрисовка врага с текстурой
     */
    public void render(Graphics g) {
        if (alive) {
            if (currentFrame != null) {
                g.drawImage(currentFrame, (int)x, (int)y, (int)width, (int)height, null);
            } else {
                // Если текстура не загружена, рисуем прямоугольник
                g.setColor(java.awt.Color.RED);
                g.fillRect((int)x, (int)y, (int)width, (int)height);
            }
        }
    }
}
