package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Domain model representing school announcements and notices.
 */
public class Announcement {
    private int id;
    private String titleEn;
    private String titleAm;
    private String contentEn;
    private String contentAm;
    private String targetRole; // ALL, TEACHER, STUDENT, STAFF
    private Integer authorId;
    private Date publishedDate;
    private Date expiryDate;
    private boolean isActive;
    private Timestamp createdAt;

    // Joined display fields
    private String authorName;

    public Announcement() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }

    public String getTitleAm() { return titleAm; }
    public void setTitleAm(String titleAm) { this.titleAm = titleAm; }

    public String getContentEn() { return contentEn; }
    public void setContentEn(String contentEn) { this.contentEn = contentEn; }

    public String getContentAm() { return contentAm; }
    public void setContentAm(String contentAm) { this.contentAm = contentAm; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public Integer getAuthorId() { return authorId; }
    public void setAuthorId(Integer authorId) { this.authorId = authorId; }

    public Date getPublishedDate() { return publishedDate; }
    public void setPublishedDate(Date publishedDate) { this.publishedDate = publishedDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getTitle(String locale) {
        return "am".equalsIgnoreCase(locale) && titleAm != null && !titleAm.isEmpty() ? titleAm : titleEn;
    }

    public String getContent(String locale) {
        return "am".equalsIgnoreCase(locale) && contentAm != null && !contentAm.isEmpty() ? contentAm : contentEn;
    }
}
