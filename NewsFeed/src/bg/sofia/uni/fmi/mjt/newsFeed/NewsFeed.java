package bg.sofia.uni.fmi.mjt.newsFeed;

import bg.sofia.uni.fmi.mjt.newsFeed.dto.Article;
import bg.sofia.uni.fmi.mjt.newsFeed.exceptions.NewsFeedException;

import java.util.List;

public interface NewsFeed {

    /**
     * By the given criteria sends a request to the newsFeed API and returns the response
     *
     * @param criteria the criteria to search by- it contains at least one keyword, and may contain
     *                 country or category
     * @return the response
     * @throws NewsFeedException
     */
    Response getResponse(Criteria criteria) throws NewsFeedException;

    /**
     * By the given criteria sends a request to the newsFeed API and returns the first
     * page of responses with default size 5
     *
     * @param criteria the criteria to search by- it contains at least one keyword, and may contain
     *                 country or category
     * @return list<Article>
     * @throws NewsFeedException
     */
    List<Article> getArticles(Criteria criteria) throws NewsFeedException;

    /**
     * By the given criteria sends a request to the newsFeed API and returns the pageNumber
     * page of responses with default size 5
     *
     * @param criteria   the criteria to search by- it contains at least one keyword, and may contain
     *                   country or category
     * @param pageNumber number of desired page of results starting from 0
     * @return list<Article>
     * @throws NewsFeedException
     */
    List<Article> getArticles(Criteria criteria, int pageNumber) throws NewsFeedException;

    /**
     * By the given criteria sends a request to the newsFeed API and returns the pageNumber
     * page of responses with page size of pageSize
     *
     * @param criteria   the criteria to search by- it contains at least one keyword, and may contain
     *                   country or category
     * @param pageNumber number of desired page of results starting from 0
     * @param pageSize   number of articles on a page
     * @return list<Article>
     * @throws NewsFeedException
     */
    List<Article> getArticles(Criteria criteria, int pageNumber, int pageSize) throws NewsFeedException;
}
