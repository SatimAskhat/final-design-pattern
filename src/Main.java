import javax.swing.*;

/**
 * Главный класс игры, использующий Java Swing
 */
public class Main {
    // Точка входа в программу
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Инициализация менеджера аудио
            AudioManager audioManager = AudioManager.getInstance();
            audioManager.loadAllAudio();
            
            // Инициализация менеджера текстур
            TextureManager textureManager = TextureManager.getInstance();
            textureManager.loadAllTextures();
            
            // Инициализация настроек игры
            GamePreferences.getInstance();
            
            // Создание и настройка главного окна
            JFrame frame = new JFrame("Mario Style Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            // Создание главного меню
            MainMenu mainMenu = new MainMenu(frame);
            frame.add(mainMenu);
            
            // Отображение окна
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}