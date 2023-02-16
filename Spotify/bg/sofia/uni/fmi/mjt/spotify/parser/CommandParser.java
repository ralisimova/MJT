package bg.sofia.uni.fmi.mjt.spotify.parser;

import bg.sofia.uni.fmi.mjt.spotify.Spotify;
import bg.sofia.uni.fmi.mjt.spotify.exception.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Arrays;

public class CommandParser implements Parser {
    String errorsFile="ErrorsFile.txt";
    public CommandParser() {

    }

    private static Spotify spotify;

    static {
        spotify = new Spotify("Profiles");
    }
private void addToErrorsFile(String message){
        try(FileWriter writer=new FileWriter(errorsFile,true)) {
            writer.write(message+System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
}

    private boolean validString(String[] line) {
        return switch (line[0]) {
            case "register", "login", "add-song-to" -> line.length == 3;
            case "disconnect", "stop" -> line.length == 1;
            case "search" -> line.length >= 2;
            case "top", "create-playlist", "show-playlist", "play" -> line.length == 2;
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
                spotify.register(key, commands[1], commands[2]);
            } catch (UsersAlreadyExists e) {
                addToErrorsFile(e.toString());
                return e.getMessage();
                //  throw new RuntimeException(e);
            }

            return "New account was successfully created.";

        } else if (commands[0].equals("login")) {

            try {
                spotify.login(key, commands[1], commands[2]);
            } catch (UserNotFound e) {

                addToErrorsFile(e.toString());
                return e.getMessage();

                // throw new RuntimeException(e);
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

                // throw new RuntimeException(e);
            }
            return "You have successfully created a new playlist.";

        } else if (commands[0].equals("add-song-to")) {

            try {
                spotify.addToPlaylist(key, commands[1], commands[2]);
            } catch (PlaylistNotFound e) {
                addToErrorsFile(e.toString());
                return e.getMessage();

                //  throw new RuntimeException(e);
            }

            return "New song added to playlist!";
        } else if (commands[0].equals("show-playlist")) {

            try {
                return spotify.showPlaylist(key, commands[1]);
            } catch (PlaylistNotFound e) {
                addToErrorsFile(e.toString());
                return e.getMessage();

                // throw new RuntimeException(e);
            }

        } else if (line.contains("play")) {
            try {
                spotify.streamSong(key, commands[1]);
            } catch (SongNotFound e) {
                addToErrorsFile(e.toString());
                return e.getMessage();

                // throw new RuntimeException(e);
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
