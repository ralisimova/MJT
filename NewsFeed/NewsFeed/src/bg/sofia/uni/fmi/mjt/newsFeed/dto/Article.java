package bg.sofia.uni.fmi.mjt.newsFeed.dto;

//import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Article {
    private Source source;

    private String author;
    private String title;
    private String description;
    private String url;

    private String urlToImage;
    private String publishedAt;
    private String content;

    public Article(Source source, String author, String title, String description, String url, String urlToImage,
                   String publishedAt, String content) {
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(source, article.source) && Objects.equals(author, article.author) &&
            Objects.equals(title, article.title) && Objects.equals(description, article.description) &&
            Objects.equals(url, article.url) && Objects.equals(urlToImage, article.urlToImage) &&
            Objects.equals(publishedAt, article.publishedAt) &&
            Objects.equals(content, article.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, author, title, description, url, urlToImage, publishedAt, content);
    }

    public String getTitle() {
        return title;
    }


}
