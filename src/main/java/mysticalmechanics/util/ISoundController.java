package mysticalmechanics.util;

public interface ISoundController {
    void playSound(int id);

    void stopSound(int id);

    default boolean isSoundPlaying(int id) {
        return getPlayId(id) > 0;
    }

    int getPlayId(int id);

    int[] getSoundIDs();

    default void handleSound() {
        for (int id : getSoundIDs()) {
            boolean shouldPlay = shouldPlaySound(id);
            boolean isPlaying = isSoundPlaying(id);
            if(shouldPlay && !isPlaying)
                playSound(id);
            if(!shouldPlay && isPlaying)
                stopSound(id);
        }
    }

    default boolean shouldPlaySound(int id) {
        return false;
    }

    default float getCurrentVolume(int id, float volume) {
        return volume;
    }

    default float getCurrentPitch(int id, float pitch) {
        return pitch;
    }
}

