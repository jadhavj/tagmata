package com.jasscon.tagmata;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
			doc.add(new TextField("cardId", cardId == null ? "" + System.currentTimeMillis() : cardId, Field.Store.YES));
			doc.add(new TextField("tags", tags, Field.Store.YES));
			doc.add(new TextField("text", text, Field.Store.YES));
			doc.add(new TextField("showAllFields", "showAllFields",
					Field.Store.YES));
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
					new String[] { "tags", "text", "showAllFields" }, analyzer);
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
				card.setCardId(Long.parseLong(doc.get("cardId")));
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

	public static Card getCard(String cardId, String bookmarkId) {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get("tagmata-index")));
			Analyzer analyzer = new StandardAnalyzer();
			MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { "cardId" }, analyzer);
			IndexSearcher searcher = new IndexSearcher(reader);
			BooleanQuery.Builder query = new BooleanQuery.Builder();
			query.add(parser.parse(cardId), Occur.SHOULD);
			if (bookmarkId != null) {
				query.add(parser.parse(bookmarkId), Occur.MUST_NOT);
			}
			TopDocs topDocs = searcher.search(query.build(), 50000);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			Document doc = reader.document(scoreDocs[0].doc);
			Card card = new Card();
			card.setCardId(Long.parseLong(doc.get("cardId")));
			card.setTags(doc.get("tags"));
			card.setText(doc.get("text"));
			card.setBookmarkId(Long.parseLong(bookmarkId));
			if (doc.get("pos") != null)
				card.setPos(Integer.parseInt(doc.get("pos")));
			reader.close();
			return card;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void deleteCard(String cardId, String bookmarkId) {
		try {
			index = FSDirectory.open(Paths.get("tagmata-index"));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(index, config);
			BooleanQuery query = new BooleanQuery();
			MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { "cardId" }, analyzer);
			query.add(parser.parse(cardId), Occur.SHOULD);
			if (bookmarkId != null) {
				query.add(parser.parse(bookmarkId), Occur.MUST_NOT);
			}
			writer.deleteDocuments(query);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteBookmark(String bookmarkId) {
		try {
			index = FSDirectory.open(Paths.get("tagmata-index"));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(index, config);
			writer.deleteDocuments(new Term("bookmarkId", bookmarkId));
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addBookmark(String cardId, int pos) {
		try {
			index = FSDirectory.open(Paths.get("tagmata-index"));
			StandardAnalyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(index, config);
			Document doc = new Document();
			doc.add(new TextField("bookmarkId", System.currentTimeMillis() + "", Field.Store.YES));
			doc.add(new TextField("cardId", cardId, Field.Store.YES));
			doc.add(new TextField("pos", "" + pos, Field.Store.YES));
			doc.add(new TextField("showAllBookmarks", "showAllBookmarks",
					Field.Store.YES));
			writer.addDocument(doc);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Card> getBookmarks() {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get("tagmata-index")));
			Analyzer analyzer = new StandardAnalyzer();
			MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { "showAllBookmarks" }, analyzer);
			IndexSearcher searcher = new IndexSearcher(reader);
			BooleanQuery.Builder query = new BooleanQuery.Builder();
			query.add(parser.parse("showAllBookmarks"), Occur.SHOULD);
			TopDocs topDocs = searcher.search(query.build(), 50000);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			List<Card> cards = new ArrayList<Card>();
			for (ScoreDoc scoreDoc : scoreDocs) {
				Document doc = reader.document(scoreDoc.doc);
				String cardId = doc.get("cardId");
				String bookmarkId = doc.get("bookmarkId");
				Card card = getCard(cardId, bookmarkId);
				card.setBookmarkId(Long.parseLong(bookmarkId));
				cards.add(card);
			}
			Comparator<Card> comparator = new Comparator<Card>() {

				public int compare(Card o1, Card o2) {
					return new Integer(o1.getPos()).compareTo(new Integer(o2
							.getPos()));
				}
			};
			Collections.sort(cards, comparator);
			reader.close();
			return cards;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Card>();
	}

	public static Card getBookmark(String bookmarkId) {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get("tagmata-index")));
			Analyzer analyzer = new StandardAnalyzer();
			MultiFieldQueryParser parser = new MultiFieldQueryParser(
					new String[] { "bookmarkId" }, analyzer);
			IndexSearcher searcher = new IndexSearcher(reader);
			BooleanQuery.Builder query = new BooleanQuery.Builder();
			query.add(parser.parse(bookmarkId), Occur.SHOULD);
			TopDocs topDocs = searcher.search(query.build(), 50000);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			Document doc = reader.document(scoreDocs[0].doc);
			String cardId = doc.get("cardId");
			return getCard(cardId, bookmarkId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateBookmarks(List<Card> bookmarks) {
		for (Card bm : bookmarks) {
			deleteBookmark(bm.getBookmarkId() + "");
		}
		int i = 0;
		for (Card bm : bookmarks) {
			addBookmark(bm.getCardId() + "", i++);
		}
	}
}
