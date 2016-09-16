package com.demo.nscraper;

import com.demo.nscraper.scraper.Scraper;

public class NScraperApplication {

	public static void main(String[] args) throws Exception {
        new Scraper().run();
        new SearchStorage().indexDirectoryContent();
        new WebApp().run();
	}
}
