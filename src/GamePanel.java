import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс игровой панели
 */
public class GamePanel extends JPanel implements ActionListener {
    // Константы игры
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int DELAY = 15; // Задержка в миллисекундах между кадрами
    
    // Игровые объекты
    private Player player;
    private List<Platform> platforms;
    private List<Enemy> enemies;
    private List<Coin> coins;
    
    // Управление игрой
    private Timer timer;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private int score = 0;
    private int level = 1;
    private boolean gameWon = false;
    private boolean gamePaused = false;
    private boolean levelCompleted = false;
    private boolean gameOver = false;
    private boolean waitForKeyPress = false;
    private boolean allCoinsCollected = false;
    
    // Чекпоинты
    private double checkpointX;
    private double checkpointY;
    
    // Ссылка на родительский фрейм
    private JFrame parentFrame;
    
    // Менеджеры игры
    private GamePreferences preferences;
    private AudioManager audioManager;
    
    /**
     * Конструктор игровой панели с указанным уровнем
     */
    public GamePanel(int level) {
        this.level = level;
        this.preferences = GamePreferences.getInstance();
        this.audioManager = AudioManager.getInstance();
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(135, 206, 235)); // Светло-голубой цвет (Sky Blue)
        setFocusable(true);
        
        // Настройка обработчика клавиш
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Если ожидаем нажатия клавиши для начала уровня
                if (waitForKeyPress) {
                    waitForKeyPress = false;
                    return;
                }
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        leftPressed = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = true;
                        break;
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_UP:
                        jumpPressed = true;
                        break;
                    case KeyEvent.VK_ESCAPE:
                        handleEscapeKey();
                        break;
                    case KeyEvent.VK_R:
                        // Рестарт уровня, если игра окончена
                        if (gameOver) {
                            restartLevel();
                        }
                        break;
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        leftPressed = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressed = false;
                        break;
                    case KeyEvent.VK_SPACE:
                    case KeyEvent.VK_UP:
                        jumpPressed = false;
                        break;
                    case KeyEvent.VK_ESCAPE:
                        // Обработка нажатия Escape - пауза/меню
                        handleEscapeKey();
                        break;
                }
            }
        });
        
        // Инициализация игровых объектов
        initGame();
        
        // Создание таймера
        timer = new Timer(DELAY, this);
        
        // Воспроизведение игровой музыки
        audioManager.playMusic("game", true);
    }
    
    /**
     * Конструктор игровой панели с родительским фреймом
     */
    public GamePanel(JFrame parentFrame) {
        this(1); // Начинаем с первого уровня по умолчанию
        this.parentFrame = parentFrame;
    }
    
    /**
     * Конструктор игровой панели с уровнем и родительским фреймом
     */
    public GamePanel(int level, JFrame parentFrame) {
        this(level);
        this.parentFrame = parentFrame;
    }

    /**
     * Инициализация игровых объектов
     */
    private void initGame() {
        // Создаем игрока
        player = new Player(WIDTH / 2 - 15, HEIGHT - 100, 30, 50);
        
        // Устанавливаем чекпоинт в начальной позиции игрока
        checkpointX = player.getX();
        checkpointY = player.getY();
        
        // Устанавливаем флаг ожидания нажатия клавиши
        waitForKeyPress = true;
        
        // Создаем платформы в зависимости от уровня
        platforms = new ArrayList<>();
        platforms.add(new Platform(0, HEIGHT - 50, WIDTH, 50)); // Земля
        
        // Разные конфигурации платформ для разных уровней
        switch (level) {
            case 1: // Уровень 1 - Обучающий уровень с простыми платформами
                platforms.add(new Platform(100, HEIGHT - 150, 200, 20));
                platforms.add(new Platform(400, HEIGHT - 175, 200, 20));
                platforms.add(new Platform(200, HEIGHT - 300, 200, 20));
                platforms.add(new Platform(500, HEIGHT - 400, 200, 20));
                break;
                
            case 2: // Уровень 2 - Лестница вверх
                for (int i = 0; i < 8; i++) {
                    platforms.add(new Platform(100 + i * 80, HEIGHT - 150 - i * 50, 20, 20));
                }
                // Дополнительные платформы для сбора монет
                platforms.add(new Platform(300, HEIGHT - 150, 20, 20));
                platforms.add(new Platform(500, HEIGHT - 250, 20, 20));
                break;
                
            case 3: // Уровень 3 - Зигзагообразный путь
                for (int i = 0; i < 6; i++) {
                    if (i % 2 == 0) {
                        platforms.add(new Platform(100, HEIGHT - 150 - i * 70, 200, 20));
                    } else {
                        platforms.add(new Platform(WIDTH - 300, HEIGHT - 150 - i * 70, 200, 20));
                    }
                }
                // Соединяющие платформы
                platforms.add(new Platform(300, HEIGHT - 220, 100, 20));
                platforms.add(new Platform(400, HEIGHT - 360, 100, 20));
                break;
                
            case 4: // Уровень 4 - Пирамида
                int baseWidth = 600;
                int step = 60;
                for (int i = 0; i < 8; i++) {
                    platforms.add(new Platform(WIDTH/2 - baseWidth/2 + i * step/2, HEIGHT - 150 - i * 50, baseWidth - i * step, 20));
                }
                break;
                
            case 5: // Уровень 5 - Падающие платформы
                // Основные платформы по краям
                platforms.add(new Platform(0, HEIGHT - 150, 100, 20));
                platforms.add(new Platform(WIDTH - 100, HEIGHT - 150, 100, 20));
                platforms.add(new Platform(0, HEIGHT - 300, 100, 20));
                platforms.add(new Platform(WIDTH - 100, HEIGHT - 300, 100, 20));
                platforms.add(new Platform(0, HEIGHT - 450, 100, 20));
                platforms.add(new Platform(WIDTH - 100, HEIGHT - 450, 100, 20));
                
                // Падающие платформы посередине
                for (int i = 0; i < 5; i++) {
                    platforms.add(new Platform(150 + i * 100, HEIGHT - 200 - i * 50, 80, 20));
                }
                break;
                
            case 6: // Уровень 6 - Спираль
                int centerX = WIDTH / 2;
                int centerY = HEIGHT / 2;
                int radius = 250;
                int platformWidth = 100;
                
                for (int i = 0; i < 16; i++) {
                    double angle = Math.PI * 2 * i / 16;
                    double distance = radius * (1 - i / 32.0);
                    int x = (int)(centerX + Math.cos(angle) * distance - platformWidth/2);
                    int y = (int)(centerY + Math.sin(angle) * distance);
                    platforms.add(new Platform(x, y, platformWidth, 20));
                }
                break;
                
            case 7: // Уровень 7 - Сетка
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 3; j++) {
                        platforms.add(new Platform(100 + i * 150, HEIGHT - 150 - j * 150, 100, 20));
                    }
                }
                break;
                
            case 8: // Уровень 8 - Буква "M"
                // Левая стойка
                for (int i = 0; i < 8; i++) {
                    platforms.add(new Platform(100, HEIGHT - 150 - i * 50, 80, 20));
                }
                // Правая стойка
                for (int i = 0; i < 8; i++) {
                    platforms.add(new Platform(WIDTH - 180, HEIGHT - 150 - i * 50, 80, 20));
                }
                // Середина
                platforms.add(new Platform(250, HEIGHT - 300, 80, 20));
                platforms.add(new Platform(400, HEIGHT - 350, 80, 20));
                platforms.add(new Platform(550, HEIGHT - 300, 80, 20));
                break;
                
            case 9: // Уровень 9 - Вертикальный лабиринт
                // Стены лабиринта
                platforms.add(new Platform(200, HEIGHT - 150, 20, 400)); // Вертикальная стена 1
                platforms.add(new Platform(400, HEIGHT - 350, 20, 300)); // Вертикальная стена 2
                platforms.add(new Platform(600, HEIGHT - 250, 20, 200)); // Вертикальная стена 3
                
                // Платформы для прыжков
                platforms.add(new Platform(100, HEIGHT - 250, 100, 20));
                platforms.add(new Platform(300, HEIGHT - 350, 100, 20));
                platforms.add(new Platform(500, HEIGHT - 450, 100, 20));
                platforms.add(new Platform(700, HEIGHT - 550, 100, 20));
                break;
                
            case 10: // Уровень 10 - Финальный уровень с комбинацией всех элементов
                // Начальная платформа
                platforms.add(new Platform(50, HEIGHT - 150, 100, 20));
                
                // Зигзаг
                platforms.add(new Platform(200, HEIGHT - 200, 100, 20));
                platforms.add(new Platform(350, HEIGHT - 250, 100, 20));
                platforms.add(new Platform(500, HEIGHT - 300, 100, 20));
                
                // Вертикальный подъем
                for (int i = 0; i < 5; i++) {
                    platforms.add(new Platform(650, HEIGHT - 350 - i * 50, 100, 20));
                }
                
                // Финальная платформа
                platforms.add(new Platform(400, HEIGHT - 600, 200, 20));
                break;
                
            default: // Дополнительные уровни - случайные платформы
                int numPlatforms = 10 + (level - 10) * 2; // Больше платформ с каждым уровнем
                for (int i = 0; i < numPlatforms; i++) {
                    int x = (int) (Math.random() * (WIDTH - 150));
                    int y = HEIGHT - 150 - i * 40;
                    int width = 80 + (int) (Math.random() * 120);
                    platforms.add(new Platform(x, y, width, 20));
                }
                break;
        }
        
        // Создаем врагов и монеты в зависимости от уровня
        enemies = new ArrayList<>();
        coins = new ArrayList<>();
        
        switch (level) {
            case 1: // Уровень 1 - Несколько монет, нет врагов
                // Монеты размещены над платформами
                coins.add(new Coin(150, HEIGHT - 180, 15, 15)); // Над первой платформой
                coins.add(new Coin(450, HEIGHT - 230, 15, 15)); // Над второй платформой
                coins.add(new Coin(250, HEIGHT - 330, 15, 15)); // Над третьей платформой
                coins.add(new Coin(550, HEIGHT - 430, 15, 15)); // Над четвертой платформой
                coins.add(new Coin(400, HEIGHT - 80, 15, 15));  // Над землей
                break;
                
            case 2: // Уровень 2 - Монеты на лестнице, один враг
                // Монеты над лестницей - исправлено положение монет, чтобы до них можно было допрыгнуть
                for (int i = 0; i < 8; i++) {
                    // Размещаем монеты точно над платформами, на 30 пикселей выше
                    coins.add(new Coin(140 + i * 80, HEIGHT - 190 - i * 50, 15, 15));
                }
                // Дополнительные монеты над дополнительными платформами
                coins.add(new Coin(340, HEIGHT - 280, 15, 15)); // Над платформой, ниже чтобы было доступно
                coins.add(new Coin(540, HEIGHT - 380, 15, 15)); // Над платформой, ниже чтобы было доступно
                
                // Враг на земле, чтобы не мешал на первых платформах
                enemies.add(new Enemy(300, HEIGHT - 80, 30, 30, 2.0f));
                break;
                
            case 3: // Уровень 3 - Зигзагообразный путь
                // Монеты над платформами
                for (int i = 0; i < 6; i++) {
                    if (i % 2 == 0) {
                        coins.add(new Coin(200, HEIGHT - 180 - i * 70, 15, 15)); // Над левыми платформами
                    } else {
                        coins.add(new Coin(WIDTH - 200, HEIGHT - 180 - i * 70, 15, 15)); // Над правыми платформами
                    }
                }
                
                // Монеты над соединяющими платформами
                coins.add(new Coin(350, HEIGHT - 200, 15, 15)); // Над первой соединяющей платформой
                coins.add(new Coin(450, HEIGHT - 340, 15, 15)); // Над второй соединяющей платформой
                
                // Враги на широких платформах, чтобы не падали
                enemies.add(new Enemy(150, HEIGHT - 80, 30, 30, 2.0f)); // На земле
                enemies.add(new Enemy(WIDTH - 250, HEIGHT - 180, 30, 30, -2.0f)); // На первой правой платформе
                break;
                
            case 4: // Уровень 4 - Пирамида
                // Монеты над каждым уровнем пирамиды
                for (int i = 0; i < 8; i++) {
                    coins.add(new Coin(WIDTH/2, HEIGHT - 130 - i * 50, 15, 15)); // Над центром каждого уровня пирамиды
                }
                
                // Дополнительные монеты по бокам (над платформами)
                coins.add(new Coin(WIDTH/2 - 150, HEIGHT - 230, 15, 15)); // Левая монета
                coins.add(new Coin(WIDTH/2 + 150, HEIGHT - 230, 15, 15)); // Правая монета
                
                // Враги на широких уровнях пирамиды, чтобы не падали
                enemies.add(new Enemy(WIDTH/2 - 150, HEIGHT - 80, 30, 30, 2.5f)); // На земле
                enemies.add(new Enemy(WIDTH/2 + 100, HEIGHT - 180, 30, 30, -2.5f)); // На первом уровне пирамиды
                enemies.add(new Enemy(WIDTH/2 - 50, HEIGHT - 280, 30, 30, 2.5f)); // На втором уровне пирамиды
                break;
                
            case 5: // Уровень 5 - Падающие платформы
                // Монеты над основными платформами
                coins.add(new Coin(50, HEIGHT - 130, 15, 15)); // Над первой левой платформой
                coins.add(new Coin(WIDTH - 50, HEIGHT - 130, 15, 15)); // Над первой правой платформой
                coins.add(new Coin(50, HEIGHT - 280, 15, 15)); // Над второй левой платформой
                coins.add(new Coin(WIDTH - 50, HEIGHT - 280, 15, 15)); // Над второй правой платформой
                coins.add(new Coin(50, HEIGHT - 430, 15, 15)); // Над третьей левой платформой
                coins.add(new Coin(WIDTH - 50, HEIGHT - 430, 15, 15)); // Над третьей правой платформой
                
                // Монеты над падающими платформами
                for (int i = 0; i < 5; i++) {
                    coins.add(new Coin(190 + i * 100, HEIGHT - 180 - i * 50, 15, 15)); // На 20 пикселей выше платформ
                }
                
                // Враги только на широких платформах
                enemies.add(new Enemy(30, HEIGHT - 80, 30, 30, 3.0f)); // На земле
                enemies.add(new Enemy(WIDTH - 70, HEIGHT - 180, 30, 30, -3.0f)); // На первой правой платформе
                enemies.add(new Enemy(50, HEIGHT - 330, 30, 30, 3.0f)); // На второй левой платформе
                break;
                
            case 6: // Уровень 6 - Спираль
                int centerX = WIDTH / 2;
                int centerY = HEIGHT / 2;
                int radius = 250;
                
                // Монеты над платформами спирали
                for (int i = 0; i < 16; i++) {
                    double angle = Math.PI * 2 * i / 16;
                    double distance = radius * (1 - i / 32.0);
                    int platformX = (int)(centerX + Math.cos(angle) * distance - 50); // Центр платформы
                    int platformY = (int)(centerY + Math.sin(angle) * distance); // Высота платформы
                    
                    // Размещаем монету над платформой
                    coins.add(new Coin(platformX + 50, platformY - 30, 15, 15)); // Над центром платформы
                }
                
                // Враги только на широких внешних платформах
                for (int i = 0; i < 4; i++) {
                    double angle = Math.PI * 2 * i / 4;
                    double distance = radius * 0.8; // Внешние платформы
                    int x = (int)(centerX + Math.cos(angle) * distance);
                    int y = (int)(centerY + Math.sin(angle) * distance);
                    enemies.add(new Enemy(x, y - 30, 30, 30, 3.5f * (i % 2 == 0 ? 1 : -1))); // Чуть выше платформы
                }
                break;
                
            case 7: // Уровень 7 - Сетка
                // Монеты над платформами в шахматном порядке
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 3; j++) {
                        if ((i + j) % 2 == 0) { // Шахматный порядок
                            coins.add(new Coin(150 + i * 150, HEIGHT - 130 - j * 150, 15, 15)); // Над платформой
                        }
                    }
                }
                
                // Враги только на широких платформах и земле
                enemies.add(new Enemy(150, HEIGHT - 80, 30, 30, 4.0f)); // На земле
                enemies.add(new Enemy(450, HEIGHT - 80, 30, 30, -4.0f)); // На земле
                enemies.add(new Enemy(300, HEIGHT - 230, 30, 30, 4.0f)); // На первом уровне
                enemies.add(new Enemy(600, HEIGHT - 230, 30, 30, -4.0f)); // На первом уровне
                enemies.add(new Enemy(150, HEIGHT - 380, 30, 30, 4.0f)); // На втором уровне
                enemies.add(new Enemy(450, HEIGHT - 380, 30, 30, -4.0f)); // На втором уровне
                break;
                
            case 8: // Уровень 8 - Буква "M"
                // Монеты над левой стойкой
                for (int i = 0; i < 8; i++) {
                    coins.add(new Coin(140, HEIGHT - 130 - i * 50, 15, 15)); // Над платформами левой стойки
                }
                
                // Монеты над правой стойкой
                for (int i = 0; i < 8; i++) {
                    coins.add(new Coin(WIDTH - 140, HEIGHT - 130 - i * 50, 15, 15)); // Над платформами правой стойки
                }
                
                // Монеты над средними платформами
                coins.add(new Coin(290, HEIGHT - 280, 15, 15)); // Над первой средней платформой
                coins.add(new Coin(400, HEIGHT - 330, 15, 15)); // Над второй средней платформой
                coins.add(new Coin(510, HEIGHT - 280, 15, 15)); // Над третьей средней платформой
                
                // Враги только на широких платформах
                enemies.add(new Enemy(140, HEIGHT - 80, 30, 30, 4.5f)); // На земле
                enemies.add(new Enemy(WIDTH - 140, HEIGHT - 80, 30, 30, -4.5f)); // На земле
                enemies.add(new Enemy(400, HEIGHT - 180, 30, 30, 4.5f)); // На первом уровне
                break;
                
            case 9: // Уровень 9 - Вертикальный лабиринт
                // Монеты над платформами
                coins.add(new Coin(150, HEIGHT - 230, 15, 15)); // Над первой платформой
                coins.add(new Coin(350, HEIGHT - 330, 15, 15)); // Над второй платформой
                coins.add(new Coin(550, HEIGHT - 430, 15, 15)); // Над третьей платформой
                coins.add(new Coin(750, HEIGHT - 530, 15, 15)); // Над четвертой платформой
                
                // Дополнительные монеты в проходах между стенами
                coins.add(new Coin(250, HEIGHT - 100, 15, 15)); // Монета в первом проходе
                coins.add(new Coin(450, HEIGHT - 200, 15, 15)); // Монета во втором проходе
                coins.add(new Coin(650, HEIGHT - 300, 15, 15)); // Монета в третьем проходе
                
                // Враги только на широких платформах и земле
                enemies.add(new Enemy(150, HEIGHT - 80, 30, 30, 5.0f)); // На земле
                enemies.add(new Enemy(350, HEIGHT - 80, 30, 30, -5.0f)); // На земле
                enemies.add(new Enemy(550, HEIGHT - 80, 30, 30, 5.0f)); // На земле
                enemies.add(new Enemy(150, HEIGHT - 280, 30, 30, 5.0f)); // На первой платформе
                enemies.add(new Enemy(350, HEIGHT - 380, 30, 30, -5.0f)); // На второй платформе
                enemies.add(new Enemy(550, HEIGHT - 480, 30, 30, 5.0f)); // На третьей платформе
                break;
                
            case 10: // Уровень 10 - Финальный уровень
                // Монета над начальной платформой
                coins.add(new Coin(100, HEIGHT - 130, 15, 15)); // Над начальной платформой
                
                // Монеты над зигзагом
                coins.add(new Coin(250, HEIGHT - 180, 15, 15)); // Над первой платформой зигзага
                coins.add(new Coin(400, HEIGHT - 230, 15, 15)); // Над второй платформой зигзага
                coins.add(new Coin(550, HEIGHT - 280, 15, 15)); // Над третьей платформой зигзага
                
                // Монеты над вертикальным подъемом
                for (int i = 0; i < 5; i++) {
                    coins.add(new Coin(700, HEIGHT - 330 - i * 50, 15, 15)); // Над платформами вертикального подъема
                }
                
                // Финальная монета над финальной платформой
                coins.add(new Coin(500, HEIGHT - 580, 15, 15)); // Над финальной платформой
                
                // Враги только на широких платформах и земле
                enemies.add(new Enemy(200, HEIGHT - 80, 30, 30, 5.5f)); // На земле
                enemies.add(new Enemy(350, HEIGHT - 80, 30, 30, -5.5f)); // На земле
                enemies.add(new Enemy(500, HEIGHT - 80, 30, 30, 5.5f)); // На земле
                
                // Враги на вертикальном подъеме
                enemies.add(new Enemy(650, HEIGHT - 380, 30, 30, -5.5f)); // На первой платформе подъема
                enemies.add(new Enemy(650, HEIGHT - 480, 30, 30, 5.5f)); // На третьей платформе подъема
                
                // Враг на финальной платформе
                enemies.add(new Enemy(450, HEIGHT - 580, 30, 30, 5.5f)); // На финальной платформе
                break;
                
            default: // Дополнительные уровни - случайные монеты и враги
                int numCoins = 15 + (level - 10) * 2; // Больше монет с каждым уровнем
                for (int i = 0; i < numCoins; i++) {
                    int x = (int) (Math.random() * (WIDTH - 50));
                    int y = (int) (Math.random() * (HEIGHT - 200));
                    coins.add(new Coin(x, y, 15, 15));
                }
                
                int numEnemies = 10 + (level - 10); // Больше врагов с каждым уровнем
                for (int i = 0; i < numEnemies; i++) {
                    int x = (int) (Math.random() * (WIDTH - 50));
                    int y = (int) (Math.random() * (HEIGHT - 200));
                    float speed = (i % 2 == 0 ? 1 : -1) * (6.0f + (level - 10) * 0.5f);
                    enemies.add(new Enemy(x, y, 30, 30, speed));
                }
                break;
        }
        
        // Счет не сбрасываем при переходе на новый уровень, только при проигрыше
    }
    
    /**
     * Обработка нажатия клавиши Escape
     */
    private void handleEscapeKey() {
        if (levelCompleted || gameOver) {
            // Если уровень завершен или игра окончена, возвращаемся в меню
            returnToMenu();
        } else {
            // Иначе ставим игру на паузу
            gamePaused = !gamePaused;
            
            // Воспроизводим звук паузы
            if (preferences.isSoundEnabled()) {
                audioManager.playSound("pause");
            }
            
            if (gamePaused) {
                // Останавливаем таймер и музыку
                timer.stop();
                audioManager.pauseMusic();
            } else {
                // Возобновляем таймер и музыку
                timer.start();
                audioManager.resumeMusic();
            }
        }
    }
    
    /**
     * Возврат в главное меню
     */
    private void returnToMenu() {
        // Останавливаем таймер и музыку
        timer.stop();
        audioManager.stopMusic();
        
        // Если родительский фрейм доступен
        if (parentFrame != null) {
            // Создаем главное меню
            MainMenu mainMenu = new MainMenu(parentFrame);
            
            // Заменяем текущую панель на главное меню
            parentFrame.getContentPane().removeAll();
            parentFrame.add(mainMenu);
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }
    
    /**
     * Запуск игры
     */
    public void startGame() {
        // Воспроизводим звук начала уровня
        if (preferences.isSoundEnabled()) {
            audioManager.playSound("powerup");
        }
        
        timer.start();
    }
    
    /**
     * Обработка событий таймера
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gamePaused && !levelCompleted && !gameOver && !waitForKeyPress) {
            update();
        }
        repaint();
    }
    
    /**
     * Рестарт текущего уровня
     */
    private void restartLevel() {
        // Сбрасываем состояние игры
        gameOver = false;
        levelCompleted = false;
        
        // Воспроизводим звук рестарта
        if (preferences.isSoundEnabled()) {
            audioManager.playSound("1up");
        }
        
        // Возвращаем игрока на последний чекпоинт
        player.setPosition(checkpointX, checkpointY);
        
        // Переинициализируем уровень
        initGame();
        
        // Запускаем игру
        startGame();
    }

    /**
     * Обновление игровой логики
     */
    private void update() {
        // Обработка ввода
        if (leftPressed) {
            player.moveLeft();
        }
        if (rightPressed) {
            player.moveRight();
        }
        if (jumpPressed && player.canJump()) {
            player.jump();
            // Воспроизводим звук прыжка
            if (preferences.isSoundEnabled()) {
                // Используем разные звуки прыжка для разнообразия
                if (Math.random() < 0.3) {
                    audioManager.playSound("jumpsmall");
                } else {
                    audioManager.playSound("jump");
                }
            }
        }
        
        // Обновление игрока
        player.update();
        
        // Проверка коллизий с платформами
        player.setOnGround(false);
        boolean wasOnGround = player.isOnGround();
        for (Platform platform : platforms) {
            if (player.collidesWith(platform)) {
                player.handleCollision(platform);
                
                // Если игрок приземлился на платформу сверху и не был на земле раньше
                if (!wasOnGround && player.isOnGround() && player.getVelY() > 0) {
                    // Воспроизводим звук приземления
                    if (preferences.isSoundEnabled()) {
                        audioManager.playSound("brick");
                    }
                }
            }
        }
        
        // Обновление врагов
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isAlive()) {
                enemy.update();
                
                // Проверка коллизий врагов с платформами
                for (Platform platform : platforms) {
                    if (enemy.collidesWith(platform)) {
                        enemy.handleCollision(platform);
                    }
                }
                
                // Проверка коллизий игрока с врагами
                if (player.collidesWith(enemy)) {
                    if (player.getVelY() > 0 && player.getY() + player.getHeight() < enemy.getY() + enemy.getHeight() / 2) {
                        // Игрок прыгнул на врага сверху
                        enemy.setAlive(false);
                        score += 100;
                        // Воспроизводим звук уничтожения врага
                        if (preferences.isSoundEnabled()) {
                            audioManager.playSound("bump");
                        }
                    } else {
                        // Игрок столкнулся с врагом сбоку или снизу - игра окончена
                        gameOver = true;
                        // Воспроизводим звук смерти игрока
                        if (preferences.isSoundEnabled()) {
                            audioManager.playSound("death");
                        }
                    }
                }
            }
        }
        
        // Проверка коллизий с монетами
        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            if (player.collidesWith(coin)) {
                coins.remove(i);
                i--;
                score += 50;
                // Воспроизводим звук сбора монеты
                if (preferences.isSoundEnabled()) {
                    audioManager.playSound("coin");
                }
            }
        }
        
        // Проверка завершения уровня
        checkLevelCompletion();
        
        // Проверка проигрыша
        checkGameOver();
    }
    
    /**
     * Проверка завершения уровня
     */
    private void checkLevelCompletion() {
        // Условия завершения уровня:
        // Все монеты собраны
        
        // Проверяем, собраны ли все монеты
        allCoinsCollected = coins.isEmpty();
        
        // Если все монеты собраны и уровень еще не завершен
        if (allCoinsCollected && !levelCompleted) {
            System.out.println("Уровень " + level + " завершен!");
            levelCompleted = true;
            
            // Воспроизводим звук завершения уровня
            if (preferences.isSoundEnabled()) {
                audioManager.playSound("powerup");
            }
            
            // Сохраняем прогресс - разблокируем следующий уровень
            int currentUnlockedLevel = preferences.getCurrentLevel();
            if (level >= currentUnlockedLevel) {
                preferences.setCurrentLevel(level + 1);
                preferences.save();
            }
            
            // Останавливаем текущую музыку
            audioManager.stopMusic();
            
            // Останавливаем текущий таймер
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            
            // Создаем одноразовый таймер для перехода к следующему уровню
            javax.swing.Timer nextLevelTimer = new javax.swing.Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    goToNextLevel();
                }
            });
            nextLevelTimer.setRepeats(false);
            nextLevelTimer.start();
        }
    }
    
    /**
     * Переход к следующему уровню
     */
    private void goToNextLevel() {
        // Если это не последний уровень, переходим к следующему
        if (level < 10) {
            System.out.println("Переход к уровню " + (level + 1));
            // Переходим к следующему уровню
            if (parentFrame != null) {
                try {
                    // Создаем новую панель для следующего уровня
                    final int nextLevel = level + 1;
                    
                    // Используем EDT для всех операций с GUI
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Создаем новую панель внутри EDT
                                GamePanel nextLevelPanel = new GamePanel(nextLevel, parentFrame);
                                
                                // Очищаем и обновляем родительский фрейм
                                parentFrame.getContentPane().removeAll();
                                parentFrame.add(nextLevelPanel);
                                parentFrame.revalidate();
                                parentFrame.repaint();
                                
                                // Запускаем новый уровень
                                nextLevelPanel.requestFocusInWindow();
                                nextLevelPanel.startGame();
                                
                                System.out.println("Запущен уровень " + nextLevel);
                            } catch (Exception ex) {
                                System.out.println("Ошибка при создании нового уровня: " + ex.getMessage());
                                ex.printStackTrace();
                                returnToMenu();
                            }
                        }
                    });
                } catch (Exception ex) {
                    System.out.println("Ошибка при переходе на следующий уровень: " + ex.getMessage());
                    ex.printStackTrace();
                    // В случае ошибки возвращаемся в меню
                    returnToMenu();
                }
            }
        } else {
            // Если это последний уровень, показываем экран победы
            gameWon = true;
            repaint(); // Перерисовываем экран, чтобы показать экран победы
        }
    }
    
    /**
     * Проверка проигрыша
     */
    private void checkGameOver() {
        // Если игрок упал за пределы экрана
        if (player.getY() > HEIGHT && !gameOver) {
            gameOver = true;
            
            // Воспроизводим звук смерти игрока
            if (preferences.isSoundEnabled()) {
                audioManager.playSound("death");
            }
            
            // Создаем таймер для задержки перед возвратом в меню
            Timer delayTimer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    returnToMenu();
                }
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    /**
     * Отрисовка компонентов
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Рисуем фон
        g.setColor(new Color(135, 206, 235)); // Светло-голубой цвет (Sky Blue)
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Рисуем платформы
        g.setColor(new Color(139, 69, 19)); // Коричневый цвет (Saddle Brown)
        for (Platform platform : platforms) {
            g.fillRect((int) platform.getX(), (int) platform.getY(), 
                       (int) platform.getWidth(), (int) platform.getHeight());
        }
        
        // Рисуем монеты
        g.setColor(Color.YELLOW);
        for (Coin coin : coins) {
            g.fillOval((int) coin.getX(), (int) coin.getY(), 
                      (int) coin.getWidth(), (int) coin.getHeight());
        }
        
        // Рисуем врагов
        g.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                g.fillRect((int) enemy.getX(), (int) enemy.getY(), 
                           (int) enemy.getWidth(), (int) enemy.getHeight());
            }
        }
        
        // Рисуем игрока
        g.setColor(Color.BLUE);
        g.fillRect((int) player.getX(), (int) player.getY(), 
                   (int) player.getWidth(), (int) player.getHeight());
        
        // Рисуем информацию об игре
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Уровень: " + level, 20, 30);
        g.drawString("Счет: " + score, 20, 50);
        g.drawString("Монеты: " + (3 - coins.size()) + "/3", 20, 70);
        
        // Если игра на паузе, рисуем сообщение о паузе
        if (gamePaused) {
            g.setColor(new Color(0, 0, 0, 150)); // Полупрозрачный черный
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String pauseText = "ПАУЗА";
            int textWidth = g.getFontMetrics().stringWidth(pauseText);
            g.drawString(pauseText, WIDTH / 2 - textWidth / 2, HEIGHT / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String instructionText = "Нажмите ESC для продолжения";
            int instructionWidth = g.getFontMetrics().stringWidth(instructionText);
            g.drawString(instructionText, WIDTH / 2 - instructionWidth / 2, HEIGHT / 2 + 40);
        }
        
        // Если уровень завершен, рисуем сообщение о завершении
        if (levelCompleted) {
            g.setColor(new Color(0, 0, 0, 150)); // Полупрозрачный черный
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String completedText = "УРОВЕНЬ ПРОЙДЕН!";
            int textWidth = g.getFontMetrics().stringWidth(completedText);
            g.drawString(completedText, WIDTH / 2 - textWidth / 2, HEIGHT / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String scoreText = "Счет: " + score;
            int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, WIDTH / 2 - scoreWidth / 2, HEIGHT / 2 + 40);
        }
        
        // Если игра окончена, рисуем сообщение о проигрыше
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150)); // Полупрозрачный черный
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String gameOverText = "ИГРА ОКОНЧЕНА";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            g.drawString(gameOverText, WIDTH / 2 - textWidth / 2, HEIGHT / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String scoreText = "Счет: " + score;
            int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, WIDTH / 2 - scoreWidth / 2, HEIGHT / 2 + 40);
            
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            String restartText = "Нажмите R для перезапуска уровня";
            int restartWidth = g.getFontMetrics().stringWidth(restartText);
            g.drawString(restartText, WIDTH / 2 - restartWidth / 2, HEIGHT / 2 + 80);
        }
        
        // Если ожидаем нажатия клавиши для начала уровня
        if (waitForKeyPress && !gameOver && !levelCompleted && !gamePaused) {
            g.setColor(new Color(0, 0, 0, 150)); // Полупрозрачный черный
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String levelText = "УРОВЕНЬ " + level;
            int textWidth = g.getFontMetrics().stringWidth(levelText);
            g.drawString(levelText, WIDTH / 2 - textWidth / 2, HEIGHT / 2 - 40);
            
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String pressText = "Нажмите любую клавишу для начала";
            int pressWidth = g.getFontMetrics().stringWidth(pressText);
            g.drawString(pressText, WIDTH / 2 - pressWidth / 2, HEIGHT / 2 + 20);
        }
        
        // Если игра выиграна, рисуем сообщение о победе
        if (gameWon) {
            g.setColor(new Color(0, 0, 0, 150)); // Полупрозрачный черный
            g.fillRect(0, 0, getWidth(), getHeight());
            
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String winText = "ВЫ ПРОШЛИ ИГРУ!";
            int textWidth = g.getFontMetrics().stringWidth(winText);
            g.drawString(winText, WIDTH / 2 - textWidth / 2, HEIGHT / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            String scoreText = "Итоговый счет: " + score;
            int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, WIDTH / 2 - scoreWidth / 2, HEIGHT / 2 + 40);
            
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            String instructionText = "Нажмите ESC для возврата в меню";
            int instructionWidth = g.getFontMetrics().stringWidth(instructionText);
            g.drawString(instructionText, WIDTH / 2 - instructionWidth / 2, HEIGHT / 2 + 80);
        }
    }
}
