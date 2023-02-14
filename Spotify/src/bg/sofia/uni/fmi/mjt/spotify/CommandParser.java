package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistAlreadyExists;
import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.SongNotFound;

import java.lang.reflect.Array;
import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CommandParser implements Parser {
    public CommandParser() {
        spotify.addSong("smooth-ac-guitar-loop-93bpm-137706.wav");
    }

    private static Spotify spotify;

    static {
        spotify = new Spotify("Profiles");
    }

    @Override
    public String parse(SelectionKey key, String line) {

        String[] commands = line.split(" ");
        if (commands[0].equals("register")) {
            spotify.register(key, commands[1], commands[2]);

            return "New account was successfully created.";

        } else if (commands[0].equals("login")) {
            if (spotify.login(key, commands[1], commands[2])) {
                return "You have successfully signed in.";
            }
            return "Incorrect email or password. Please try again.";

        } else if (commands[0].equals("search")) {

            spotify.search(Arrays.stream(commands).skip(1).toList());
        } else if (commands[0].equals("top")) {

            return spotify.top(Integer.parseInt(commands[1]));

        } else if (commands[0].equals("create-playlist")) {
            try {
                spotify.createPlaylist(key, commands[1]);
            } catch (PlaylistAlreadyExists e) {
                throw new RuntimeException(e);
            }
            return "You have successfully created a new playlist.";

        } else if (commands[0].equals("add-song-to")) {
            try {
                spotify.addToPlaylist(key, commands[1], commands[2]);
            } catch (PlaylistNotFound e) {
                throw new RuntimeException(e);
            }

            return "New song added to playlist!";
        } else if (commands[0].equals("show-playlist")) {

            try {
                return spotify.showPlaylist(key, commands[1]);
            } catch (PlaylistNotFound e) {
                throw new RuntimeException(e);
            }

        } else if (line.contains("play")) {
            try {
                spotify.streamSong(key, commands[1]);
            } catch (SongNotFound e) {
                throw new RuntimeException(e);
            }
            return "play";
        } else if (line.contains("stop")) {
            spotify.stopPlaying(key);
            return "Music stopped.";
        } else if (line.contains("disconnect")) {
            spotify.logOut(key);
            return "Logged out.";
        }
        return "Nothing?";
    }
}
