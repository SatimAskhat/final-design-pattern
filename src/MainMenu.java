import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Класс главного меню игры
 */
public class MainMenu extends JPanel {
    private JFrame parentFrame;
    
    /**
     * Конструктор главного меню
     */
    public MainMenu(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        
        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        setLayout(new BorderLayout());
        
        // Создаем заголовок
        JLabel titleLabel = new JLabel("Mario Style Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.RED);
        
        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 20));
        buttonPanel.setOpaque(false);
        
        // Создаем кнопки
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
        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        // Воспроизводим фоновую музыку
        AudioManager.getInstance().playMusic("menu", true);
    }
    
    /**
     * Создание стилизованной кнопки
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
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
     * Отрисовка компонентов
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Рисуем фон
        g.setColor(new Color(135, 206, 235)); // Светло-голубой цвет (Sky Blue)
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Здесь можно добавить отрисовку фонового изображения, если оно будет добавлено
    }
}
