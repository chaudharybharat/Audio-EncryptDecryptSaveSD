package com.example.saveaudiofilesd.model;

public class AudioModel {
    String audio;
    String path;

    public AudioModel(String audio, String path) {
        this.audio = audio;
        this.path = path;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
