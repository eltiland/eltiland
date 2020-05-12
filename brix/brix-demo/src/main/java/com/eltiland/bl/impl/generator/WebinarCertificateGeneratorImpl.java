package com.eltiland.bl.impl.generator;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.VelocityMergeTool;
import com.eltiland.bl.pdf.PdfGenerator;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.VelocityCommonException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Webinar certificate generator implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarCertificateGeneratorImpl implements WebinarCertificateGenerator {
    @Autowired
    private VelocityMergeTool velocityMergeTool;
    @Autowired
    private PdfGenerator pdfGenerator;
    @Autowired
    private GenericManager genericManager;

    private static final String TEMPLATE = "templates/pdf/certificate.fo";

    private final static String USER_NAME = "userName";
    private final static String WEBINAR_NAME = "webinarName";
    private final static String WEBINAR_DURATION = "webinarDuration";
    private final static String WEBINAR_DATE = "webinarDate";

    @Override
    public InputStream generateCertificate(WebinarUserPayment user) throws EltilandManagerException {
        genericManager.initialize(user, user.getWebinar());

        Map<String, Object> map = new HashMap<>();

        String fullname = user.getUserSurname() + " " + user.getUserName();
        if (user.getPatronymic() != null) {
            fullname += (" " + user.getPatronymic());
        }
        map.put(USER_NAME, fullname);
        map.put(WEBINAR_NAME, user.getWebinar().getShortDesc());
        map.put(WEBINAR_DURATION, getDuration(user.getWebinar()));
        map.put(WEBINAR_DATE, getStartDate(user.getWebinar().getStartDate()) + " г.");

        try {
            String content = velocityMergeTool.mergeTemplate(map, TEMPLATE);
            return pdfGenerator.generatePDF(new ByteArrayInputStream(content.getBytes("UTF-8")));
        } catch (VelocityCommonException | UnsupportedEncodingException e) {
            throw new EltilandManagerException("Certificate generation error", e);
        }
    }

    @Override
    public InputStream generateRecordCertificate(WebinarRecordPayment user) throws EltilandManagerException {
        genericManager.initialize(user, user.getRecord());
        genericManager.initialize(user, user.getUserProfile());
        genericManager.initialize(user.getRecord(), user.getRecord().getWebinar());

        Map<String, Object> map = new HashMap<>();
        map.put(USER_NAME, user.getUserProfile().getName());
        map.put(WEBINAR_NAME, user.getRecord().getWebinar().getShortDesc());
        map.put(WEBINAR_DURATION, getDuration(user.getRecord().getWebinar()));
        map.put(WEBINAR_DATE, getStartDate(user.getDate()) + " г.");

        try {
            String content = velocityMergeTool.mergeTemplate(map, TEMPLATE);
            return pdfGenerator.generatePDF(new ByteArrayInputStream(content.getBytes("UTF-8")));
        } catch (VelocityCommonException | UnsupportedEncodingException e) {
            throw new EltilandManagerException("Certificate generation error", e);
        }
    }

    private String getStartDate(Date webinarDate) {
        String date = DateUtils.formatRussianDate(webinarDate);
        date = date.replace("Январь", "января");
        date = date.replace("Февраль", "февраля");
        date = date.replace("Март", "марта");
        date = date.replace("Апрель", "апреля");
        date = date.replace("Май", "мая");
        date = date.replace("Июнь", "июня");
        date = date.replace("Июль", "июля");
        date = date.replace("Август", "августа");
        date = date.replace("Сентябрь", "сентября");
        date = date.replace("Октябрь", "октября");
        date = date.replace("Ноябрь", "ноября");
        return date.replace("Декабрь", "декабря");
    }

    private String getDuration(Webinar webinar) {
        float hours = (float) webinar.getDuration() / 45;

        //        duration += " ";
//        if (hours == 1) {
//            duration += "академический час";
//        } else if (hours < 5) {
//            duration += "академических часа";
//        } else {
//            duration += "академических часов";
//        }
        return (((int) hours) == hours) ? String.valueOf((int) hours) : String.valueOf(hours);
    }
}
