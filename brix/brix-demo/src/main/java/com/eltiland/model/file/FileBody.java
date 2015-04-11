package com.eltiland.model.file;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Intermediate entity to define where to store the file body.<br/>
 * Body and Filename cannot be both null or not null.         <br/>
 * {@code filename != null} means that file body stores on file system.    <br/>
 * {@code filename == null} means that file body stores in {@link FileBody#body}.
 */
@Entity
@Table(name = "file_body", schema = "public")
public class FileBody extends AbstractIdentifiable implements Serializable
{

    private byte[] body;

    private String filename;

    private String hash;

    private boolean processed;

    @Column(name = "processed", unique = true, length = 256)
    public boolean getProcessed()
    {
        return processed;
    }

    public void setProcessed(boolean processed)
    {
        this.processed = processed;
    }

    @Column(name = "hash", unique = true, length = 256)
    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    @Column(name = "body")
    public byte[] getBody()
    {
        return body;
    }

    public void setBody(byte[] body)
    {
        this.body = body;
    }

    @Column(name = "filename", unique = true)
    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }
}
