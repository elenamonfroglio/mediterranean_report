package monfroglio.elena.model;

import java.util.ArrayList;

public class Lemma {
	private String title;
	private Emotion primaryEmotion;
	private Emotion secondaryEmotion;
	
	public Lemma(String title, Emotion primaryEmotion, Emotion secondaryEmotion) {
		this.title = title;
		this.primaryEmotion = primaryEmotion;
		this.secondaryEmotion = secondaryEmotion;
	}
	
	public Lemma(String title) {
		this.title = title;
	}
	
	public static Lemma createLemmaFromLine(String line) {
		Lemma ret = null;
		String[] list;
		list = line.split("\t");
		if(list.length>5) {
			Emotion primary = new Emotion(list[5]);
			Emotion secondary = new Emotion(list[6]);
			ret = new Lemma(list[0],primary,secondary);
		}
		return ret;
	}
	
	public boolean hasPrimaryEmotion(Emotion e) {
		return this.primaryEmotion.equals(e);
	}
	
	public boolean hasPrimaryEmotionType(String e) {
		return this.primaryEmotion.getType().equals(e);
	}
	
	public boolean equals(Lemma c) {
		return (this.title.equals(c.title));
	}
	
	public String getTitle() {
		return title;
	}
	
	public Emotion getPrimaryEmotion() {
		return primaryEmotion;
	}
	
	public Emotion getSecondaryEmotion() {
		return secondaryEmotion;
	}
	
}
