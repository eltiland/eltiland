package com.eltiland.bl.validators;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.NewsException;
import com.eltiland.model.NewsItem;
import org.springframework.stereotype.Component;

/**
 * News item validator.
 */
@Component
public class NewsItemValidator {

    public void validate(NewsItem item) throws NewsException {
        if (item == null) {
            throw new NewsException(NewsException.EMPTY_ENTITY_ERROR);
        }

        if (item.getTitle() == null) {
            throw new NewsException(NewsException.EMPTY_TITLE_ERROR);
        }

        if (item.getTitle().isEmpty()) {
            throw new NewsException(NewsException.EMPTY_TITLE_ERROR);
        }

        if (item.getDate() == null) {
            throw new NewsException(NewsException.EMPTY_DATE_ERROR);
        }

        if (item.getBody() == null) {
            throw new NewsException(NewsException.EMPTY_BODY_ERROR);
        }

        if (item.getBody().isEmpty()) {
            throw new NewsException(NewsException.EMPTY_BODY_ERROR);
        }
    }
}
