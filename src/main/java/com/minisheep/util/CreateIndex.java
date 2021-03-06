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
import sun.misc.DoubleConsts;

import java.io.File;
import java.io.IOException;

/**
 * Created by minisheep on 17/1/24.
 */
public class CreateIndex {
    public static void createIndex(String indexPath) throws IOException {
        Directory directory = FSDirectory.open(new File(indexPath));
        //Analyzer analyzer = new IKAnalyzer();

        IKAnalyzer analyzer = new IKAnalyzer();
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

        Document document5 = new Document();
        document5.add(new TextField("title","登机口",Field.Store.YES));
        indexWriter.addDocument(document5);

        Document document6 = new Document();
        document6.add(new TextField("title","候机楼", Field.Store.YES));
        indexWriter.addDocument(document6);

        Document document7 = new Document();
        document7.add(new TextField("title","状态", Field.Store.YES));
        indexWriter.addDocument(document7);

        Document document8 = new Document();
        document8.add(new TextField("title","检票口", Field.Store.YES));
        indexWriter.addDocument(document8);

        Document document9 = new Document();
        document9.add(new TextField("title","始发站", Field.Store.YES));
        indexWriter.addDocument(document9);

        Document document10 = new Document();
        document10.add(new TextField("title","终点站", Field.Store.YES));
        indexWriter.addDocument(document10);

        Document document11 = new Document();
        document11.add(new TextField("title","经停", Field.Store.YES));
        indexWriter.addDocument(document11);

        Document document12 = new Document();
        document12.add(new TextField("title","值机时间", Field.Store.YES));
        indexWriter.addDocument(document12);

        Document document14 = new Document();
        document14.add(new TextField("title","行李传送带", Field.Store.YES));
        indexWriter.addDocument(document14);

        Document document21 = new Document();
        document21.add(new TextField("title","异常状态", Field.Store.YES));
        indexWriter.addDocument(document21);
        indexWriter.close();
    }
}
