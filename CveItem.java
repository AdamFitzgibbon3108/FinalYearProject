package com.example.model;

public class CveItem {
    private String id;
    private String summary;
    private String published;
    private String modifiedDate;
    private String baseScore;
    private String cwe;

    private String advisoryUrl; // optional Red Hat advisory link
    private String displayId;   // CVE ID or RHSA ID (used in table)
    private String sourceLink;  // URL to link to (NVD or Red Hat)

    public CveItem() {
    }

    public CveItem(String id, String summary, String published, String modifiedDate,
                   String baseScore, String cwe, String advisoryUrl,
                   String displayId, String sourceLink) {
        this.id = id;
        this.summary = summary;
        this.published = published;
        this.modifiedDate = modifiedDate;
        this.baseScore = baseScore;
        this.cwe = cwe;
        this.advisoryUrl = advisoryUrl;
        this.displayId = displayId;
        this.sourceLink = sourceLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(String baseScore) {
        this.baseScore = baseScore;
    }

    public String getCwe() {
        return cwe;
    }

    public void setCwe(String cwe) {
        this.cwe = cwe;
    }

    public String getAdvisoryUrl() {
        return advisoryUrl;
    }

    public void setAdvisoryUrl(String advisoryUrl) {
        this.advisoryUrl = advisoryUrl;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    @Override
    public String toString() {
        return "CveItem{" +
                "id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", published='" + published + '\'' +
                ", modifiedDate='" + modifiedDate + '\'' +
                ", baseScore='" + baseScore + '\'' +
                ", cwe='" + cwe + '\'' +
                ", advisoryUrl='" + advisoryUrl + '\'' +
                ", displayId='" + displayId + '\'' +
                ", sourceLink='" + sourceLink + '\'' +
                '}';
    }
}
