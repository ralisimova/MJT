package bg.sofia.uni.fmi.mjt.spotify.exception;

public class PlaylistAlreadyExists extends SpotifyException{
    public PlaylistAlreadyExists() {
    }

    public PlaylistAlreadyExists(String message) {
        super(message);
    }
}
