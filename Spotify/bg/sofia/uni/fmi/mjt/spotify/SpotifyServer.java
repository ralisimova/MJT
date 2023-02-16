package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.parser.CommandParser;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SpotifyServer {
    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 1024;
private static String serverErrorsFile="ServerErrorsFile";
    //   private static final Spotify spotify;

    /*  static {
          spotify=new Spotify(new HashSet<>(),"Profiles");
      }*/
    private static CommandParser parser;
    private static void addToErrorsFile(String message){
        try(FileWriter writer=new FileWriter(serverErrorsFile,true)) {
            writer.write(message+System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static {
        parser = new CommandParser();
    }

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    // select() is blocking but may still return with 0, check javadoc
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();

                        buffer.clear();
                        int r = sc.read(buffer);
                        if (r < 0) {
                            System.out.println("Client has closed the connection");
                            sc.close();
                            continue;
                        }
                        buffer.flip();

                        // String s=buffer.toString()

                        byte[] clientInputBytes = new byte[buffer.remaining()];
                        buffer.get(clientInputBytes);

                        //return new String(clientInputBytes);
                        String answer = parser.parse(key, new String(clientInputBytes));
                      /* ByteBuffer buffer1 = ByteBuffer.allocate(BUFFER_SIZE);

                       buffer1= ByteBuffer.wrap(answer.getBytes());*/
                        buffer.clear();

                        buffer.put(answer.getBytes());
                        buffer.flip();

                        sc.write(buffer);

                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }

                    keyIterator.remove();
                }

            }

        } catch (IOException e) {
            System.out.println("There is a problem with the server socket");
            addToErrorsFile(e.toString());
            return ;
           // throw new RuntimeException("There is a problem with the server socket", e);
        }
    }
}