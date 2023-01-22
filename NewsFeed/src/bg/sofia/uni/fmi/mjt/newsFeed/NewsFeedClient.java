package bg.sofia.uni.fmi.mjt.newsFeed;

import bg.sofia.uni.fmi.mjt.newsFeed.dto.Article;
import bg.sofia.uni.fmi.mjt.newsFeed.exceptions.NewsFeedException;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NewsFeedClient implements NewsFeed {

    private static final String API_KEY = "fc0fefe94ddc4f2ab2529698df6d1b7a";
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "newsapi.org";
    private static final String API_ENDPOINT_PATH = "/v2/top-headlines";
    private static final Gson GSON = new Gson();
    private final HttpClient newsFeedHttpClient;
    private final String apiKey;

    public NewsFeedClient(HttpClient newsFeedHttpClient) {
        this(newsFeedHttpClient, API_KEY);
    }

    public NewsFeedClient(HttpClient newsFeedHttpClient, String apiKey) {
        this.newsFeedHttpClient = newsFeedHttpClient;
        this.apiKey = apiKey;
    }


    public Response getResponse(Criteria criteria) throws NewsFeedException {
        HttpResponse<String> response;
        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH,
                criteria.criteriaToRequest() + "&apiKey=" + apiKey
                /* API_ENDPOINT_QUERY.formatted(criteria, apiKey)*/, null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

            response = newsFeedHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new NewsFeedException("Could not retrieve news feed", e);
        }
/*        String json = new String();

        Gson gson = new Gson();
        Response dev = gson.fromJson(json, Response.class);
return dev;*/
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            //if(GSON.fromJson(response.body(), Response.class).getStatus()==)
            return GSON.fromJson(response.body(), Response.class);
        }

        //if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
        else {
            throw new NewsFeedException(GSON.fromJson(response.body(), Response.class).getMessage());


            //  throw new NewsFeedException("Unexpected response code from weather forecast service");
        }
    }

    public List<Article> getArticles(Criteria criteria) throws NewsFeedException {
        Response response = getResponse(criteria);
        return response
            .getArticles().stream().limit(5).toList();
    }


    public List<Article> getArticles(Criteria criteria, int pageNumber) throws NewsFeedException {
        Response response = getResponse(criteria);
        return response
            .getArticles().stream().skip(pageNumber * 5L)
            .limit(5).toList();
    }

    public List<Article> getArticles(Criteria criteria, int pageNumber, int pageSize) throws NewsFeedException {
        Response response = getResponse(criteria);
        return response
            .getArticles().stream().skip(pageNumber * pageSize)
            .limit(pageSize).toList();
    }
}
