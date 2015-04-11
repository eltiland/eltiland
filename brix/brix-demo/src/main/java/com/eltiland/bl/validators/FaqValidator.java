package com.eltiland.bl.validators;

import com.eltiland.exceptions.FaqException;
import com.eltiland.model.faq.Faq;
import com.eltiland.model.faq.FaqApproval;
import com.eltiland.model.faq.FaqCategory;
import org.springframework.stereotype.Component;

/**
 * Validator for FAQ entity
 *
 * @author Pavel Androschuk
 */
@Component
public class FaqValidator {
    public void validateFaq(Faq faq) throws FaqException {
        if (faq == null) {
            throw new FaqException(FaqException.FAQ_NULL_ENTITY_ERROR);
        }

        if (faq.getQuestion() == null) {
            throw new FaqException(FaqException.FAQ_QUESTION_EMPTY);
        }

        if (faq.getQuestion().isEmpty()) {
            throw new FaqException(FaqException.FAQ_QUESTION_EMPTY);
        }

        if (faq.getAnswer() == null) {
            throw new FaqException(FaqException.FAQ_ANSWER_EMPTY);
        }

        if (faq.getAnswer().isEmpty()) {
            throw new FaqException(FaqException.FAQ_ANSWER_EMPTY);
        }

        if (faq.getQuestion().length() > 2048) {
            throw new FaqException(FaqException.FAQ_QUESTION_TOO_LONG);
        }

        if (faq.getAnswer().length() > 2048) {
            throw new FaqException(FaqException.FAQ_ANSWER_TOO_LONG);
        }
    }

    public void validateFaqApproval(FaqApproval faqApproval) throws FaqException {
        if (faqApproval == null) {
            throw new FaqException(FaqException.FAQ_APPROVAL_NULL_ENTITY_ERROR);
        }

        if (faqApproval.getUserEMail() == null) {
            throw new FaqException(FaqException.FAQ_APPROVAL_EMAIL_EMPTY);
        }

        if (faqApproval.getUserEMail().isEmpty()) {
            throw new FaqException(FaqException.FAQ_APPROVAL_EMAIL_EMPTY);
        }

        if (faqApproval.getQuestion() == null) {
            throw new FaqException(FaqException.FAQ_APPROVAL_QUESTION_EMPTY);
        }

        if (faqApproval.getQuestion().isEmpty()) {
            throw new FaqException(FaqException.FAQ_APPROVAL_QUESTION_EMPTY);
        }

        if (faqApproval.getQuestion().length() > 2048) {
            throw new FaqException(FaqException.FAQ_APPROVAL_QUESTION_TOO_LONG);
        }

        if (faqApproval.getAnswer() != null) {
            if (faqApproval.getAnswer().length() > 2048) {
                throw new FaqException(FaqException.FAQ_APPROVAL_ANSWER_TOO_LONG);
            }

            if (faqApproval.isAnswered() && faqApproval.getAnswer().isEmpty()) {
                throw new FaqException(FaqException.FAQ_APPROVAL_ANSWER_EMPTY);
            }
        }
    }

    public void validateCategory(FaqCategory faqCategory) throws FaqException {
        if (faqCategory == null) {
            throw new FaqException(FaqException.CATEGORY_NULL_ENTITY_ERROR);
        }

        if (faqCategory.getName() == null) {
            throw new FaqException(FaqException.CATEGORY_NAME_EMPTY);
        }

        if (faqCategory.getName().isEmpty()) {
            throw new FaqException(FaqException.CATEGORY_NAME_EMPTY);
        }

        if (faqCategory.getName().length() > 80) {
            throw new FaqException(FaqException.CATEGORY_NAME_TOO_LONG);
        }
    }
}
