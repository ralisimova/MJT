package bg.sofia.uni.fmi.mjt.spotify;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class SpotifyClient {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 512;
    private static boolean loggedIn = false;
    private static String serverErrorsFile = "ServerErrorsFile";
    private static String INITIAL_PROMPT="Please select a command.Options are:\n" +
        "register <email> <password>\n" +
        "login <email> <password>\n" +
        "disconnect\n" +
        "search <words> \n" +
        "top <number> \n" +
        "create-playlist <name_of_the_playlist>\n" +
        "add-song-to <name_of_the_playlist> <song>\n" +
        "show-playlist <name_of_the_playlist>\n" +
        "play <song>\n" +
        "stop";

    private static void addToErrorsFile(String message) {
        try (FileWriter writer = new FileWriter(serverErrorsFile, true)) {
            writer.write(message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

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
                }
               else if (loggedIn && (message.contains("login") || message.contains("register"))) {
                    System.out.println("You are already logged in.");
                }
                else {
                   /* if ("quit".equals(message)) {
                        break;
                    }*/


                    buffer.clear();
                    buffer.put(message.getBytes());
                    buffer.flip();
                    socketChannel.write(buffer);

                    buffer.clear();
                    socketChannel.read(buffer);
                    buffer.flip();

                    byte[] byteArray = new byte[buffer.remaining()];
                    buffer.get(byteArray);
                    String reply = new String(byteArray, "UTF-8");
                    if (reply.equals("New account was successfully created.") ||
                        reply.equals("You have successfully signed in.")) {
                        loggedIn = true;
                    }
                    if (reply.equals("Disconnected")) {
                        loggedIn = false;
                        break;
                    }

                    System.out.println(  reply );
                }
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the network communication");
            addToErrorsFile(e.toString());

            //     throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}