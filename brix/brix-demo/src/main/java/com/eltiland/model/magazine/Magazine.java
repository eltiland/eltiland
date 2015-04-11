package com.eltiland.model.magazine;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.Countable;
import com.eltiland.model.file.File;
import com.eltiland.model.payment.PaidEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for child question magazine.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "magazine", schema = "magazine")
public class Magazine extends AbstractIdentifiable implements PaidEntity, Countable {
    private File content;
    private File cover;
    private String name;
    private String topic;
    private String about;
    private BigDecimal price;
    private boolean active;
    private Integer index;
    private Set<Client> clients = new HashSet<>();
    private Set<Client> clientsDownloaded = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    public File getContent() {
        return content;
    }

    public void setContent(File content) {
        this.content = content;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public File getCover() {
        return cover;
    }

    public void setCover(File cover) {
        this.cover = cover;
    }

    @Column(name = "name", nullable = false, length = 256)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "topic", nullable = false, length = 256)
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Column(name = "content", columnDefinition = "TEXT")
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Column(name = "price", nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    @Transient
    public boolean getStatus() {
        return false;
    }

    @Override
    public void setStatus(boolean status) {
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "magazines")
    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    @Column(name = "active", nullable = false, columnDefinition = "boolean default TRUE")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "downloadedMagazines")
    public Set<Client> getClientsDownloaded() {
        return clientsDownloaded;
    }

    public void setClientsDownloaded(Set<Client> clientsDownloaded) {
        this.clientsDownloaded = clientsDownloaded;
    }

    @Override
    @Column(name = "index")
    public Integer getIndex() {
        return index;
    }

    @Override
    public void setIndex(Integer index) {
        this.index = index;
    }
}
