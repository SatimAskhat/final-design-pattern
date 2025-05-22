import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Класс игрока
 */
public class Player extends GameObject {
    private double velX = 0;
    private double velY = 0;
    private boolean onGround = false;
    private double speed = 5; // Базовая скорость
    private double jumpForce = -15; // Базовая сила прыжка
    private final double GRAVITY = 0.8;
    
    // Параметры бонусов
    private boolean hasSpeedBoost = false;
    private boolean hasJumpBoost = false;
    private boolean isInvincible = false;
    private long speedBoostEndTime = 0;
    private long jumpBoostEndTime = 0;
    private long invincibilityEndTime = 0;
    private final double SPEED_BOOST_MULTIPLIER = 1.5;
    private final double JUMP_BOOST_MULTIPLIER = 1.3;
    
    // Анимация
    // Используется для определения, какой набор кадров анимации использовать
    private boolean facingRight = true;
    private BufferedImage[] animationFramesRight;
    private BufferedImage[] animationFramesLeft;
    private BufferedImage[] animationFrames;
    private BufferedImage currentFrame;
    private int frameIndex = 0;
    private int frameDelay = 0;
    private final int FRAME_DELAY_MAX = TextureManager.ANIMATION_FRAME_DELAY;
    
    public Player(double x, double y, double width, double height) {
        super(x, y, width, height);
        loadAnimationFrames();
    }
    
    /**
     * Загрузка кадров анимации
     */
    private void loadAnimationFrames() {
        TextureManager textureManager = TextureManager.getInstance();
        
        // Загружаем кадры анимации для движения вправо
        animationFramesRight = new BufferedImage[2];
        animationFramesRight[0] = textureManager.getTexture("character_tile_0000");
        animationFramesRight[1] = textureManager.getTexture("character_tile_0001");
        
        // Загружаем кадры анимации для движения влево
        animationFramesLeft = new BufferedImage[2];
        animationFramesLeft[0] = textureManager.getTexture("character_tile_0000");
        animationFramesLeft[1] = textureManager.getTexture("character_tile_0001");
        
        // По умолчанию смотрим вправо
        animationFrames = animationFramesRight;
        currentFrame = animationFrames[0];
    }
    
    /**
     * Обновление состояния игрока
     */
    public void update() {
        // Проверяем истечение бонусов
        updatePowerUps();
        
        // Применяем гравитацию
        velY += GRAVITY;
        
        // Обновляем позицию
        x += velX;
        y += velY;
        
        // Ограничиваем скорость падения
        if (velY > 20) {
            velY = 20;
        }
        
        // Обновляем анимацию только если игрок движется
        if (velX != 0) {
            // Обновляем направление в зависимости от скорости
            boolean newFacingRight = velX > 0;
            if (newFacingRight != facingRight) {
                facingRight = newFacingRight;
                animationFrames = facingRight ? animationFramesRight : animationFramesLeft;
            }
            
            frameDelay++;
            if (frameDelay >= FRAME_DELAY_MAX) {
                frameDelay = 0;
                frameIndex = (frameIndex + 1) % animationFrames.length;
                currentFrame = animationFrames[frameIndex];
            }
        } else {
            // Если игрок не движется, показываем первый кадр анимации
            frameIndex = 0;
            currentFrame = animationFrames[frameIndex];
        }
        
        // Автоматически останавливаем горизонтальное движение
        velX = 0;
    }
    
    /**
     * Движение влево
     */
    public void moveLeft() {
        // Применяем ускорение, если есть бонус
        velX = hasSpeedBoost ? -speed * SPEED_BOOST_MULTIPLIER : -speed;
        facingRight = false;
        animationFrames = animationFramesLeft;
    }
    
    /**
     * Движение вправо
     */
    public void moveRight() {
        // Применяем ускорение, если есть бонус
        velX = hasSpeedBoost ? speed * SPEED_BOOST_MULTIPLIER : speed;
        facingRight = true;
        animationFrames = animationFramesRight;
    }
    
    /**
     * Отрисовка игрока с текстурой
     */
    public void render(Graphics g) {
        if (currentFrame != null) {
            // Если игрок неуязвим, добавляем мерцание
            if (isInvincible && System.currentTimeMillis() % 300 < 150) {
                // Создаем эффект мерцания, пропуская каждый второй кадр
                return;
            }
            g.drawImage(currentFrame, (int)x, (int)y, (int)width, (int)height, null);
            
            // Отображаем индикаторы активных бонусов
            int indicatorY = (int)y - 10;
            if (hasSpeedBoost) {
                g.setColor(Color.BLUE);
                g.fillOval((int)x, indicatorY, 8, 8);
            }
            if (hasJumpBoost) {
                g.setColor(Color.GREEN);
                g.fillOval((int)x + 10, indicatorY, 8, 8);
            }
            if (isInvincible) {
                g.setColor(Color.YELLOW);
                g.fillOval((int)x + 20, indicatorY, 8, 8);
            }
        } else {
            // Если текстура не загружена, рисуем прямоугольник
            g.setColor(java.awt.Color.BLUE);
            g.fillRect((int)x, (int)y, (int)width, (int)height);
        }
    }
    
    /**
     * Прыжок
     */
    public void jump() {
        if (onGround) {
            // Применяем усиление прыжка, если есть бонус
            velY = hasJumpBoost ? jumpForce * JUMP_BOOST_MULTIPLIER : jumpForce;
            onGround = false;
        }
    }
    
    /**
     * Проверка возможности прыжка
     */
    public boolean canJump() {
        return onGround;
    }
    
    /**
     * Установка состояния нахождения на земле
     */
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    
    /**
     * Проверить, находится ли игрок на земле
     */
    public boolean isOnGround() {
        return onGround;
    }
    
    /**
     * Проверка неуязвимости игрока
     */
    public boolean isInvincible() {
        return isInvincible;
    }
    
    /**
     * Применение бонуса
     */
    public void applyPowerUp(int type, int duration) {
        long currentTime = System.currentTimeMillis();
        
        switch (type) {
            case PowerUp.TYPE_SPEED:
                hasSpeedBoost = true;
                speedBoostEndTime = currentTime + duration;
                break;
            case PowerUp.TYPE_JUMP:
                hasJumpBoost = true;
                jumpBoostEndTime = currentTime + duration;
                break;
            case PowerUp.TYPE_INVINCIBILITY:
                isInvincible = true;
                invincibilityEndTime = currentTime + duration;
                break;
        }
    }
    
    /**
     * Обновление состояния бонусов
     */
    private void updatePowerUps() {
        long currentTime = System.currentTimeMillis();
        
        // Проверяем истечение бонусов
        if (hasSpeedBoost && currentTime > speedBoostEndTime) {
            hasSpeedBoost = false;
        }
        
        if (hasJumpBoost && currentTime > jumpBoostEndTime) {
            hasJumpBoost = false;
        }
        
        if (isInvincible && currentTime > invincibilityEndTime) {
            isInvincible = false;
        }
    }
    
    /**
     * Получение вертикальной скорости
     */
    public double getVelY() {
        return velY;
    }
    
    /**
     * Установка позиции игрока
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.velX = 0;
        this.velY = 0;
    }
    
    /**
     * Обработка столкновения с платформой
     */
    public void handleCollision(Platform platform) {
        // Получаем границы объектов
        double playerLeft = x;
        double playerRight = x + width;
        double playerTop = y;
        double playerBottom = y + height;
        
        double platformLeft = platform.getX();
        double platformRight = platform.getX() + platform.getWidth();
        double platformTop = platform.getY();
        double platformBottom = platform.getY() + platform.getHeight();
        
        // Вычисляем перекрытие
        double overlapLeft = playerRight - platformLeft;
        double overlapRight = platformRight - playerLeft;
        double overlapTop = playerBottom - platformTop;
        double overlapBottom = platformBottom - playerTop;
        
        // Находим минимальное перекрытие
        double minOverlapX = Math.min(overlapLeft, overlapRight);
        double minOverlapY = Math.min(overlapTop, overlapBottom);
        
        // Разрешаем столкновение по оси с минимальным перекрытием
        if (minOverlapX < minOverlapY) {
            // Разрешаем по X
            if (overlapLeft < overlapRight) {
                // Столкновение слева
                x = platformLeft - width;
            } else {
                // Столкновение справа
                x = platformRight;
            }
            velX = 0;
        } else {
            // Разрешаем по Y
            if (overlapTop < overlapBottom) {
                // Столкновение сверху (игрок на платформе)
                y = platformTop - height;
                velY = 0;
                onGround = true;
            } else {
                // Столкновение снизу
                y = platformBottom;
                velY = 0;
            }
        }
    }
}
