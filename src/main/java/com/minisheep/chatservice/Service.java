package com.minisheep.chatservice;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Random;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.minisheep.bean.Knowledge;
import com.minisheep.util.MysqlUtil;

/**
 * Created by minisheep on 16/12/28.
 */

public class Service {
	public static String cutWords(String words) throws IOException{
		String finalStr = "";
		StringReader sr = new StringReader(words);
		//智能分词可能会导致漏掉,false比较保险,比如成都到厦门的航班什么时候抵达,如果用智能分词的话,那么就会变成 成|都到|厦门的航班
		IKSegmenter ik = new IKSegmenter(sr, true);
		Lexeme lex = null;
		System.out.println("分词结果为:");
		while((lex = ik.next()) != null){
			System.out.print(lex.getLexemeText() + "|");
			finalStr += lex.getLexemeText() + "|";
		}
		System.out.println();
		return finalStr;
	}


	public static void createIndex(){   //建立知识库的索引
		List<Knowledge> knowledges = MysqlUtil.findAllKownLedge();
		Directory directory = null;
		IndexWriter indexWriter = null;
		String pathname = "/Users/minisheep/Documents/index";   //迁移到服务器时修改
		try{
			directory = FSDirectory.open(new File(pathname));
			IndexWriterConfig iWriterConfig = new IndexWriterConfig(Version.LUCENE_46, new IKAnalyzer(true));
			indexWriter = new IndexWriter(directory,iWriterConfig);
			Document document = null;
			
			for(Knowledge knowledge : knowledges){
				document = new Document();
				document.add(new TextField("question",knowledge.getQuestion(),Store.YES));
				document.add(new IntField("id", knowledge.getId(), Store.YES));
				document.add(new StringField("answer", knowledge.getAnswer(), Store.YES));
				document.add(new IntField("category", knowledge.getCategory(), Store.YES));
				indexWriter.addDocument(document);
			}
			indexWriter.close();
			directory.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private static Knowledge searchIndex(String content){
		Knowledge knowledge = null;
		Knowledge result = null;
		String pathname = "/Users/minisheep/Documents/index";
		try{
			Directory directory = FSDirectory.open(new File(pathname));
			IndexReader reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			QueryParser questParser = new QueryParser(Version.LUCENE_46,"question",new IKAnalyzer(true));
			Query query = questParser.parse(QueryParser.escape(content));
			
			TopDocs topDocs = searcher.search(query, 3);

			float maxScore = topDocs.getMaxScore();
			//System.out.println("最大的评分为:" + maxScore);

			System.out.println("给您推荐的相关问题答案如下:");
			if(topDocs.totalHits > 0){
				result = new Knowledge();
				knowledge = new Knowledge();
				ScoreDoc[] scoreDocs = topDocs.scoreDocs;
				for(ScoreDoc sd : scoreDocs){
					Document doc = searcher.doc(sd.doc);

					knowledge.setId(doc.getField("id").numericValue().intValue());
					knowledge.setQuestion(doc.get("question"));
					knowledge.setAnswer(doc.get("answer"));
					knowledge.setCategory(doc.getField("category").numericValue().intValue());

					//这部分可以做近似推荐

					System.out.println("评分为:" + sd.score);
					System.out.println("问题为:" + knowledge.getQuestion());
					System.out.println("答案为:" + knowledge.getAnswer());


					if(Math.abs(sd.score - maxScore) < 0.000001){
						result.setId(knowledge.getId());
						result.setAnswer(knowledge.getAnswer());
						result.setCategory(knowledge.getCategory());
						result.setQuestion(knowledge.getQuestion());
					}
					System.out.println("-------------------------------------");
				}
			}
			reader.close();
			directory.close();
		}catch (Exception e) {
			// TODO: handle exception
			knowledge = null;
			result = null;
			e.printStackTrace();
		}
		return result;
	}
	
	public static String chat(String openId,String createTime,String question){
		String answer = null;
		int chatCategory = 0;
		Knowledge knowledge = searchIndex(question);
		//找到匹配项
		if(null != knowledge){
			//System.out.println("该分类为:" + knowledge.getCategory());
			//笑话
			if(5 == knowledge.getCategory()){
				answer = MysqlUtil.getJoke();
				chatCategory = 5;
				//上下文
			}else if(4 == knowledge.getCategory()){
				//判断上一次的聊天类别
				int category = MysqlUtil.getLastCategory(openId);
				//如果是笑话，本次继续回复笑话给用户
				if(5 == category){
					answer = MysqlUtil.getJoke();
					chatCategory = 5;
				}else{
					answer = knowledge.getAnswer();
					chatCategory = knowledge.getCategory();
				}
			}else{  //普通对话
				answer = knowledge.getAnswer();
				//如果答案为空，根据知识id从问答知识分表中随机获取一条
				if("".equals(answer)){
					answer = MysqlUtil.getKnowledSub(knowledge.getId());
				}
				chatCategory = 4;
			}
		}else{ //未找到匹配项
			answer = getDefaultAnswer();
			chatCategory = 0;
		}
		MysqlUtil.saveChatLog(openId, createTime, question, answer, chatCategory);
		return answer;
	}
	
	public static String getDefaultAnswer(){
		String[] answer = {
				"是在下才疏学浅,要不您换种问法?",
				"时代在进步,我也要努力学习才是。",
				"您问的问题好深奥，我听不懂。",
				"我应该跟我的主人多学点东西的。",
				"嗯?"
		};
		return answer[getRandomNumber(answer.length)];
	}
	
	private static int getRandomNumber(int length){
		Random random = new Random();
		return random.nextInt(length);
	}
}
