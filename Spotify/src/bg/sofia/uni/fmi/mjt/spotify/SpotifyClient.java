package bg.sofia.uni.fmi.mjt.spotify;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SpotifyClient {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 512;
    private static boolean loggedIn = false;
    private static final String serverErrorsFile = "ServerErrorsFile";
    private static final String INITIAL_PROMPT = "Please select a command.Options are:\n" +
        "register <email> <password>" + System.lineSeparator() +
        "login <email> <password>" + System.lineSeparator() +
        "disconnect" + System.lineSeparator() +
        "search <words> " + System.lineSeparator() +
        "top <number> " + System.lineSeparator() +
        "create-playlist <name_of_the_playlist>" + System.lineSeparator() +
        "add-song-to <name_of_the_playlist> <song>" + System.lineSeparator() +
        "show-playlist <name_of_the_playlist>" + System.lineSeparator() +
        "play <song>" + System.lineSeparator() +
        "stop";

    private static void addToErrorsFile(String message) {
        try (FileWriter writer = new FileWriter(serverErrorsFile, true)) {
            writer.write(message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println(INITIAL_PROMPT);

            while (true) {
                System.out.print("Enter command: ");
                String message = scanner.nextLine();
                if (!loggedIn && !message.contains("login") && !message.contains("register")) {
                    System.out.println("Please log in first!");
                } else if (loggedIn && (message.contains("login")
                    || message.contains("register"))) {
                    System.out.println("You are already logged in.");
                } else {

                    buffer.clear();
                    buffer.put(message.getBytes());
                    buffer.flip();
                    socketChannel.write(buffer);

                    buffer.clear();
                    socketChannel.read(buffer);
                    buffer.flip();

                    byte[] byteArray = new byte[buffer.remaining()];
                    buffer.get(byteArray);
                    String reply = new String(byteArray, StandardCharsets.UTF_8);
                    if (reply.equals("New account was successfully created.")
                        || reply.equals("You have successfully signed in.")) {
                        loggedIn = true;
                    }
                    if (reply.equals("Disconnected")) {
                        loggedIn = false;
                        break;
                    }

                    System.out.println(reply);
                }
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the network communication");
            addToErrorsFile(e.toString());
        }
    }
}