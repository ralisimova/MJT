package bg.sofia.uni.fmi.mjt.spotify.exception;

public class UsersAlreadyExists extends SpotifyException{
    public UsersAlreadyExists() {
    }

    public UsersAlreadyExists(String message) {
        super(message);
    }
}
