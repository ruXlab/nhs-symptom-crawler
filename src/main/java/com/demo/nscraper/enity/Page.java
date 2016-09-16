package com.demo.nscraper.enity;

/**
 */
public final class Page {
    public String url;
    public String description;
    public String title;
    public String disease;
    public String content;
    public String section = "Default";

    public Page() {
    }

    public Page(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "ConditionPage{" +
                "url='" + url + '\'' +
                ", metaDescription='" + description + '\'' +
                ", title='" + title + '\'' +
                ", content='" + (content == null ? "null" : content.substring(0, Math.min(content.length(), 1000))) + '\'' +
                '}';
    }
}
