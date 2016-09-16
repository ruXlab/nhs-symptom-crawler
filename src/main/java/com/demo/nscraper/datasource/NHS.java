package com.demo.nscraper.datasource;

import com.demo.nscraper.enity.PageLink;
import com.demo.nscraper.enity.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 */
public class NHS {
    public static List<PageLink> fetchConditionIndex(char c) throws IOException {
        Document document = fetch(conditionIndex(c));
        return document.getElementsByTag("a").stream()
                .filter(a -> a.attr("href").contains("/conditions/"))
                .map(a -> new PageLink(a.text().trim(), normalizeUrl(a.attr("href"))))
                .collect(Collectors.toList());

    }

    public static List<Page> fetchCondition(PageLink pageLink) throws IOException {
        Document rootRawDoc = fetch(pageLink.url);
        Page rootPage = parsePage(pageLink.url, rootRawDoc);

        List<Page> pages = parseSectionLinks(rootRawDoc).stream()
            .map(link -> {
                try {
                    return parsePage(link.url, fetch(link.url));
                } catch (Exception oops) {
                    return null;
                }
            })
            .filter(page -> page != null)
            .collect(Collectors.toList());

        pages.add(rootPage);
        for (Page page : pages) page.disease = pageLink.title;

        return pages;
    }

    public static List<PageLink> parseSectionLinks(Document document) {
        return document.select("ul.sub-nav > li a").stream()
            .map(a -> new PageLink(
                    a.textNodes().isEmpty() ? a.text() : a.textNodes().get(0).toString(),
                    normalizeUrl(a.attr("href"))
            ))
            .collect(Collectors.toList());
    }

    public static Page parsePage(String url, Document document) throws IOException {
        Page condition = new Page(url);
        condition.section = document.select("ul.sub-nav > li.active > span.active-text").text();
        condition.description = document.select("head > meta[name='description']").attr("content");
        condition.title = document.select("h1").text();
        condition.content = document.select("div.main-content").text();
        return condition;
    }


    public static Page fetchCondition1(PageLink pageLink) throws IOException {
        Page condition = new Page(normalizeUrl(pageLink.url));
        Document document = fetch(condition.url);
        condition.description = document.select("head > meta[name='description']").attr("content");
        condition.title = document.select("h1").text();
        condition.content = document.select("div.main-content").text();
        condition.disease = pageLink.title;

        return condition;
    }


    private static Document fetch(String url) throws IOException {
        return Jsoup.connect(normalizeUrl(url)).get();
    }

    private static String normalizeUrl(String url) {
        if (url.startsWith("/")) url = "http://www.nhs.uk" + url;
        return url.trim();
    }

    private static String conditionIndex(char c) {
        return "/Conditions/Pages/BodyMap.aspx?Index=" + c;
    }

}
