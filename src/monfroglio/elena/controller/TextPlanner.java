package monfroglio.elena.controller;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;
import javax.json.*;


public class TextPlanner {
	public String lingua;
	public Utente user;
	public Settimana settimana;
	
	public TextPlanner() {
		lingua = "it";
	}
	
	public TextPlanner(String lingua) {
		this.lingua = lingua;
	}

	public TextPlanner(String lingua, Utente user, Settimana settimana) {
		this.lingua = lingua;
		this.user = user;
		this.settimana = settimana;
	}
	
	public void getTextPlan() {
		ArrayList<Macronutriente> mores = settimana.getAllMoreIsBetter();
		ArrayList<Macronutriente> lesses = settimana.getAllLessIsBetter();
		int index = 0;
		//
		if (user.interesseAmbientale) {
			int count = settimana.countBadEnvironment();
			
			//classifico il TextPlan con 4 diverse classi di gravità (0-4,4-8,8-12,12-16,16-20)
			if(count<4) index = 0;
			else if(count<8) index = 1;
			else if(count<12) index = 2;
			else if(count<16) index = 3;
			else index = 4;
		}
		/*
		//EXAMPLE WITH STRINGWRITER
		JsonObject value = Json.createObjectBuilder()
				.add("indexEnvironment", index)
				.build();
		StringWriter stringWriter = new StringWriter();
	    JsonWriter writer = Json.createWriter(stringWriter);
	 
	    writer.writeObject(value);
	    writer.close();
	    System.out.println(stringWriter.getBuffer().toString());
	    */
	}
	
}
