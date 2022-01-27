package monfroglio.elena.model;

import java.util.ArrayList;

public class Concept {
	private String title;
	private ArrayList<String> words;
	
	public Concept(String title, ArrayList<String> words) {
		this.title = title;
		this.words = words;
	}
	
	public String getTitle() {
		return title;
	}

	public ArrayList<String> getWords() {
		return words;
	}
}
