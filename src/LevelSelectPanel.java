import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Класс экрана выбора уровня
 */
public class LevelSelectPanel extends JPanel {
    private JFrame parentFrame;
    private GamePreferences preferences;
    
    // Количество уровней в игре
    private static final int MAX_LEVELS = 10;
    
    /**
     * Конструктор экрана выбора уровня
     */
    public LevelSelectPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.preferences = GamePreferences.getInstance();
        
        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        setLayout(new BorderLayout());
        
        // Создаем заголовок
        JLabel titleLabel = new JLabel("Выбор уровня", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.GREEN);
        
        // Создаем панель для кнопок уровней
        JPanel levelsPanel = new JPanel();
        levelsPanel.setLayout(new GridLayout(4, 3, 10, 10));
        levelsPanel.setOpaque(false);
        
        // Все уровни разблокированы
        
        // Создаем кнопки для каждого уровня
        for (int i = 1; i <= MAX_LEVELS; i++) {
            final int level = i;
            JButton levelButton = createLevelButton(level, true); // Все уровни разблокированы
            
            levelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Все уровни разблокированы
                    // Воспроизводим звук нажатия
                    if (preferences.isSoundEnabled()) {
                        AudioManager.getInstance().playSound("jump");
                    }
                    
                    // Запускаем выбранный уровень
                    startGame(level);
                }
            });
            
            levelsPanel.add(levelButton);
        }
        
        // Добавляем кнопку "Назад"
        JButton backButton = createButton("Назад");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Воспроизводим звук нажатия
                if (preferences.isSoundEnabled()) {
                    AudioManager.getInstance().playSound("bump");
                }
                
                // Возвращаемся в главное меню
                showMainMenu();
            }
        });
        
        // Создаем панель для кнопки "Назад"
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setOpaque(false);
        backPanel.add(backButton);
        
        // Добавляем компоненты на главную панель
        add(titleLabel, BorderLayout.NORTH);
        
        // Создаем центральную панель для размещения кнопок уровней по центру
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(levelsPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        add(backPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Создание кнопки уровня
     */
    private JButton createLevelButton(int level, boolean unlocked) {
        JButton button = new JButton("Уровень " + level);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        
        if (unlocked) {
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(46, 139, 87)); // Sea Green
        } else {
            button.setForeground(Color.GRAY);
            button.setBackground(new Color(169, 169, 169)); // Dark Gray
        }
        
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 60));
        return button;
    }
    
    /**
     * Создание стилизованной кнопки
     */
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }
    
    /**
     * Запуск игры с указанным уровнем
     */
    private void startGame(int level) {
        // Останавливаем музыку меню
        AudioManager.getInstance().stopMusic();
        
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
     * Показать главное меню
     */
    private void showMainMenu() {
        // Создаем главное меню
        MainMenu mainMenu = new MainMenu(parentFrame);
        
        // Заменяем текущую панель на главное меню
        parentFrame.getContentPane().removeAll();
        parentFrame.add(mainMenu);
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
        g.setColor(new Color(152, 251, 152)); // Светло-зеленый цвет (Pale Green)
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Здесь можно добавить отрисовку фонового изображения, если оно будет добавлено
    }
}
