package bg.sofia.uni.fmi.mjt.spotify.exception;

public class UserNotFound extends SpotifyException{
    public UserNotFound() {
    }

    public UserNotFound(String message) {
        super(message);
    }
}
