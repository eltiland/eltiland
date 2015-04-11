package com.eltiland.model.file;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.Pei;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.webinar.Webinar;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity aggregates file metadata.
 * <p/>
 * Each file has to entity - body and previewBody.
 * Both of them is stored in FileBody and cannot be null.
 * <p/>
 * Body of file represent file source. It can be assessed as binary data from DB ({@link FileBody#body})
 * or loaded from disk ({@link FileBody#filename}). Body cannot be null.
 * <p/>
 * PreviewBody of file represent preview icon/image.
 * If, file is image - preview icon stored in DB and loaded from ({@link FileBody#body}).
 * If file is not image, used standard icon, which placed on disk ({@link FileBody#filename}).
 * <p/>
 * ({@link FileBody#filename}) for standard icons stored in utility class for all known types of files.
 * <p/>
 * Body of them used only for startup sync to ensure presents of icon on disk.
 * PreviewBody of them used only as link to icon on disk ({@link FileBody#filename}).
 */
@Entity
@Table(name = "file", schema = "public")
public class File extends AbstractIdentifiable implements Serializable {
    private String name;
    private String type;
    private long size;

    private FileBody body;
    private FileBody previewBody;

    private CourseItem item;
    private ELTCourseItem courseItem;

    private Webinar webinar;

    @Column(name = "name", nullable = false, length = 256)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "type", nullable = false, length = 60)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "size", nullable = false)
    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body", unique = true, nullable = false)
    public FileBody getBody() {
        return body;
    }

    public void setBody(FileBody body) {
        this.body = body;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preview_body", unique = true, nullable = false)
    public FileBody getPreviewBody() {
        return previewBody;
    }


    public void setPreviewBody(FileBody previewBody) {
        this.previewBody = previewBody;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public CourseItem getItem() {
        return item;
    }

    public void setItem(CourseItem item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webinar")
    public Webinar getWebinar() {
        return webinar;
    }

    public void setWebinar(Webinar webinar) {
        this.webinar = webinar;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseItem")
    public ELTCourseItem getCourseItem() {
        return courseItem;
    }

    public void setCourseItem(ELTCourseItem courseItem) {
        this.courseItem = courseItem;
    }
}
