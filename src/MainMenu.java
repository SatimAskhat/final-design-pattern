import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Класс главного меню игры
 */
public class MainMenu extends JPanel {
    private JFrame parentFrame;
    private BufferedImage backgroundImage;
    private BufferedImage logoImage;
    private Color gradientStart = new Color(0, 102, 204);
    private Color gradientEnd = new Color(51, 204, 255);
    private Font titleFont;
    private Font buttonFont;
    private int cloudX1 = 0;
    private int cloudX2 = 300;
    private int cloudX3 = 600;
    private Timer animationTimer;
    
    /**
     * Конструктор главного меню
     */
    public MainMenu(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        
        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        setLayout(new BorderLayout());
        
        // Загружаем шрифты
        try {
            titleFont = new Font("Arial", Font.BOLD, 48);
            buttonFont = new Font("Arial", Font.BOLD, 22);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки шрифтов: " + e.getMessage());
            titleFont = new Font("Arial", Font.BOLD, 48);
            buttonFont = new Font("Arial", Font.BOLD, 22);
        }
        
        // Загружаем фоновое изображение и логотип
        try {
            // Пытаемся загрузить фоновое изображение из нескольких возможных мест
            File bgFile = new File("src/textures/background.png");
            if (!bgFile.exists()) {
                bgFile = new File("textures/background.png");
            }
            
            if (bgFile.exists()) {
                backgroundImage = ImageIO.read(bgFile);
            }
            
            // Пытаемся загрузить логотип
            File logoFile = new File("src/textures/logo.png");
            if (!logoFile.exists()) {
                logoFile = new File("textures/logo.png");
            }
            
            if (logoFile.exists()) {
                logoImage = ImageIO.read(logoFile);
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображений: " + e.getMessage());
        }
        
        // Создаем заголовок
        JLabel titleLabel = new JLabel("", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(255, 215, 0)); // Золотой цвет
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Добавляем тень к тексту и стилизацию
        titleLabel.putClientProperty("html.disable", Boolean.FALSE);
        titleLabel.setText("<html><div style='text-align: center; text-shadow: 3px 3px 5px #000000;'>"
                + "<span style='color: #FFD700; font-size: 52px;'>Adventure</span><br>"
                + "<span style='color: #FF6347; font-size: 42px;'>Time</span>"
                + "</div></html>");
        
        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80)); // Добавляем отступы
        
        // Создаем кнопки с эффектом наведения
        JButton playButton = createButton("Играть");
        JButton levelSelectButton = createButton("Выбор уровня");
        JButton settingsButton = createButton("Настройки");
        JButton exitButton = createButton("Выход");
        
        // Добавляем обработчики событий для кнопок
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame(1); // Начать с первого уровня
            }
        });
        
        levelSelectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLevelSelect();
            }
        });
        
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSettings();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Добавляем кнопки на панель
        buttonPanel.add(playButton);
        buttonPanel.add(levelSelectButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);
        
        // Добавляем компоненты на главную панель
        add(titleLabel, BorderLayout.NORTH);
        
        // Создаем центральную панель для размещения кнопок по центру
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        // Добавляем отступы вокруг кнопок
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Добавляем декоративную панель вокруг кнопок
        JPanel decorativePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Создаем полупрозрачный фон
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Добавляем рамку
                g2d.setColor(new Color(255, 215, 0, 150));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
                
                g2d.dispose();
            }
        };
        decorativePanel.setOpaque(false);
        decorativePanel.setLayout(new BorderLayout());
        decorativePanel.add(buttonPanel, BorderLayout.CENTER);
        
        centerPanel.add(decorativePanel);
        add(centerPanel, BorderLayout.CENTER);
        
        // Добавляем информационную панель внизу
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel infoLabel = new JLabel("© 2025 Mario Style Game");
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(infoLabel);
        
        add(infoPanel, BorderLayout.SOUTH);
        
        // Воспроизводим фоновую музыку
        AudioManager.getInstance().playMusic("menu", true);
    }
    
    /**
     * Создание стилизованной кнопки
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                // Рисуем градиентный фон кнопки
                Color topColor = new Color(220, 53, 69); // Красный (в стиле Марио)
                Color bottomColor = new Color(240, 73, 89);
                
                // Если кнопка нажата, меняем цвета
                if (getModel().isPressed()) {
                    topColor = new Color(180, 33, 49);
                    bottomColor = new Color(200, 53, 69);
                } else if (getModel().isRollover()) {
                    // Если курсор над кнопкой, делаем цвета ярче
                    topColor = new Color(240, 73, 89);
                    bottomColor = new Color(255, 93, 109);
                }
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, topColor,
                    0, height, bottomColor
                );
                
                g2d.setPaint(gradient);
                
                // Рисуем скругленный прямоугольник
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, width - 1, height - 1, 20, 20 // Более скругленные углы
                );
                
                g2d.fill(roundedRectangle);
                
                // Рисуем границу
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.draw(roundedRectangle);
                
                // Рисуем блик вверху кнопки
                g2d.setColor(new Color(255, 255, 255, 70));
                g2d.fillRoundRect(5, 5, width - 10, height / 2 - 5, 15, 15);
                
                // Добавляем тень
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, height - 5, width - 6, 5, 10, 10);
                
                // Рисуем текст с точным центрированием
                g2d.setFont(buttonFont);
                FontMetrics fm = g2d.getFontMetrics();
                
                // Вычисляем точные размеры текста
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                
                // Точно центрируем текст по горизонтали и вертикали
                int textX = (width - textWidth) / 2;
                int textY = height / 2 + (fm.getAscent() - fm.getDescent()) / 2;
                
                // Текст с тенью для лучшей читаемости
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(text, textX + 2, textY + 2);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(250, 60); // Увеличиваем размер кнопки
            }
        };
        
        // Убираем стандартные настройки кнопки
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        
        // Добавляем эффект наведения
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Добавляем звук при наведении
                AudioManager.getInstance().playSound("menu_hover");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                // Добавляем звук при нажатии
                AudioManager.getInstance().playSound("menu_select");
            }
        });
        
        return button;
    }
    
    /**
     * Запуск игры с указанным уровнем
     */
    private void startGame(int level) {
        // Останавливаем музыку меню
        AudioManager.getInstance().stopMusic();
        
        // Воспроизводим звук нажатия
        AudioManager.getInstance().playSound("jump");
        
        // Создаем игровую панель с указанным уровнем
        GamePanel gamePanel = new GamePanel(level, parentFrame);
        
        // Заменяем текущую панель на игровую
        parentFrame.getContentPane().removeAll();
        parentFrame.add(gamePanel);
        parentFrame.revalidate();
        parentFrame.repaint();
        
        // Фокус на игровую панель и запуск игры
        gamePanel.requestFocus();
        gamePanel.startGame();
    }
    
    /**
     * Показать экран выбора уровня
     */
    private void showLevelSelect() {
        // Воспроизводим звук нажатия
        AudioManager.getInstance().playSound("coin");
        
        // Создаем экран выбора уровня
        LevelSelectPanel levelSelectPanel = new LevelSelectPanel(parentFrame);
        
        // Заменяем текущую панель на экран выбора уровня
        parentFrame.getContentPane().removeAll();
        parentFrame.add(levelSelectPanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    /**
     * Показать экран настроек
     */
    private void showSettings() {
        // Воспроизводим звук нажатия
        AudioManager.getInstance().playSound("coin");
        
        // Создаем экран настроек
        SettingsPanel settingsPanel = new SettingsPanel(parentFrame);
        
        // Заменяем текущую панель на экран настроек
        parentFrame.getContentPane().removeAll();
        parentFrame.add(settingsPanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    /**
     * Запуск анимации облаков
     */
    private void startAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Анимация движения облаков
                cloudX1 = (cloudX1 + 1) % (getWidth() + 200);
                cloudX2 = (cloudX2 + 1) % (getWidth() + 200);
                cloudX3 = (cloudX3 + 1) % (getWidth() + 200);
                
                repaint();
            }
        });
        
        animationTimer.start();
    }
    
    /**
     * Остановка анимации
     */
    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
    
    /**
     * Отрисовка облака
     */
    private void drawCloud(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillOval(x, y, width, height);
        g2d.fillOval(x + width/2, y - height/4, width, height);
        g2d.fillOval(x + width, y, width, height);
    }
    
    /**
     * Отрисовка компонентов
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Рисуем градиентный фон
        GradientPaint backgroundGradient = new GradientPaint(
            0, 0, gradientStart,
            0, height, gradientEnd
        );
        g2d.setPaint(backgroundGradient);
        g2d.fillRect(0, 0, width, height);
        
        // Если есть фоновое изображение, рисуем его
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, width, height, null);
        } else {
            // Рисуем декоративные элементы если нет фонового изображения
            
            // Рисуем солнце
            g2d.setColor(new Color(255, 255, 150));
            g2d.fillOval(width - 100, 50, 80, 80);
            
            // Рисуем лучи солнца
            g2d.setStroke(new BasicStroke(3));
            for (int i = 0; i < 12; i++) {
                double angle = Math.toRadians(i * 30);
                int startX = width - 60 + (int)(50 * Math.cos(angle));
                int startY = 90 + (int)(50 * Math.sin(angle));
                int endX = width - 60 + (int)(70 * Math.cos(angle));
                int endY = 90 + (int)(70 * Math.sin(angle));
                g2d.drawLine(startX, startY, endX, endY);
            }
            
            // Рисуем облака
            drawCloud(g2d, cloudX1 - 200, 70, 80, 50);
            drawCloud(g2d, cloudX2 - 200, 120, 100, 60);
            drawCloud(g2d, cloudX3 - 200, 40, 90, 55);
            
            // Рисуем холмы
            g2d.setColor(new Color(34, 139, 34)); // Зеленый
            g2d.fillOval(-50, height - 100, 300, 200);
            g2d.fillOval(200, height - 80, 250, 150);
            g2d.fillOval(400, height - 120, 350, 250);
            g2d.fillOval(width - 200, height - 90, 300, 180);
        }
        
        // Если есть логотип, рисуем его
        if (logoImage != null) {
            int logoWidth = logoImage.getWidth();
            int logoHeight = logoImage.getHeight();
            g2d.drawImage(logoImage, (width - logoWidth) / 2, 20, null);
        }
        
        // Запускаем анимацию, если она еще не запущена
        if (animationTimer == null || !animationTimer.isRunning()) {
            startAnimation();
        }
    }
}
