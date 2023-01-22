import bg.sofia.uni.fmi.mjt.newsFeed.Criteria;
import bg.sofia.uni.fmi.mjt.newsFeed.NewsFeedClient;
import bg.sofia.uni.fmi.mjt.newsFeed.dto.Article;
import bg.sofia.uni.fmi.mjt.newsFeed.exceptions.NewsFeedException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws NewsFeedException, IOException, InterruptedException {
        HttpClient httpClient= HttpClient.newBuilder().build();
        List<Article> client=new NewsFeedClient(httpClient)
            .getArticles(Criteria.builder(Set.of("trump", "donald"))
                //   .setCategory("business")
                //      .setCountry("us")
                .build());
        for(Article a:client){
            System.out.println(a.getTitle());
        }

  //      System.out.println(uri);

       // HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

       // httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
