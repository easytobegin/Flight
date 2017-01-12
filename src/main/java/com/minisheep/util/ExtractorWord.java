package com.minisheep.util;

import org.textmining.text.extraction.WordExtractor;

import java.io.*;

/**
 * Created by minisheep on 17/1/12.
 */

//word转pdf
public class ExtractorWord{
    public static String getText(String file){
        String s = "";
        String wordFile = file;
        try{
            FileInputStream in = new FileInputStream(new File(wordFile));
            WordExtractor extractor = new WordExtractor();
            s = extractor.extractText(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void ToTextFile(String doc,String filename) throws Exception{
        String s = "";
        String wordfile = doc;
        String txtfile = filename;
        WordExtractor extractor = null;
        try{
            s = getText(wordfile);
            PrintWriter pw = new PrintWriter(new FileWriter(new File(filename)));
            pw.write(s);
            pw.flush();
            pw.close();
            System.out.println("写入文件成功");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        try{
            String sc = getText("/Users/minisheep/Downloads/1.doc");
            System.out.print(sc);
            ToTextFile("/Users/minisheep/Downloads/1.doc","/Users/minisheep/Downloads/result.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
