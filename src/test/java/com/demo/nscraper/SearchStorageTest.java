package com.demo.nscraper;

import com.demo.nscraper.enity.Page;
import com.demo.nscraper.scraper.Storage;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Test;
import ratpack.jackson.Jackson;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by rux on 18/08/16.
 */
public class SearchStorageTest {
    private SearchStorage searchStorage;

    @Before
    public void setUp() throws Exception {
        searchStorage = new SearchStorage();
        searchStorage.indexDirectoryContent();
    }

    @Test
    public void search() throws Exception {
        Page allergy = searchAndLoad("allergy");
        assertThat(allergy.url, is("http://www.nhs.uk/Conditions/food-allergy/Pages/living-with.aspx"));

        Page allergySymptoms = searchAndLoad("symptoms of allergy");
        assertThat(allergySymptoms.url, is("http://www.nhs.uk/Conditions/food-allergy/Pages/Symptoms.aspx"));

        Page he = searchAndLoad("What causes tension headaches?");
        assertThat(he.url, is("http://www.nhs.uk/Conditions/Migraine/Pages/Causes.aspx\""));
    }

    public Page searchAndLoad(String str) throws IOException, ParseException {
        return Storage.loadPage(searchStorage.search(str));
    }

}