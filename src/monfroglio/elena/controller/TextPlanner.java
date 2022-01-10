package monfroglio.elena.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


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
		
		int indexAmbientale = getIndexAmbientale();
		
		//EXAMPLE WITH STRINGWRITER
		JsonObject value = Json.createObjectBuilder()
				.add("indexEnvironment", indexAmbientale)
				.build();
		
		createJsonFile(value);
	    
	}
	
	public int getIndexAmbientale() {
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
		return index;
	}
	
	public void createJsonFile(JsonObject value) {
		FileWriter file = null;
		try {
			
			String fileName = new SimpleDateFormat("yyyyMMdd HH:mm:ss'.json'", Locale.getDefault()).format(new Date());
			file = new FileWriter("src/monfroglio/elena/files/"+user.cognome+" "+fileName);
			file.write(value.toString());
			
		} catch (IOException e) {
			
            e.printStackTrace(); 
            
        } finally {
 
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
	}
	
}
