package bg.sofia.uni.fmi.mjt.newsFeed.dto;

import java.util.Objects;

public class Author {
    private String author;

    public Author(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author1 = (Author) o;
        return Objects.equals(author, author1.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author);
    }
}
