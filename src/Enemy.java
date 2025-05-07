/**
 * Класс врага
 */
public class Enemy extends GameObject {
    private double velX;
    private boolean alive = true;
    
    public Enemy(double x, double y, double width, double height, double direction) {
        super(x, y, width, height);
        this.velX = direction * 2; // Скорость и направление
    }
    
    /**
     * Обновление состояния врага
     */
    public void update() {
        // Обновляем позицию
        x += velX;
    }
    
    /**
     * Обработка столкновения с платформой
     */
    public void handleCollision(Platform platform) {
        // Получаем границы объектов
        double enemyLeft = x;
        double enemyRight = x + width;
        
        double platformLeft = platform.getX();
        double platformRight = platform.getX() + platform.getWidth();
        
        // Если враг достиг края платформы, меняем направление
        if (enemyRight >= platformRight || enemyLeft <= platformLeft) {
            velX = -velX;
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
}
