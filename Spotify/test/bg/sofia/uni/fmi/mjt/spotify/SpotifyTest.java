package bg.sofia.uni.fmi.mjt.spotify;


import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistAlreadyExists;
import bg.sofia.uni.fmi.mjt.spotify.exception.PlaylistNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.UserNotFound;
import bg.sofia.uni.fmi.mjt.spotify.exception.UsersAlreadyExists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class SpotifyTest {
    private static final int TOTAL_USER_SIZE = 3;
    private static final int NEGATIVE_NUMBER = -3;
    private static final int TOTAL_SONG_SIZE = 6;
    private static final int TOTAL_PLAYLIST_SIZE = 2;


    @Mock
    private static SelectionKey key1 = mock(SelectionKey.class);
    @Mock
    private static SelectionKey key2 = mock(SelectionKey.class);
    @Mock
    private static SelectionKey key3 = mock(SelectionKey.class);
    private static Spotify spotify;
    private static final String profilesFile = "testProfilesFile";

    @BeforeAll
    static void setUp() {
        spotify = new Spotify(profilesFile);
        try {
            spotify.register(key1, "anna@abv.bg", "12345678");
            spotify.register(key2, "martin@abv.bg", "qwerty");

            spotify.createPlaylist(key1, "playlist1");

        } catch (UsersAlreadyExists | PlaylistAlreadyExists e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testRegisterUserAlreadyExists() {

        assertThrows(UsersAlreadyExists.class,
            () -> spotify.register(key3, "anna@abv.bg", "password"));
    }

    @Test
    void testRegisterUser() {

        try {
            spotify.register(key3, "another_anna@abv.bg", "password");
        } catch (UsersAlreadyExists e) {
            throw new RuntimeException(e);
        }

        assertEquals(TOTAL_USER_SIZE, spotify.getNumberLoggedInUsers());

    }

    @Test
    void testLogInUserNotFound() {

        assertThrows(UserNotFound.class,
            () -> spotify.login(key3, "anna@abv.bg", "password"));
    }

    @Test
    void testLogINUser() {

        try {
            spotify.login(key3, "anna@abv.bg", "12345678");
        } catch (UserNotFound e) {
            throw new RuntimeException(e);
        }
        assertEquals(TOTAL_USER_SIZE, spotify.getNumberLoggedInUsers());

    }

    @Test
    void testSearchNull() {
        assertNull(spotify.search(null));
    }

    @Test
    void testSearch() {
        Set<String> expected = new HashSet<>();
        expected.add("Arctic_Monkeys_Do_I_Wanna_Know");
        expected.add("Arctic_Monkeys_I_Wanna_Be_Yours");
        Set<String> result = spotify.search(Collections.singletonList("Wanna"));
        assertTrue(expected.containsAll(result) &&
            result.containsAll(expected)
            && expected.size() == result.size());
    }

    @Test
    void testTopNegative() {
        assertEquals("Please enter a positive number.",
            spotify.top(NEGATIVE_NUMBER));
    }

    @Test
    void testTop() {
        Set<String> expected = new HashSet<>(List.of(new String[]
            {"Arctic_Monkeys_Do_I_Wanna_Know",
                "Arctic_Monkeys_I_Wanna_Be_Yours", "Maneskin_Beggin",
                "Miley_Cyrus_Flowers", "Måneskin_CORALINE",
                "Måneskin_I_WANNA_BE_YOUR_SLAVE"}));
        String uneditedResult = spotify.top(TOTAL_SONG_SIZE);
        Set<String> result = Set.of(uneditedResult
            .substring(1, uneditedResult.length() - 1)
            .split(" "));
        assertTrue(expected.containsAll(result) &&
            result.containsAll(expected)
            && expected.size() == result.size());
    }

    @Test
    void testCreatePlaylistTwice() {
        assertThrows(PlaylistAlreadyExists.class,
            () -> spotify.createPlaylist(key1, "playlist1"));

    }

    @Test
    void testCreatePlaylist() {
        try {
            spotify.createPlaylist(key1, "playlist2");
            assertEquals(TOTAL_PLAYLIST_SIZE, spotify.getPlaylists()
                .get(spotify.getUserByKey(key1))
                .size());

        } catch (PlaylistAlreadyExists e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddSongInvalidPlaylist() {

        assertThrows(PlaylistNotFound.class,
            () -> spotify.addToPlaylist(key1, "impossible", "song1"));
    }

    @Test
    void testAddSong() {
        try {
            spotify.addToPlaylist(key1, "playlist1", "song1");
            String result = "playlist1" +
                System.lineSeparator() +
                "song1" +
                System.lineSeparator();
            assertEquals(result, spotify.showPlaylist(key1, "playlist1"));

        } catch (PlaylistNotFound e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShowInvalidPlaylist() {

        assertThrows(PlaylistNotFound.class, () -> spotify.showPlaylist(key1, "impossible"));
    }
}

