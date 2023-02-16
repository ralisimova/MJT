package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistAlreadyExists;
import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.SongNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.UserNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.UsersAlreadyExists;
import bg.sofia.uni.fmi.mjt.spotify.song.Song;
import bg.sofia.uni.fmi.mjt.spotify.song.SongThread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Spotify implements SpotifyAPI {
    private final String profilesFile;
    private final Map<String, Set<String>> playlists;
    private final Map<SelectionKey, String> loggedInUsers;
    private final Map<SelectionKey, SongThread> currentlyPlaying;
    private final Set<Song> songs;

    boolean fileHasThisLine(String fileName, String toSearch) {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileName))) {
            String line = null;
            while ((line = fileReader.readLine()) != null) {
                if (line.contains(toSearch)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private Song getSong(String name) {
        List<Song> result = songs.stream()
            .filter((x) -> x.getName().equals(name))
            .toList();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public String getUserByKey(SelectionKey key) {
        return loggedInUsers.get(key);
    }

    public void addSong(String name) {
        songs.add(new Song(name));
    }

    public Map<String, Set<String>> getPlaylists() {
        return playlists;
    }

    public Spotify(String profilesFile) {
        this.profilesFile = profilesFile;
        this.playlists = new HashMap<>();
        this.loggedInUsers = new HashMap<>();
        this.currentlyPlaying = new HashMap<>();
        this.songs = new HashSet<>();

        addSongs();
    }

    public void addSongs() {
        List<String> songs = List.of(new String[] {"Arctic_Monkeys_Do_I_Wanna_Know",
            "Arctic_Monkeys_I_Wanna_Be_Yours",
            "Maneskin_Beggin", "Måneskin_CORALINE", "Måneskin_I_WANNA_BE_YOUR_SLAVE",
            "Miley_Cyrus_Flowers"});
        for (String song : songs) {
            this.addSong(song);
        }
    }


    public void logOut(SelectionKey key) {
        loggedInUsers.remove(key);
    }

    private boolean userAlreadyExists(String email) {
        return fileHasThisLine(profilesFile, email);
    }

    @Override
    public void register(SelectionKey key, String email, String password) throws UsersAlreadyExists {
        try (FileWriter fileWriter = new FileWriter(profilesFile, true)) {
            if (!userAlreadyExists(email)) {
                fileWriter.write(email + " " + password + System.lineSeparator());
                fileWriter.flush();
                loggedInUsers.put(key, email);
            } else {
                throw new UsersAlreadyExists("User with this email already exists.");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNumberLoggedInUsers() {
        return loggedInUsers.size();
    }

    @Override
    public void login(SelectionKey key, String email, String password) throws UserNotFound {
        if (fileHasThisLine(profilesFile, email + " " + password)) {
            loggedInUsers.put(key, email);
            return;
        }
        throw new UserNotFound("Invalid email or password. Please try again.");
    }

    @Override
    public Set<String> search(List<String> words) {
        if (words == null || words.isEmpty()) {
            return null;
        }
        return songs.stream()
            .map(Song::getName)
            .filter(name -> new HashSet<>(List.of(name.split("_"))).containsAll(words))
            .collect(Collectors.toSet());
    }

    @Override
    public String top(int number) {
        if (number < 0) {
            return "Please enter a positive number.";
        }
        return songs.stream()
            .sorted(Comparator.comparing(Song::getNumberStreams))
            .limit(number)
            .map(Song::getName)
            .collect(Collectors.joining(" ", "{", "}"));
    }

    @Override
    public void createPlaylist(SelectionKey key, String name) throws PlaylistAlreadyExists {
        playlists.putIfAbsent(getUserByKey(key), new HashSet<>());

        if (!playlists.get(getUserByKey(key)).isEmpty()
            && playlists.get(getUserByKey(key)).contains(name)) {
            throw new PlaylistAlreadyExists("You already have a playlist with this name.");
        }
        playlists.get(getUserByKey(key)).add(name);
    }

    @Override
    public void addToPlaylist(SelectionKey key, String playlist, String song) throws PlaylistNotFound {
        if (playlists == null || playlists.isEmpty()
            || playlists.get(getUserByKey(key)) == null
            || playlists.get(getUserByKey(key)).isEmpty()
            || !playlists.get(getUserByKey(key)).contains(playlist)) {
            throw new PlaylistNotFound("You don't have a playlist with this name yet.");

        }
        if (!fileHasThisLine(playlist, song)) {

            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(playlist, true))) {
                fileWriter.write(song + System.lineSeparator());
                fileWriter.flush();

            } catch (IOException e) {

                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String showPlaylist(SelectionKey key, String name) throws PlaylistNotFound {
        if (playlists == null || playlists.isEmpty()
            || playlists.get(getUserByKey(key)) == null
            || playlists.get(getUserByKey(key)).isEmpty()
            || !playlists.get(getUserByKey(key)).contains(name)) {
            throw new PlaylistNotFound("You don't have a playlist with this name yet.");

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
            if (songToPlay == null) {
                throw new SongNotFound("We can't find the song you are looking for.");
            }
            File songFile = songToPlay.getPath().toFile();
            if (!songFile.exists()) {
                throw new SongNotFound("We know about the requested song, " +
                    "but it doesn't exist in our database. " +
                    "It cannot be played.");
            }
            SongThread songThread = new SongThread(songToPlay, key);
            getSong(songName).play();

            this.currentlyPlaying.put(key, songThread);
            songThread.start();

        } catch (NullPointerException e) {
            throw new SongNotFound("Song" + songName + " was not found.");
        }
    }

    public void stopPlaying(SelectionKey key) {
        if (currentlyPlaying == null || currentlyPlaying.isEmpty()) {
            return;
        }
        currentlyPlaying.get(key).stopPlaying();
    }
}
