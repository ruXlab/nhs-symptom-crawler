package com.demo.nscraper.datasource;

import com.demo.nscraper.enity.Page;
import com.demo.nscraper.enity.PageLink;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by rux on 18/08/16.
 */
public class NHSTest {
    @Test
    public void fetchCondition() throws Exception {
        List<Page> allergies = NHS.fetchCondition(new PageLink("allergies", "http://www.nhs.uk/Conditions/Allergies/Pages/Introduction.aspx"));
        assertThat(allergies, is(not(nullValue())));
        assertThat(allergies.size(), is(not(0)));
        List<String> sections = allergies.stream().map(p -> p.section).collect(Collectors.toList());
        assertThat(sections, containsInAnyOrder("Allergies", "Allergies Symptoms", "Allergies Diagnosis", "Allergies Treatment", "Allergies Prevention"));
    }



}