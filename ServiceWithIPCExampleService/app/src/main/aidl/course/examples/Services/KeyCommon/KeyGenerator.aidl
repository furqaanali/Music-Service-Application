package course.examples.Services.KeyCommon;

interface KeyGenerator {
    String[] getKey();
    String getString();
    Bitmap getImage(int imageNumber);
    void startMusic(int audioNumber);
    void pauseMusic();
    void stopMusic();
}