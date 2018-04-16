package music.lisong.com.lisongmusic.storyaudioservice;

public interface Callback {
        void onCompletion(String currentPlayUrl, int currentVoiceId, String nextPlayUrl);
        void onPlaybackStatusChanged(String playUrl, int voiceId, int state);
        void onError(String playUrl, int voiceId, String error);
        void onProgress(String playUrl, int voiceId, long progress, long duration);
        void onPlayBegin(String playUrl, int voiceId);
        void onPlayOver();
    }
