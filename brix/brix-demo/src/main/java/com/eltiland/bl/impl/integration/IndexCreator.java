package com.eltiland.bl.impl.integration;

import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.annotations.Indexed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author knorr
 * @version 1.0
 * @since 9/27/12
 */
public class IndexCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexCreator.class);

    @Autowired
    private SessionFactory sessionFactory;

    private String packageToScan;

    /**
     * <p/>
     * Invoked initially to search for index.
     * If not created, will throw exceptions.
     * <p/>
     * It works in background so should not be a huge issue.
     */
    public void doRebuildIndex() {
        try {
            List<Class> classesToIndex = new ArrayList<>();
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(Indexed.class, true, true));
            for (BeanDefinition definition : scanner.findCandidateComponents(packageToScan)) {
                LOGGER.info("Add {} to indexing list", definition.getBeanClassName());
                classesToIndex.add(Class.forName(definition.getBeanClassName()));
            }
            FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.openSession());
            LOGGER.info("About to perform total reindex.");
            for (Class c : classesToIndex) {
                LOGGER.info("Going to reindex class {}", c.getName());
                fullTextSession
                        .createIndexer(c)
                        .threadsForSubsequentFetching(1)
                        .threadsToLoadObjects(1)
                        .startAndWait();

            }
            LOGGER.info("Total reindex completed successfully.");
        } catch (InterruptedException ex) {
            LOGGER.error("Index creation interrupted. Aborting.", ex);
            throw new IllegalStateException("Can not create index", ex);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Some class definition that must be indexed was not found", e);
            throw new IllegalStateException("Some classes was not found", e);
        }
    }

    public void setPackageToScan(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    /**
     * Rebuild indexes for specified entity.
     *
     * @param clazz class for index rebuilding.
     */
    public void doRebuildIndex(Class clazz) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.openSession());
        LOGGER.info("Going to reindex class {}", clazz.getName());
        try {
            fullTextSession.createIndexer(clazz)
                    .threadsForSubsequentFetching(1)
                    .threadsToLoadObjects(1)
                    .startAndWait();
        } catch (InterruptedException e) {
            LOGGER.error("Index creation interrupted. Aborting.", e);
            throw new IllegalStateException("Can not create index", e);
        }
    }
}