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
	
	//which information to present in individual sentences 
	public void sentenceAggregation() {
		JsonObject object = readJson();
		String evaluation = object.getString("Cereali");
	}
	
	//finding the right words and phrases to express information --> senticnet
	public void lexicalisation() {
		
	}
	
	public JsonObject readJson() {
		File initialFile = new File(filename);
	    InputStream targetStream;
	    JsonObject object = null;
		try {
			targetStream = new FileInputStream(initialFile);

			JsonReaderFactory factory = Json.createReaderFactory(null);
			JsonReader reader = factory.createReader(targetStream);
			object = reader.readObject();
            reader.close();
            //read string data
            //System.out.println("\n\nCereali: " + object.getInt("Cereali"));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
}
