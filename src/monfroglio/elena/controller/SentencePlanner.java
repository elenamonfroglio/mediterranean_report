package monfroglio.elena.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

public class SentencePlanner {
	String filename;

	public SentencePlanner(String filename) {
		this.filename = filename;
	}
	
	public void sentenceAggregation() {
		
	}
	
	public void Lexicalisation() {
		
	}
	
	public void readJson() {
		File initialFile = new File(filename);
	    InputStream targetStream;
		try {
			targetStream = new FileInputStream(initialFile);

			JsonReaderFactory factory = Json.createReaderFactory(null);
			JsonReader reader = factory.createReader(targetStream);
			JsonObject object = reader.readObject();
            reader.close();
            //read string data
            //System.out.println("\n\nCereali: " + object.getInt("Cereali"));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
