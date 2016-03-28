package com.jasscon.tagmata;

public class Card implements Comparable {
	private String cardId;
	private String tags;
	private String text;

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
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

	public int compareTo(Object o) {
		return this.cardId.compareTo(((Card) o).getCardId());
	}
}
