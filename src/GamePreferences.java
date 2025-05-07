import java.io.*;
import java.util.Properties;

/**
 * Класс для управления настройками игры
 * Использует паттерн Singleton
 */
public class GamePreferences {
    private static GamePreferences instance;
    private Properties properties;
    private final String PREFS_FILE = "game_preferences.properties";
    
    // Ключи для настроек
    private final String KEY_MUSIC_ENABLED = "music_enabled";
    private final String KEY_SOUND_ENABLED = "sound_enabled";
    private final String KEY_VOLUME = "volume";
    private final String KEY_CURRENT_LEVEL = "current_level";
    
    // Приватный конструктор для Singleton
    private GamePreferences() {
        properties = new Properties();
        load();
    }
    
    /**
     * Получить экземпляр класса настроек
     */
    public static GamePreferences getInstance() {
        if (instance == null) {
            instance = new GamePreferences();
        }
        return instance;
    }
    
    /**
     * Загрузить настройки из файла
     */
    private void load() {
        try {
            File file = new File(PREFS_FILE);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                properties.load(fis);
                fis.close();
            } else {
                // Установить значения по умолчанию
                setDefaults();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке настроек: " + e.getMessage());
            setDefaults();
        }
    }
    
    /**
     * Установить значения по умолчанию
     */
    private void setDefaults() {
        properties.setProperty(KEY_MUSIC_ENABLED, "true");
        properties.setProperty(KEY_SOUND_ENABLED, "true");
        properties.setProperty(KEY_VOLUME, "0.7");
        properties.setProperty(KEY_CURRENT_LEVEL, "1");
        save();
    }
    
    /**
     * Сохранить настройки в файл
     */
    public void save() {
        try {
            FileOutputStream fos = new FileOutputStream(PREFS_FILE);
            properties.store(fos, "Game Preferences");
            fos.close();
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении настроек: " + e.getMessage());
        }
    }
    
    /**
     * Проверить, включена ли музыка
     */
    public boolean isMusicEnabled() {
        return Boolean.parseBoolean(properties.getProperty(KEY_MUSIC_ENABLED, "true"));
    }
    
    /**
     * Установить состояние музыки
     */
    public void setMusicEnabled(boolean enabled) {
        properties.setProperty(KEY_MUSIC_ENABLED, String.valueOf(enabled));
    }
    
    /**
     * Проверить, включены ли звуковые эффекты
     */
    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(properties.getProperty(KEY_SOUND_ENABLED, "true"));
    }
    
    /**
     * Установить состояние звуковых эффектов
     */
    public void setSoundEnabled(boolean enabled) {
        properties.setProperty(KEY_SOUND_ENABLED, String.valueOf(enabled));
    }
    
    /**
     * Получить громкость (от 0.0 до 1.0)
     */
    public float getVolume() {
        return Float.parseFloat(properties.getProperty(KEY_VOLUME, "0.7"));
    }
    
    /**
     * Установить громкость
     */
    public void setVolume(float volume) {
        properties.setProperty(KEY_VOLUME, String.valueOf(volume));
    }
    
    /**
     * Получить текущий уровень
     */
    public int getCurrentLevel() {
        return Integer.parseInt(properties.getProperty(KEY_CURRENT_LEVEL, "1"));
    }
    
    /**
     * Установить текущий уровень
     */
    public void setCurrentLevel(int level) {
        properties.setProperty(KEY_CURRENT_LEVEL, String.valueOf(level));
    }
}
