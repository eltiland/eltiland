package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.NewsException;
import com.eltiland.model.NewsItem;

import java.util.List;

/**
 * News Manager, containing methods related to {@link NewsItem}.
 */
public interface NewsManager {
    /**
     * @param id id of news item
     * @return item of news specified by passed id.
     */
    NewsItem getNewsItem(Long id);

    /**
     * Creates and persists item of news.
     *
     * @param toCreate item of news to create
     * @return persisted item of news
     * @throws EltilandManagerException if validation failed
     */
    NewsItem createNewsItem(NewsItem toCreate) throws NewsException;

    /**
     * Updates item of news in DB
     *
     * @param toUpdate news item to update
     * @return updated item
     * @throws EltilandManagerException if validation failed
     */
    NewsItem updateNewsItem(NewsItem toUpdate) throws NewsException;

    /**
     * Delete item from DB
     *
     * @param toDelete news item to delete
     * @throws EltilandManagerException if item cannot be deleted
     */
    void deleteNewsItem(NewsItem toDelete) throws NewsException;

    /**
     * Get all news.
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of all news
     */
    List<NewsItem> getNewsList(int index, Integer count, String sProperty, boolean isAscending, String searchString);
    List<NewsItem> getNewsList(int index, Integer count, List<String> sorts, boolean isAscending, String searchString);

    /**
     * @return all news count
     */
    int getNewsListCount(String searchString);
}
