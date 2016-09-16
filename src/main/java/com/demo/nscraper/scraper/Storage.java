package com.demo.nscraper.scraper;

import com.demo.nscraper.enity.Page;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Storage {

    static void savePage(Page condition) throws IOException {
        File outFile = new File(dir + File.separator + filenameHash(condition.url));
        objectMapper.writeValue(outFile, condition);
    }

    public static Page loadPage(String filename) throws IOException {
        File inFile = new File(dir + File.separator + filename);
        return loadPage(inFile);
    }

    static boolean pageExists(String fullUrl) {
        return new File(dir + File.separator + filenameHash(fullUrl)).exists();
    }

    public static Page loadPage(File inFile) throws IOException {
        return objectMapper.readValue(inFile, Page.class);
    }

    static String filenameHash(String fullUrl) {
        String name = fullUrl.toLowerCase();
        name = name.replaceAll("(.*\\.uk/)(.*$)", "$2");
        name = name.replaceAll("[^a-z-]", "-");
        return name + ".json";
    }

    public static List<File> allPages() {
        return Arrays.stream(Optional.ofNullable(dir.listFiles()).orElse(new File[0]))
                .filter(File::isFile)
                .collect(Collectors.toList());
    }



    private static File dir = new File(System.getProperty("user.home") + File.separator + "nscraper");
    public static File indexDir = new File(dir.getAbsolutePath() + File.separator + "index");


    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        if (!dir.exists()) dir.mkdirs();
        if (!indexDir.exists()) indexDir.mkdirs();
    }

}
