package bg.sofia.uni.fmi.mjt.newsFeed;

import bg.sofia.uni.fmi.mjt.newsFeed.dto.Article;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Response {
    private String status;
    private int totalResults;
    Article[] articles;
    //optional
    private String code;
    private String message;

    public static ResponseBuilder builder(String status, int results, Article[] articles) {
        return new ResponseBuilder(status,results,articles);
    }

    private Response(ResponseBuilder builder) {
        this.status = builder.status;
        this.totalResults = builder.totalResults;
        this.articles = builder.articles;
        this.code = builder.code;
        this.message = builder.message;

    }

    // Builder Class
    public static class ResponseBuilder {

        private String status;
        private int totalResults;
        Article[] articles;
        //optional
        private String code;
        private String message;

        public ResponseBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public ResponseBuilder setMessage(String message) {
            this.message = message;
            return this;

        }

        private ResponseBuilder(String status, int results, Article[] articles) {
            this.status = status;
            this.totalResults = results;
            this.articles = articles;
        }
        public Response build() {
            return new Response(this);
        }

    }

/*    public Response(String status, int results, Article[] articles) {
        this.status = status;
        this.totalResults = results;
        this.articles = articles;
    }*/
public String getMessage() {
    return message;
}


    public String getStatus() {
        return status;
    }

    public int getResults() {
        return totalResults;
    }

    public Article getArticleAt(int index) {
        if (index < totalResults) {
            return articles[index];
        }
        return null;
    }


    public List<Article> getArticles() {
        return List.of(articles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return totalResults == response.totalResults && Objects.equals(status, response.status) &&
            Objects.equals(articles, response.articles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, totalResults, articles);
    }
}
