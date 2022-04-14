package monfroglio.elena.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import com.opencsv.CSVReader;

import monfroglio.elena.model.Lemma;  



public class SenticnetManager {
	private String language;
	private ArrayList<Lemma> concepts = new ArrayList<>();
	private ArrayList<String> words = new ArrayList<>();
	
	public SenticnetManager(String language) {
		this.language = language;
		concepts = new ArrayList<>();
		words = new ArrayList<>();
	}
	
	public void readCSV() {
		
		String fileName = "";
		if(language.equals("italiano"))		fileName = "senticnet_it/senticnet.csv";
		else 	fileName = "senticnet/senticnet.csv";
		CSVReader reader = null;  
		try {
			Lemma c = null;
			String [] nextLine;  
			//parsing a CSV file into Scanner class constructor  
			reader = new CSVReader(new FileReader(fileName));  
			reader.readNext();
			while ((nextLine = reader.readNext()) != null)  {  
				for(String token : nextLine)  {  
					if(!token.equals("")) {
						c = Lemma.createLemmaFromLine(token);
						if(c!=null) {
							concepts.add(c);
							words.add(c.getTitle());
						}
					}
				}  
			}  
		} catch (Exception e)   {  
			e.printStackTrace();  
		}  
		System.out.print("\n");  
			 
		
	}
	
	public ArrayList<Lemma> getLemmas(){
		return concepts;
	}
	
	public ArrayList<String> getWords(){
		return words;
	}
}
