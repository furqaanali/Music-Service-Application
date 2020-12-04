package myAPI;

interface FunCenterFunctions {
    String[] getKey();
    String getString();
    Bitmap getImage(int imageNumber);
    void startMusic(int audioNumber);
    void pauseMusic();
    void stopMusic();
}