import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Класс экрана настроек
 */
public class SettingsPanel extends JPanel {
    private JFrame parentFrame;
    private GamePreferences preferences;
    
    /**
     * Конструктор экрана настроек
     */
    public SettingsPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.preferences = GamePreferences.getInstance();
        
        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        setLayout(new BorderLayout());
        
        // Создаем заголовок
        JLabel titleLabel = new JLabel("Настройки", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLUE);
        
        // Создаем панель для элементов настроек
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(4, 1, 10, 20));
        settingsPanel.setOpaque(false);
        
        // Создаем элементы настроек
        final JCheckBox musicCheckbox = new JCheckBox("Музыка", preferences.isMusicEnabled());
        musicCheckbox.setFont(new Font("Arial", Font.BOLD, 18));
        musicCheckbox.setForeground(Color.BLACK);
        musicCheckbox.setOpaque(false);
        
        final JCheckBox soundCheckbox = new JCheckBox("Звуковые эффекты", preferences.isSoundEnabled());
        soundCheckbox.setFont(new Font("Arial", Font.BOLD, 18));
        soundCheckbox.setForeground(Color.BLACK);
        soundCheckbox.setOpaque(false);
        
        // Создаем панель для слайдера громкости
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volumePanel.setOpaque(false);
        
        JLabel volumeLabel = new JLabel("Громкость: ");
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        volumeLabel.setForeground(Color.BLACK);
        
        final JSlider volumeSlider = new JSlider(0, 100, (int)(preferences.getVolume() * 100));
        volumeSlider.setPreferredSize(new Dimension(200, 30));
        volumeSlider.setOpaque(false);
        
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        
        // Создаем панель для кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = createButton("Сохранить");
        JButton backButton = createButton("Назад");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);
        
        // Добавляем обработчики событий
        musicCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferences.setMusicEnabled(musicCheckbox.isSelected());
                preferences.save();
                
                // Воспроизводим или останавливаем музыку в зависимости от настройки
                if (musicCheckbox.isSelected()) {
                    AudioManager.getInstance().playMusic("menu", true);
                } else {
                    AudioManager.getInstance().stopMusic();
                }
            }
        });
        
        soundCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferences.setSoundEnabled(soundCheckbox.isSelected());
                preferences.save();
                
                // Воспроизводим тестовый звук, если звуки включены
                if (soundCheckbox.isSelected()) {
                    AudioManager.getInstance().playSound("coin");
                }
            }
        });
        
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float volume = volumeSlider.getValue() / 100.0f;
                preferences.setVolume(volume);
                preferences.save();
                
                // Воспроизводим тестовый звук при изменении громкости
                if (!volumeSlider.getValueIsAdjusting() && preferences.isSoundEnabled()) {
                    AudioManager.getInstance().playSound("coin");
                }
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Воспроизводим звук нажатия
                if (preferences.isSoundEnabled()) {
                    AudioManager.getInstance().playSound("jump");
                }
                
                // Возвращаемся в главное меню
                showMainMenu();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Воспроизводим звук нажатия
                if (preferences.isSoundEnabled()) {
                    AudioManager.getInstance().playSound("bump");
                }
                
                // Возвращаемся в главное меню без сохранения
                showMainMenu();
            }
        });
        
        // Добавляем элементы на панель настроек
        settingsPanel.add(musicCheckbox);
        settingsPanel.add(soundCheckbox);
        settingsPanel.add(volumePanel);
        settingsPanel.add(buttonPanel);
        
        // Добавляем компоненты на главную панель
        add(titleLabel, BorderLayout.NORTH);
        
        // Создаем центральную панель для размещения элементов по центру
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(settingsPanel);
        add(centerPanel, BorderLayout.CENTER);
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
        g.setColor(new Color(173, 216, 230)); // Светло-голубой цвет (Light Blue)
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Здесь можно добавить отрисовку фонового изображения, если оно будет добавлено
    }
}
