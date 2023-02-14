package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistAlreadyExists;
import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.SongNotFound;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.channels.SelectionKey;
import java.util.*;
import java.util.stream.Collectors;

public class Spotify implements SpotifyAPI {
    // private File profiles;
    private String profilesFile;
    private Map<String,Set<String>> playlists;
    private Map<SelectionKey,String>loggedInUsers;
    private  Map<SelectionKey, SongThread> currentlyPlaying;
    private Set<Song> songs;

    private Song getSong(String name) {
        return songs.stream()
            .filter((x) -> x.getName().equals(name))
            .toList().get(0);
    }
private String getUserByKey(SelectionKey key){
        return loggedInUsers.get(key);
}

public void addSong(String name){
        songs.add(new Song(name));
}

    public Spotify(String profilesFile) {
        this.profilesFile = profilesFile;
        this.playlists = new HashMap<>();
        this.loggedInUsers = new HashMap<>();
        this.currentlyPlaying = new HashMap<>();
        this.songs = new HashSet<>();
    }
    public Spotify(String profilesFile, Map<String, Set<String>> playlists, Map<SelectionKey, String> loggedInUsers,
                   Map<SelectionKey, SongThread> currentlyPlaying, Set<Song> songs) {
        this.profilesFile = profilesFile;
        this.playlists = playlists;
        this.loggedInUsers = loggedInUsers;
        this.currentlyPlaying = currentlyPlaying;
        this.songs = songs;
    }
    public void logOut(SelectionKey key){
    loggedInUsers.remove(key);
}
private boolean userAlreadyExists(String email){
    try (BufferedReader fileReader = new BufferedReader(new FileReader(profilesFile))) {
        String line = null;
        while ((line = fileReader.readLine()) != null) {
            if (line.contains(email)) {
                return true;
            }
        }
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    return false;
}

    @Override
    public void register(SelectionKey key,String email, String password) {
        try (FileWriter fileWriter = new FileWriter(profilesFile, true)) {
          if(!userAlreadyExists(email)) {
              fileWriter.write(email + " " + password + System.lineSeparator());
              fileWriter.flush();
              loggedInUsers.put(key,email);
          }
          else {
              throw new RuntimeException("user already exists.");

          }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean login(SelectionKey key,String email, String password) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(profilesFile))) {
            String profile = new String(email + " " + password);
            String line = null;
            while ((line = fileReader.readLine()) != null) {
                if (line.equals(profile)) {
                    loggedInUsers.put(key,email);
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public String getSongs() {
        return new String();
    }

    @Override
    public Set<String> search(List<String> words) {
        return songs.stream()
            .filter(x -> List.of(x.getName()).containsAll(words)
                || List.of(x.getArtist()).containsAll(words))
            .map(Song::getName)
            .collect(Collectors.toSet());
    }

    @Override
    public String top(int number) {
        return songs.stream()
            .sorted(Comparator.comparing(Song::getNumberStreams))
            .limit(number)
            .map(Song::getName)
            .collect(Collectors.joining(" ", "{", "}"));
    }

    @Override
    public void createPlaylist(SelectionKey key,String name) throws PlaylistAlreadyExists {
        //  File newPlaylist = new File(name);
        if( playlists.get( getUserByKey(key)).contains(name)){
            throw new PlaylistAlreadyExists();
        }
        playlists.get( getUserByKey(key)).add(name);
    }

    @Override
    public void addToPlaylist(SelectionKey key,String playlist, String song) throws PlaylistNotFound {
        //individual playlist!!!!!!!!!!
        if (!playlists.get( getUserByKey(key)).contains(playlist)) {
            throw new PlaylistNotFound();

          //  return;//??????
        }
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(playlist, true))) {
            fileWriter.write(song);
            fileWriter.flush();

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public String showPlaylist(SelectionKey key,String name) throws PlaylistNotFound {
        if (!playlists.get( getUserByKey(key)).contains(name)) {
            throw new PlaylistNotFound();
        //    return "No such playlist.";//??????
        }
        try (BufferedReader fileReader = new BufferedReader(new FileReader(name))) {
            StringBuilder playlist = new StringBuilder(name + System.lineSeparator());
            String line = null;
            while ((line = fileReader.readLine()) != null) {
                playlist.append(line).append(System.lineSeparator());
            }
            return String.valueOf(playlist);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public void streamSong(SelectionKey key, String songName) throws SongNotFound {
        Objects.requireNonNull(key);
        Objects.requireNonNull(songName);
        Song songToPlay;
        try {
            songToPlay = getSong(songName);

            File songFile = songToPlay.getPath().toFile();
            if (!songFile.exists()) {
            throw new SongNotFound("We know about the requested song, but it doesn't exist in our database. " +
                "It cannot be played.");
            }
            // check whether the song is already running is performed in the client
            SongThread songThread = new SongThread(songToPlay, key);
            getSong(songName).play();

               this.currentlyPlaying.put(key, songThread);
            songThread.start();
            //   this.changeHasOccurred = true;

        } catch (NullPointerException e) {
             throw new SongNotFound("Song" + songName + " was not found");
        }/* catch (SongNotFound e) {
            throw new RuntimeException(e);
        }*/
    }
    public void stopPlaying(SelectionKey key){
    currentlyPlaying.get(key).stopPlaying();
    }
}
