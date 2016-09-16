package com.demo.nscraper;

import com.demo.nscraper.enity.Page;
import com.demo.nscraper.scraper.Storage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 */
public class SearchStorage {
    private static final Logger logger = LoggerFactory.getLogger(SearchStorage.class);
    private final Directory dir;
    private final StandardAnalyzer analyzer;


    public SearchStorage() throws IOException {
        analyzer = new StandardAnalyzer();

        dir = FSDirectory.open(Storage.indexDir.toPath());


    }


    public void indexDocument(IndexWriter indexWriter, File file, Page page) throws IOException {
        Document document = new Document();
        document.add(new StringField(KEY_FILE, file.getName(), Field.Store.YES));
        document.add(new TextField(KEY_TITLE, page.title, Field.Store.YES));
        document.add(new TextField(KEY_DESCRIPTION, page.description, Field.Store.YES));
        document.add(new TextField(KEY_DISEASE, page.disease, Field.Store.YES));
        document.add(new TextField(KEY_CONTENT, page.content, Field.Store.NO));
        document.add(new TextField(KEY_URL, page.url, Field.Store.YES));
        document.add(new TextField(KEY_SECTION, page.section, Field.Store.NO));
        indexWriter.updateDocument(new Term("id", file.getName()), document);
    }

    public void indexDirectoryContent() throws IOException {
        logger.info("Indexing cache...");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter writer = new IndexWriter(dir, config);
        for (File file : Storage.allPages()) {
            Page condition;
            try {
                condition = Storage.loadPage(file);
            } catch (IOException e) {
                logger.warn("Can't load file {}, ignoring..", file, e);
                continue;
            }
            indexDocument(writer, file, condition);
        }
        writer.close();
        logger.info("Indexation finished");

    }

    public void test() throws IOException, ParseException {
        indexDirectoryContent();
        search("headache");
        search("allergy");
        search("symptom of allergy");
        search("Treating allergies");
        search("treating of allergies");
        search("what are the symptoms of cancer?");
        search("treatments for headaches");
        search("What causes tension headaches?");
        search("Symptoms of anxiety");
    }

    /**
     * Searches for query
     * @param searchString user's query
     * @return page's filename or null
     */
    public String search(String searchString) throws IOException, ParseException {
        searchString = searchString.toLowerCase().trim();
        IndexReader indexReader = DirectoryReader.open(dir);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Analyzer analyzer = new StandardAnalyzer();

        String q = "" +
                KEY_CONTENT + ":(\"Q\" Q) " +
                KEY_DESCRIPTION + ":(\"Q\" Q) " +
                KEY_DISEASE + ":(\"Q\"~)^100 " +
                "";
        q = q.replace("Q", searchString);
        for (String word : wordsToBoost) {
            if (!searchString.contains(word)) continue;
            q += String.format("%s:%s^10 ", KEY_SECTION, word);
        }

        QueryParser queryParser = new QueryParser("", analyzer);
        Query query = queryParser.parse(q);

        logger.debug("Query {}", query.toString());

        TopDocs topDocs = indexSearcher.search(query, 1);

        String docFilename = null;
        if (topDocs.scoreDocs.length > 0) {
            docFilename = indexReader.document(topDocs.scoreDocs[0].doc).getField(KEY_FILE).stringValue();
        }

        indexReader.close();

        return docFilename;

    }

    private static final String[] wordsToBoost = "symptoms,diagnosis,treatment,self-help,complications,prevention,children,causes,living-with".split(",");


    public static final String KEY_FILE = "file";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DISEASE = "name";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_URL = "url";
    public static final String KEY_SECTION = "section";

}
