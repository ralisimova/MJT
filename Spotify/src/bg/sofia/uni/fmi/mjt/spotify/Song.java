package bg.sofia.uni.fmi.mjt.spotify;

import javax.sound.sampled.AudioFormat;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class Song  {
    private String name;
    private String artist;
    private int duration;

   // private AudioFormat format;
   // private AtomicInteger numberStreams;
   private int numberStreams;

    public Song(String name) {
        this.name = name;
    }

    public Song(String name, String artist, int numberStreams) {
        this.name = name;
        this.artist = artist;
       // this.format = format;
        this.numberStreams = numberStreams;
    }

    public Song(String name, String artist) {
        this.name = name;
        this.artist = artist;
       // this.format = format;
        this.numberStreams = 0;

    }
    public Path getPath() {
        return Path.of("resources", "songs", this.artist + " - " + this.name + " - " + this.duration + ".wav");
    }
        public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }



    public int getNumberStreams() {
        return numberStreams;
    }

    public void play(){
        numberStreams++;
    }


}
