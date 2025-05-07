/**
 * Класс игрока
 */
public class Player extends GameObject {
    private double velX = 0;
    private double velY = 0;
    private boolean onGround = false;
    private final double SPEED = 5;
    private final double JUMP_FORCE = -15;
    private final double GRAVITY = 0.8;
    
    public Player(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
    
    /**
     * Обновление состояния игрока
     */
    public void update() {
        // Применяем гравитацию
        velY += GRAVITY;
        
        // Обновляем позицию
        x += velX;
        y += velY;
        
        // Ограничиваем скорость падения
        if (velY > 20) {
            velY = 20;
        }
        
        // Автоматически останавливаем горизонтальное движение
        velX = 0;
    }
    
    /**
     * Движение влево
     */
    public void moveLeft() {
        velX = -SPEED;
    }
    
    /**
     * Движение вправо
     */
    public void moveRight() {
        velX = SPEED;
    }
    
    /**
     * Прыжок
     */
    public void jump() {
        if (onGround) {
            velY = JUMP_FORCE;
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
