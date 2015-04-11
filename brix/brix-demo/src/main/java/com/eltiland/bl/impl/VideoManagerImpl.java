package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.VideoManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Video;
import com.eltiland.utils.DateUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manager for Video entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class VideoManagerImpl extends ManagerImpl implements VideoManager {

    @Autowired
    private GenericManager genericManager;

    private static final String YOUTUBE_API_LINK = "http://gdata.youtube.com/feeds/api/videos/";
    private static final String YOUTUBE_API_PARAMS = "?v=2&alt=rss";

    @Override
    @Transactional
    public Video createVideo(Video video) throws EltilandManagerException {
        try {
            video.setCreationDate(DateUtils.getCurrentDate());
            genericManager.saveNew(video);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when creating video", e);
        }
        return video;
    }

    @Override
    @Transactional
    public Video updateVideo(Video video) throws EltilandManagerException {
        try {
            genericManager.update(video);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when updating video", e);
        }
        return video;
    }

    @Override
    @Transactional
    public void deleteVideo(Video video) throws EltilandManagerException {
        genericManager.delete(video);
    }

    @Override
    public void fillAdditionalInfo(Video video) throws EltilandManagerException {
        String url = YOUTUBE_API_LINK + video.getLink() + YOUTUBE_API_PARAMS;
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);

        Pattern second_pattern = Pattern.compile("<yt:duration seconds=[\\s\\S]*?/>");
        Pattern quotes_pattern = Pattern.compile("'[\\s\\S]*?'");

        Pattern upload_pattern = Pattern.compile("<yt:uploaded>[\\s\\S]*?</");
        Pattern brackets_pattern = Pattern.compile(">[\\s\\S]*?<");

        int statusCode = 0;
        try {
            statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return;
            }
            String response = method.getResponseBodyAsString();
            if (response != null) {
                // get duration
                Matcher second_match = second_pattern.matcher(response);
                second_match.find();
                String seconds_str = second_match.group();
                if (seconds_str != null) {
                    Matcher duration_match = quotes_pattern.matcher(seconds_str);
                    duration_match.find();
                    video.setDuration(Integer.parseInt(duration_match.group().replace("'", "")));
                }
                // get upload date if necessary
                if (video.getCreationDate() == null) {
                    Matcher upload_match = upload_pattern.matcher(response);
                    upload_match.find();
                    String upload_str = upload_match.group();
                    if (upload_str != null) {
                        upload_match = brackets_pattern.matcher(upload_str);
                        upload_match.find();
                        String str = upload_match.group().replace("<", "").replace(">", "").
                                replace("T", " ").replace("Z", " ");
                        video.setCreationDate(DateUtils.dateDBFormat.parse(str));
                    }
                }
                updateVideo(video);
            }
        } catch (IOException | ParseException e) {
            throw new EltilandManagerException("Cannot save video data", e);
        }
    }

    @Override
    public void fillViewCount(Video video) throws EltilandManagerException {
        String url = YOUTUBE_API_LINK + video.getLink() + YOUTUBE_API_PARAMS;
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);

        Pattern statistics_pattern = Pattern.compile("viewCount=[\\s\\S]*?/>");
        Pattern quotes_pattern = Pattern.compile("'[\\s\\S]*?'");

        int statusCode = 0;
        try {
            statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return;
            }
            String response = method.getResponseBodyAsString();
            if (response != null) {
                Matcher statistics_match = statistics_pattern.matcher(response);
                statistics_match.find();
                String statistics_str = statistics_match.group();
                if (statistics_str != null) {
                    Matcher views_match = quotes_pattern.matcher(statistics_str);
                    views_match.find();
                    Integer views = Integer.parseInt(views_match.group().replace("'", ""));
                    video.setViewCount(views);
                    updateVideo(video);
                }
            }
        } catch (IOException e) {
            throw new EltilandManagerException("Cannot save video data", e);
        }
    }
}
