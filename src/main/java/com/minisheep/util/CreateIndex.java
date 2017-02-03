package com.minisheep.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * Created by minisheep on 17/1/24.
 */
public class CreateIndex {
    public static void createIndex(String indexPath) throws IOException {
        Directory directory = FSDirectory.open(new File(indexPath));
        Analyzer analyzer = new IKAnalyzer();

//		IKAnalyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_46, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        Document document1 = new Document();

        document1.add(new TextField("title", "实际抵达", Field.Store.YES));
        indexWriter.addDocument(document1);

        Document document2 = new Document();
        document2.add(new TextField("title", "实际起飞", Field.Store.YES));
        indexWriter.addDocument(document2);

        Document document3 = new Document();
        document3.add(new TextField("title", "预计抵达", Field.Store.YES));
        indexWriter.addDocument(document3);

        Document document4 = new Document();
        document4.add(new TextField("title", "预计起飞", Field.Store.YES));
        indexWriter.addDocument(document4);
        indexWriter.close();
    }
}
