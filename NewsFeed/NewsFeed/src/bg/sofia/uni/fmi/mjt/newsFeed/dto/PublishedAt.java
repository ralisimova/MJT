package bg.sofia.uni.fmi.mjt.newsFeed.dto;

import java.util.Objects;

public class PublishedAt {
    private String publishedAt;

    public PublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublishedAt that = (PublishedAt) o;
        return Objects.equals(publishedAt, that.publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publishedAt);
    }
}
