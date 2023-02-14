package bg.sofia.uni.fmi.mjt.spotify;

import java.nio.channels.SelectionKey;

public interface Parser {
     String parse(SelectionKey key,String command);
}
