package com.demo.nscraper.scraper;

import com.demo.nscraper.datasource.NHS;
import com.demo.nscraper.enity.Page;
import com.demo.nscraper.enity.PageLink;
import org.jsoup.HttpStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 */
public class ScraperWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ScraperWorker.class);

    private final BlockingQueue<PageLink> queue;


    public ScraperWorker(BlockingQueue<PageLink> queue) {
        this.queue = queue;
    }

    private void processNextTask(PageLink task) throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            try {
                List<Page> pages = NHS.fetchCondition(task);
                for (Page page : pages) Storage.savePage(page);

                logger.debug("Fetched {} pages for {} ", pages.size(), task.title);
                return;
            } catch (Exception e) {
                if (e instanceof HttpStatusException && ((HttpStatusException)e).getStatusCode() == 404) {
                    logger.warn("Got error 404 for {} - {}", task.title, task.url, i+1, e);
                    return;
                }
                logger.warn("Can't fetch {} - {} at attempt #{}", task.title, task.url, i+1, e);
                i++;
            }
            Thread.sleep(200 + i * 300);
        }
        logger.error("Skipping {} - {} as failed to fetch after few attempts", task.title, task.url);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                processNextTask(queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.debug("Worker is done");
    }





}
