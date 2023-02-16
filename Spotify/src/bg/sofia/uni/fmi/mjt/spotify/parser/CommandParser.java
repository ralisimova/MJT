package bg.sofia.uni.fmi.mjt.spotify.parser;

import bg.sofia.uni.fmi.mjt.spotify.Spotify;
import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistAlreadyExists;
import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.SongNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.UserNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.UsersAlreadyExists;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class CommandParser implements Parser {
    private static String errorsFile = "ErrorsFile.txt";
    private static final int SECOND_ARG = 2;
    private static final int MIN_SIZE_TWO = 2;
    private static final int MIN_SIZE_TREE = 3;


    public CommandParser() {
    }

    private static final Spotify spotify;

    static {
        spotify = new Spotify("Profiles");
    }

    private void addToErrorsFile(String message) {
        try (FileWriter writer = new FileWriter(errorsFile, true)) {
            writer.write(message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validString(String[] line) {
        return switch (line[0]) {
            case "register", "login", "add-song-to" -> line.length == MIN_SIZE_TREE;
            case "disconnect", "stop" -> line.length == 1;
            case "search" -> line.length >= MIN_SIZE_TWO;
            case "top", "create-playlist", "show-playlist", "play" -> line.length == MIN_SIZE_TWO;
            default -> false;
        };
    }

    @Override
    public String parse(SelectionKey key, String line) {

        String[] commands = line.split(" ");
        if (!validString(commands)) {
            return "Please enter a valid command.";
        }

        if (commands[0].equals("register")) {

            try {
                spotify.register(key, commands[1], commands[SECOND_ARG]);
            } catch (UsersAlreadyExists e) {
                addToErrorsFile(e.toString());
                return e.getMessage();
            }

            return "New account was successfully created.";

        } else if (commands[0].equals("login")) {

            try {
                spotify.login(key, commands[1], commands[SECOND_ARG]);
            } catch (UserNotFound e) {

                addToErrorsFile(e.toString());
                return e.getMessage();

            }
            return "You have successfully signed in.";

        } else if (commands[0].equals("search")) {

            return spotify.search(Arrays.stream(commands).skip(1).toList()).toString();
        } else if (commands[0].equals("top")) {

            return spotify.top(Integer.parseInt(commands[1]));

        } else if (commands[0].equals("create-playlist")) {

            try {
                spotify.createPlaylist(key, commands[1]);
            } catch (PlaylistAlreadyExists e) {
                addToErrorsFile(e.toString());
                return e.getMessage();

            }
            return "You have successfully created a new playlist.";

        } else if (commands[0].equals("add-song-to")) {

            try {
                spotify.addToPlaylist(key, commands[1], commands[SECOND_ARG]);
            } catch (PlaylistNotFound e) {
                addToErrorsFile(e.toString());
                return e.getMessage();
            }

            return "New song added to playlist!";
        } else if (commands[0].equals("show-playlist")) {

            try {
                return spotify.showPlaylist(key, commands[1]);
            } catch (PlaylistNotFound e) {
                addToErrorsFile(e.toString());
                return e.getMessage();
            }

        } else if (line.contains("play")) {
            try {
                spotify.streamSong(key, commands[1]);
            } catch (SongNotFound e) {
                addToErrorsFile(e.toString());
                return e.getMessage();

            }
            return "play";
        } else if (line.contains("stop")) {
            spotify.stopPlaying(key);
            return "Music stopped.";
        }
        spotify.logOut(key);
        return "Logged out.";

    }
}
