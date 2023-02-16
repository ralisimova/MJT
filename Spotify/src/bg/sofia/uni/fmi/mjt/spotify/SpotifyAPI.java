package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.exception.*;

import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.Set;

public interface SpotifyAPI {

    void register(SelectionKey key, String email, String password) throws UsersAlreadyExists;

    void login(SelectionKey key, String email, String password) throws UserNotFound;


    Set<String> search(List<String> song);

    String top(int number);

    void createPlaylist(SelectionKey key, String name) throws PlaylistAlreadyExists;

    void addToPlaylist(SelectionKey key, String playlist, String song) throws PlaylistNotFound;

    String showPlaylist(SelectionKey key, String name) throws PlaylistNotFound;

    void streamSong(SelectionKey key, String song) throws SongNotFound;

}
