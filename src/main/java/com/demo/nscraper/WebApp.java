package com.demo.nscraper;

import com.demo.nscraper.enity.PageLink;
import com.demo.nscraper.scraper.Storage;
import ratpack.jackson.Jackson;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rux on 11/08/16.
 */
public class WebApp {
    public WebApp() {
    }

    public void run() throws Exception {
        RatpackServer.start(server -> server
                .registryOf(registry -> registry.add("World!"))
                .handlers(chain -> chain
                        .get(ctx -> ctx.getResponse().contentType("text/html;charset=utf-8").send(index()))
                        .get(":query", ctx -> {
                            String query = ctx.getPathTokens().get("query");
                            String datafile = new SearchStorage().search(query);
                            if (datafile == null) {
                                ctx.getResponse().status(404);
                                ctx.render("No results found");
                                return;
                            }
                            ctx.getResponse().contentType("application/json;charset=utf-8");
                            ctx.render(Jackson.json(Storage.loadPage(datafile)));
                        })
                ));
    }

    private String index() {
        StringBuilder s = new StringBuilder("Add your query to url, like :");
        s.append("<ul>");
        for (String sample : samples)
            s.append(String.format("<li><a href=\"/%s\">%s</a></li>", sample, sample));
        s.append("</ul>");
        return s.toString();
    }

    private static List<String> samples = Arrays.asList(
        "Allergy treatment", "Cancer",
        "What are the symptoms of cancer?", "Treatments for headaches",
        "Diagnosing chickenpox", "Dehydration"
    );

}
