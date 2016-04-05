package com.jasscon.tagmata;

public class Card implements Comparable<Card> {
	private Long cardId;
	private Long bookmarkId;
	private String tags;
	private String text;
	private int pos;

	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}

	public Long getBookmarkId() {
		return bookmarkId;
	}

	public void setBookmarkId(Long bookmarkId) {
		this.bookmarkId = bookmarkId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int compareTo(Card o) {
		return this.cardId.compareTo(o.getCardId());
	}
	
	public String snapshot() {
		return "Tags: " + StringUtil.substring(tags, 100) + "\n Text: " + StringUtil.substring(text, 100);
	}
}
