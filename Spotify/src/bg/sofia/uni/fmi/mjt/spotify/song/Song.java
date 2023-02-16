package bg.sofia.uni.fmi.mjt.spotify.song;

import java.nio.file.Path;

public class Song {
    private String name;
    private int numberStreams;


    public Song(String name) {
        this.name = name;
        this.numberStreams = 0;

    }

    public Path getPath() {

        return Path.of(name + ".wav");
    }

    public String getName() {
        return name;
    }

    public int getNumberStreams() {
        return numberStreams;
    }

    public void play() {
        numberStreams++;
    }

}
