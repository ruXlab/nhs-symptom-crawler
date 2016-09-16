package com.demo.nscraper.scraper;

import com.demo.nscraper.datasource.NHS;
import com.demo.nscraper.enity.PageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

/**

 */
public class Scraper {
    private static final Logger logger = LoggerFactory.getLogger(ScraperWorker.class);

    private final ExecutorService pool;
    private final BlockingQueue<PageLink> queue;

    public Scraper() {
        int threadsAmount = Runtime.getRuntime().availableProcessors() - 1;
        queue = new ArrayBlockingQueue<>(threadsAmount + 1);
        pool = Executors.newFixedThreadPool(threadsAmount);
        for (int i = 0; i < threadsAmount; i++) pool.submit(new ScraperWorker(queue));
    }

    public void run() throws IOException, InterruptedException {
        logger.info("Getting conditions index...");
        ArrayList<PageLink> links = new ArrayList<>();
        for(char c = 'A'; c <= 'Z'; c++) {
            links.addAll(NHS.fetchConditionIndex(c));
        }
        logger.info("Got {} links with conditions, fetching... Please be patient", links.size());



        for (int i = 0; i < links.size(); i++) {
            if (i % 100 == 0) logger.info("{} links done..", i);
            if (Storage.pageExists(links.get(i).url)) continue;
            queue.put(links.get(i));
        }


        while (!queue.isEmpty()) Thread.sleep(1000L);
        pool.shutdown();
        logger.info("Scraper is finished");

    }

}
