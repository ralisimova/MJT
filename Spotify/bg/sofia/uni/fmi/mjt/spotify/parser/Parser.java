package bg.sofia.uni.fmi.mjt.spotify.parser;

import java.nio.channels.SelectionKey;

public interface Parser {
     String parse(SelectionKey key,String command);
}
