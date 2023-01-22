package bg.sofia.uni.fmi.mjt.newsFeed;

import bg.sofia.uni.fmi.mjt.newsFeed.dto.Article;
import bg.sofia.uni.fmi.mjt.newsFeed.dto.Source;
import bg.sofia.uni.fmi.mjt.newsFeed.exceptions.NewsFeedException;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class NewsFeedTest {
    private static NewsFeed newsFeed;
    private static String titleArticle;
    private static String errorGson;
    private static String validGson;
    private static Criteria criteria;
    private static Article article1;
    private static Article article2;

    //  private static String validGson;


    @Mock
    private HttpClient newsFeedHttpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private NewsFeedClient client;

    @BeforeAll
    public static void setUpGson() {
        Response response = Response.builder("error", 0, null)
            .setCode("apiKeyMissing ")
            .setMessage("Your API key is missing from the request." +
                " Append it to the request with one of these methods.")
            .build();
        errorGson = new Gson().toJson(response, Response.class);

        Response responseValid = Response.builder("ok", 1,
            new Article[] {article1, article2}).build();
        validGson = new Gson().toJson(responseValid, Response.class);
    }

    @BeforeAll
    public static void setUpCriteria() {
        article1 = new Article(new Source("id", "name"), "author", "title",
            "description", "url", "urlToImage",
            "1.1.2023", "Empty article");
        article2 = new Article(new Source("id2", "name2"), "author2", "title2",
            "description2", "url2", "urlToImage2",
            "1.1.2023", "Empty article");

        criteria = Criteria.builder(Set.of("article"))
            .setCountry("us")
            .setCategory("business")
            .build();
    }

  /*  @BeforeAll
    public void setUp() throws IOException, InterruptedException {
      *//*  when(weatherHttpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<BodyHandler<String>>any()))
            .thenReturn(httpWeatherResponseMock);*//*
        when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(httpWeatherResponseMock);
        //client = new WeatherForecastClient(weatherHttpClientMock);
    }*/

    /*  @Test
      void testNoApiKey() {
          try {
              when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class),
                  ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                  .thenReturn(httpResponseMock);

              when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
              when(httpResponseMock.body()).thenReturn(errorGson);
              client = new NewsFeedClient(newsFeedHttpClientMock, "");
              // NewsFeed client = new NewsFeedClient(newsFeedHttpClientMock,null);
              assertThrows(NewsFeedException.class, () -> client.getResponse(criteria));
          } catch (IOException e) {
              throw new RuntimeException(e);
          } catch (InterruptedException e) {
              throw new RuntimeException(e);


          }
      }*/
    @Test
    void testGetArticle() {
        try {
            when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

            when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpResponseMock.body()).thenReturn(validGson);
            client = new NewsFeedClient(newsFeedHttpClientMock);

            List<Article> expected = List.of(article1, article2);
            List<Article> result = client.getArticles(criteria);
            assertTrue(expected.containsAll(result)
                && result.containsAll(expected)
                && expected.size() == result.size());

        } catch (IOException | InterruptedException | NewsFeedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetArticlePageNumber() {
        try {
            when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

            when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpResponseMock.body()).thenReturn(validGson);
            client = new NewsFeedClient(newsFeedHttpClientMock);

          /*  List<Article> expected = List.of(article1, article2);
            List<Article> result = client.getArticles(criteria);*/
            assertNull(client.getArticles(criteria, 1));

        } catch (IOException | InterruptedException | NewsFeedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetArticlePageNumberPageSize() {
        try {
            when(newsFeedHttpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);

            when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(httpResponseMock.body()).thenReturn(validGson);
            client = new NewsFeedClient(newsFeedHttpClientMock);

            List<Article> expected = List.of(article2);
            List<Article> result = client.getArticles(criteria);
            assertTrue(expected.containsAll(result)
                && result.containsAll(expected)
                && expected.size() == result.size());
        } catch (IOException | InterruptedException | NewsFeedException e) {
            throw new RuntimeException(e);
        }
    }
}

