package bg.sofia.uni.fmi.mjt.newsFeed.dto;

import java.util.Objects;

public class UrlToImage {
    private String urlToImage;

    public UrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlToImage that = (UrlToImage) o;
        return Objects.equals(urlToImage, that.urlToImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(urlToImage);
    }
}
