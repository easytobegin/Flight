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
        document12.add(new TextField("title","开始检票", Field.Store.YES));
        indexWriter.addDocument(document12);

        Document document13 = new Document();
        document13.add(new TextField("title","停止检票", Field.Store.YES));
        indexWriter.addDocument(document13);

        Document document14 = new Document();
        document14.add(new TextField("title","行李传送带开始时间", Field.Store.YES));
        indexWriter.addDocument(document14);

        Document document15 = new Document();
        document15.add(new TextField("title","行李传送带停止时间", Field.Store.YES));
        indexWriter.addDocument(document15);

        Document document16 = new Document();
        document16.add(new TextField("title","登机门打开时间", Field.Store.YES));
        indexWriter.addDocument(document16);

        Document document17 = new Document();
        document17.add(new TextField("title","登机门打开时间", Field.Store.YES));
        indexWriter.addDocument(document17);


        //评分bug
//        Document document18 = new Document();
//        document18.add(new TextField("title","航班任务", Field.Store.YES));
//        indexWriter.addDocument(document18);
        indexWriter.close();
    }
}
