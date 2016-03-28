package com.jasscon.tagmata;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

	private static Directory index;

	public static void addCard(String tags, String text) {
		addCard(null, tags, text);
	}
	
	public static void addCard(String cardId, String tags, String text) {
		try {
			index = FSDirectory.open(Paths.get("tagmata-index"));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(index, config);
			Document doc = new Document();
			doc.add(new TextField("cardId", cardId == null ? UUID.randomUUID().toString().replace("-", "") : cardId, Field.Store.YES));
			doc.add(new TextField("tags", tags, Field.Store.YES));
			doc.add(new TextField("text", text, Field.Store.YES));
			doc.add(new TextField("showAllFields", "showAllFields", Field.Store.YES));
			writer.addDocument(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Card> getCards(String searchTerms) {
		try {
			if (searchTerms.trim().length() == 0) {
				searchTerms = "showAllFields";
			}
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get("tagmata-index")));
			Analyzer analyzer = new StandardAnalyzer();
			MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { "tags", "text", "showAllFields"}, analyzer);
			IndexSearcher searcher = new IndexSearcher(reader);
			BooleanQuery.Builder query = new BooleanQuery.Builder();
			for (String term : searchTerms.split(" ")) {
				if (term.trim().length() == 0) {
					continue;
				}
				query.add(parser.parse(term), Occur.SHOULD);
			}
			TopDocs topDocs = searcher.search(query.build(), 50000);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			List<Card> cards = new ArrayList<Card>();
			for (ScoreDoc scoreDoc : scoreDocs) {
				Document doc = reader.document(scoreDoc.doc);
				Card card = new Card();
				card.setCardId(doc.get("cardId"));
				card.setTags(doc.get("tags"));
				card.setText(doc.get("text"));
				cards.add(card);
			}
			Collections.sort(cards);
			reader.close();
			return cards;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Card>();
	}
	
	public static void deleteCard(String cardId) {
		try {
			index = FSDirectory.open(Paths.get("tagmata-index"));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(index, config);
			writer.deleteDocuments(new Term("cardId", cardId));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
