package bg.sofia.uni.fmi.mjt.spotify;


import bg.sofia.uni.fmi.mjt.spotify.parser.CommandParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.channels.SelectionKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class CommandParserTest {

    @Mock
    private static SelectionKey key1 = mock(SelectionKey.class);
    private static CommandParser parser;

    @BeforeAll
    static void setUp() {
        parser = new CommandParser();

        parser.parse(key1, "register account@abv.bg 1234");
        parser.parse(key1, "create-playlist p1");

    }

    @Test
    void testInvalidRegister() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "register"));
    }

    @Test
    void testInvalidLogin() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "login"));
    }

    @Test
    void testInvalidAddSong() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "add-song-to"));
    }

    @Test
    void testInvalidDisconnect() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "disconnect ok"));
    }

    @Test
    void testInvalidStop() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "stop ok"));
    }

    @Test
    void testInvalidSearch() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "search"));
    }

    @Test
    void testInvalidTop() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "top"));
    }

    @Test
    void testInvalidCreatePlaylist() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "create-playlist"));
    }

    @Test
    void testInvalidShowPlaylist() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "show-playlist"));
    }

    @Test
    void testInvalid() {

        assertEquals("Please enter a valid command.",
            parser.parse(key1, "other command"));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        assertEquals("User with this email already exists.",
            parser.parse(key1, "register account@abv.bg 1234"));
    }

    @Test
    void testRegisterUser() {
        assertEquals("New account was successfully created.",
            parser.parse(key1, "register anna@abv.bg 1234"));
    }

    @Test
    void testLogInUserNotFound() {
        assertEquals("Invalid email or password. Please try again.",
            parser.parse(key1, "login impossible@abv.bg 1234"));

    }

    @Test
    void testLogInUser() {
        assertEquals("You have successfully signed in.",
            parser.parse(key1, "login account@abv.bg 1234"));

    }


    @Test
    void testSearch() {
        assertEquals(
            "[Arctic_Monkeys_Do_I_Wanna_Know, Arctic_Monkeys_I_Wanna_Be_Yours]"
            , parser.parse(key1, "search Wanna"));
    }


    @Test
    void testTop() {
        Set<String> expected = new HashSet<>(List.of(new String[]
            {"Arctic_Monkeys_Do_I_Wanna_Know",
                "Arctic_Monkeys_I_Wanna_Be_Yours", "Maneskin_Beggin",
                "Miley_Cyrus_Flowers", "Måneskin_CORALINE",
                "Måneskin_I_WANNA_BE_YOUR_SLAVE"}));

        String uneditedResult = parser.parse(key1, "top 6");
        Set<String> result = Set.of(uneditedResult
            .substring(1, uneditedResult.length() - 1)
            .split(" "));
        assertTrue(expected.containsAll(result) &&
            result.containsAll(expected)
            && expected.size() == result.size());
    }

    @Test
    void testCreatePlaylist() {
        assertEquals("You have successfully created a new playlist.",
            parser.parse(key1, "create-playlist p"));
    }
    @Test
    void testCreatePlaylistAlreadyExists() {
        parser.parse(key1, "create-playlist p2");
        assertEquals("You already have a playlist with this name.",
            parser.parse(key1, "create-playlist p2"));
    }
    @Test
    void testAddSongPlaylistNotFound() {
        assertEquals("You don't have a playlist with this name yet.",
            parser.parse(key1, "add-song-to playlist song"));
    }

    @Test
    void testShowPlaylistNotFound() {
        assertEquals("You don't have a playlist with this name yet.",
            parser.parse(key1, "show-playlist playlist "));
    }

    @Test
    void testLogOut() {
        assertEquals("Logged out.",
            parser.parse(key1, "disconnect"));
    }

    @Test
    void testStop() {
        assertEquals("Music stopped.",
            parser.parse(key1, "stop"));
    }

    @Test
    void testPlayInvalidSong() {
        assertEquals("We can't find the song you are looking for.",
            parser.parse(key1, "play song"));
    }

    @Test
    void testPlay() {
        assertEquals("play",
            parser.parse(key1, "play Arctic_Monkeys_Do_I_Wanna_Know"));
    }
}

