import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления звуками и музыкой в игре
 * Использует паттерн Singleton
 */
public class AudioManager {
    private static AudioManager instance;
    private GamePreferences prefs;
    
    // Карты для хранения звуков и музыки
    private Map<String, File> sounds;
    private Map<String, File> music;
    
    // Кэш звуковых файлов и музыки
    private Map<String, Clip> soundFiles;
    private Map<String, Clip> musicFiles;
    
    // Текущий проигрыватель музыки
    private Clip currentMusic;
    
    // Приватный конструктор для Singleton
    private AudioManager() {
        prefs = GamePreferences.getInstance();
        sounds = new HashMap<>();
        music = new HashMap<>();
        soundFiles = new HashMap<>();
        musicFiles = new HashMap<>();
        
        // Создаем директорию для аудио, если её нет
        File audioDir = new File("audio");
        if (!audioDir.exists()) {
            audioDir.mkdir();
        }
        
        // Создаем директорию для звуков, если её нет
        File soundsDir = new File("sounds");
        if (!soundsDir.exists()) {
            soundsDir.mkdir();
        }
        
        // Инициализируем пустые файлы для звуков, если их нет
        initializeAudioFiles();
    }
    
    /**
     * Получить экземпляр класса аудио менеджера
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Инициализация аудио файлов
     */
    private void initializeAudioFiles() {
        // Создаем пустые файлы для звуков, если их нет
        createEmptyAudioFile("audio/jump.wav");
        createEmptyAudioFile("audio/coin.wav");
        createEmptyAudioFile("audio/bump.wav");
        createEmptyAudioFile("audio/break.wav");
        createEmptyAudioFile("audio/powerup.wav");
        createEmptyAudioFile("audio/menu_music.wav");
        createEmptyAudioFile("audio/game_music.wav");
    }
    
    /**
     * Создать пустой аудио файл, если он не существует
     */
    private void createEmptyAudioFile(String name) {
        try {
            // Создаем директорию sounds в текущей директории, если её нет
            File soundsDir = new File("sounds");
            if (!soundsDir.exists()) {
                soundsDir.mkdir();
            }
            
            // Создаем пустой файл в директории sounds
            File file = new File("sounds/" + name + ".wav");
            if (!file.exists()) {
                // Создаем пустой файл
                file.createNewFile();
                
                // Записываем пустой WAV файл
                createEmptyWavFile(file);
                
                // Добавляем в кэш
                soundFiles.put(name, createEmptyClip());
                System.out.println("Создан пустой звуковой файл: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании аудио файла: " + e.getMessage());
        }
    }
    
    /**
     * Создать пустой WAV файл
     */
    private void createEmptyWavFile(File file) throws IOException {
        // Создаем пустой WAV файл с минимальным заголовком
        // Параметры формата: 44100 Гц, 16 бит, 1 канал (mono), signed, little endian
        byte[] data = new byte[44];
        
        // RIFF header
        data[0] = 'R'; data[1] = 'I'; data[2] = 'F'; data[3] = 'F';
        data[4] = 36; data[5] = 0; data[6] = 0; data[7] = 0;  // Размер файла - 8
        data[8] = 'W'; data[9] = 'A'; data[10] = 'V'; data[11] = 'E';
        
        // fmt chunk
        data[12] = 'f'; data[13] = 'm'; data[14] = 't'; data[15] = ' ';
        data[16] = 16; data[17] = 0; data[18] = 0; data[19] = 0;  // Размер fmt чанка
        data[20] = 1; data[21] = 0;  // PCM
        data[22] = 1; data[23] = 0;  // Моно
        data[24] = 68; data[25] = -84; data[26] = 0; data[27] = 0;  // Частота дискретизации (44100)
        data[28] = -120; data[29] = -11; data[30] = 1; data[31] = 0;  // Байт рейт (88200)
        data[32] = 2; data[33] = 0;  // Блок выравнивания
        data[34] = 16; data[35] = 0;  // Биты на сэмпл
        
        // data chunk
        data[36] = 'd'; data[37] = 'a'; data[38] = 't'; data[39] = 'a';
        data[40] = 0; data[41] = 0; data[42] = 0; data[43] = 0;  // Размер данных
        
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }
    
    /**
     * Создать пустой аудио клип
     */
    private Clip createEmptyClip() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            
            // Создаем пустой буфер данных
            byte[] data = new byte[0];
            AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(data),
                format,
                0
            );
            
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            System.out.println("Ошибка при создании пустого клипа: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Загрузить звуковой эффект
     */
    public void loadSound(String name, String path) {
        File soundFile = new File(path);
        if (soundFile.exists()) {
            sounds.put(name, soundFile);
        } else {
            System.out.println("Звуковой файл не найден: " + path);
        }
    }
    
    /**
     * Загрузить музыку
     */
    public void loadMusic(String name, String path) {
        File musicFile = new File(path);
        if (musicFile.exists()) {
            music.put(name, musicFile);
        } else {
            System.out.println("Музыкальный файл не найден: " + path);
        }
    }
    
    /**
     * Воспроизвести звуковой эффект
     */
    public void playSound(String name) {
        if (!prefs.isSoundEnabled()) return;
        
        try {
            // Проверяем, есть ли звук в кэше
            Clip clip = soundFiles.get(name);
            if (clip != null) {
                // Если звук в кэше, используем его
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0); // Перемотка на начало
                
                // Устанавливаем громкость
                setVolume(clip, prefs.getVolume());
                
                clip.start();
                return;
            }
            
            // Если звука нет в кэше, пробуем загрузить из файла
            File soundFile = sounds.get(name);
            if (soundFile != null && soundFile.exists() && soundFile.length() > 0) {
                try {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    
                    // Сохраняем в кэш
                    soundFiles.put(name, clip);
                    
                    // Устанавливаем громкость
                    setVolume(clip, prefs.getVolume());
                    
                    clip.start();
                } catch (Exception e) {
                    System.out.println("Ошибка при воспроизведении звука " + name + ": " + e.getMessage());
                    // Попробуем загрузить звук снова из директории sounds
                    String[] audioPaths = {"sounds/"};
                    tryLoadSound(name, audioPaths);
                    // И попробуем воспроизвести еще раз
                    clip = soundFiles.get(name);
                    if (clip != null) {
                        clip.setFramePosition(0);
                        setVolume(clip, prefs.getVolume());
                        clip.start();
                    }
                }
            } else {
                System.out.println("Звуковой файл не найден: " + name);
                // Попробуем загрузить звук из директории sounds
                String[] audioPaths = {"sounds/"};
                tryLoadSound(name, audioPaths);
                // И попробуем воспроизвести еще раз
                clip = soundFiles.get(name);
                if (clip != null) {
                    clip.setFramePosition(0);
                    setVolume(clip, prefs.getVolume());
                    clip.start();
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при воспроизведении звука: " + e.getMessage());
        }
    }
    
    /**
     * Воспроизвести музыку
     */
    public void playMusic(String name, boolean loop) {
        if (!prefs.isMusicEnabled()) return;
        
        // Останавливаем текущую музыку
        stopMusic();
        
        try {
            // Проверяем, есть ли музыка в кэше
            Clip clip = musicFiles.get(name);
            if (clip != null) {
                // Если музыка в кэше, используем её
                clip.setFramePosition(0); // Перемотка на начало
                
                // Устанавливаем громкость
                setVolume(clip, prefs.getVolume());
                
                if (loop) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                
                currentMusic = clip;
                currentMusic.start();
                return;
            }
            
            // Если музыки нет в кэше, пробуем загрузить из файла
            File musicFile = music.get(name);
            if (musicFile != null && musicFile.exists() && musicFile.length() > 0) {
                try {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicFile);
                    currentMusic = AudioSystem.getClip();
                    currentMusic.open(audioIn);
                    
                    // Сохраняем в кэш
                    musicFiles.put(name, currentMusic);
                    
                    // Устанавливаем громкость
                    setVolume(currentMusic, prefs.getVolume());
                    
                    if (loop) {
                        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                    
                    currentMusic.start();
                } catch (Exception e) {
                    System.out.println("Ошибка при воспроизведении музыки " + name + ": " + e.getMessage());
                    // Попробуем загрузить музыку снова из директории sounds
                    String[] audioPaths = {"sounds/"};
                    tryLoadMusic(name, audioPaths);
                    // И попробуем воспроизвести еще раз
                    Clip newClip = musicFiles.get(name);
                    if (newClip != null) {
                        currentMusic = newClip;
                        currentMusic.setFramePosition(0);
                        setVolume(currentMusic, prefs.getVolume());
                        if (loop) {
                            currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                        currentMusic.start();
                    }
                }
            } else {
                System.out.println("Музыкальный файл не найден: " + name);
                // Попробуем загрузить музыку из директории sounds
                String[] audioPaths = {"sounds/"};
                tryLoadMusic(name, audioPaths);
                // И попробуем воспроизвести еще раз
                Clip newClip = musicFiles.get(name);
                if (newClip != null) {
                    currentMusic = newClip;
                    currentMusic.setFramePosition(0);
                    setVolume(currentMusic, prefs.getVolume());
                    if (loop) {
                        currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                    currentMusic.start();
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при воспроизведении музыки: " + e.getMessage());
        }
    }
    
    /**
     * Установить громкость для клипа
     */
    private void setVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
            gainControl.setValue(dB);
        }
    }
    
    /**
     * Остановить музыку
     */
    public void stopMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
            currentMusic.close();
        }
    }
    
    /**
     * Приостановить музыку
     */
    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
        }
    }
    
    /**
     * Возобновить музыку
     */
    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isRunning() && prefs.isMusicEnabled()) {
            currentMusic.start();
        }
    }
    
    /**
     * Загрузить все аудио файлы
     */
    public void loadAllAudio() {
        // Используем только директорию sounds
        String[] audioPaths = {"sounds/"};
        
        // Загрузка звуков
        // Пытаемся загрузить из директории sounds
        tryLoadSound("jump", audioPaths);           // Прыжок
        tryLoadSound("jumpsmall", audioPaths);      // Маленький прыжок
        tryLoadSound("coin", audioPaths);           // Сбор монеты
        tryLoadSound("bump", audioPaths);           // Столкновение
        tryLoadSound("brick", audioPaths);          // Удар о блок
        tryLoadSound("powerup", audioPaths);        // Бонус
        tryLoadSound("1up", audioPaths);            // Дополнительная жизнь
        tryLoadSound("death", audioPaths);          // Смерть игрока
        tryLoadSound("gameover", audioPaths);       // Конец игры
        tryLoadSound("pause", audioPaths);          // Пауза
        tryLoadSound("item", audioPaths);           // Подбор предмета
        tryLoadSound("flagpole", audioPaths);       // Флагшток (завершение уровня)
        tryLoadSound("fireball", audioPaths);       // Огненный шар
        tryLoadSound("kickkill", audioPaths);       // Убийство врага
        
        // Загрузка музыки
        tryLoadMusic("menu", audioPaths);           // Музыка для меню
        tryLoadMusic("game", audioPaths);           // Музыка для игры
    }
    
    /**
     * Пытается загрузить звук из директории sounds
     */
    private void tryLoadSound(String name, String[] paths) {
        // Проверяем, есть ли уже звук в кэше
        if (soundFiles.containsKey(name)) {
            return;
        }
        
        boolean found = false;
        try {
            // Используем только директорию sounds
            String path = "sounds/" + name + ".wav";
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                // Загружаем звук и добавляем в кэш
                sounds.put(name, file);
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    soundFiles.put(name, clip);
                    found = true;
                    System.out.println("Загружен звук: " + file.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("Ошибка при загрузке звука " + name + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибку
            System.out.println("Ошибка при попытке загрузить звук: " + e.getMessage());
        }
        
        // Если файл не найден, создаем пустой аудиофайл
        if (!found) {
            System.out.println("Звук " + name + " не найден, создаем пустой файл");
            createEmptyAudioFile(name);
        }
    }
    
    /**
     * Пытается загрузить музыку из директории sounds
     */
    private void tryLoadMusic(String name, String[] paths) {
        // Проверяем, есть ли уже музыка в кэше
        if (musicFiles.containsKey(name)) {
            return;
        }
        
        boolean found = false;
        try {
            // Используем только директорию sounds
            String path = "sounds/" + name + ".wav";
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                // Загружаем музыку и добавляем в кэш
                music.put(name, file);
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    musicFiles.put(name, clip);
                    found = true;
                    System.out.println("Загружена музыка: " + file.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("Ошибка при загрузке музыки " + name + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибку
            System.out.println("Ошибка при попытке загрузить музыку: " + e.getMessage());
        }
        
        // Если файл не найден, создаем пустой аудиофайл
        if (!found) {
            System.out.println("Музыка " + name + " не найдена, создаем пустой файл");
            createEmptyAudioFile(name);
        }
    }
}
