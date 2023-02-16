package bg.sofia.uni.fmi.mjt.spotify.song;

import java.nio.file.Path;
import java.util.Objects;

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
        return Path.of(name+ ".wav");
       // return Path.of("resources",   this.name  + ".wav");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(name, song.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
