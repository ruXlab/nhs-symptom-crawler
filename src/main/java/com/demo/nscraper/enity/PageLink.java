package com.demo.nscraper.enity;

import java.util.ArrayList;
import java.util.List;

/**
 */
public final class PageLink {
    public final String title;
    public final String url;

    public PageLink(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "title=" + title +
                ", url=" + url +
                '}';
    }
}
