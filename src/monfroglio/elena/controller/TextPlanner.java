package monfroglio.elena.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.PhraseType;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;
import javax.json.*;


public class TextPlanner {
	private String lingua;
	private String fileName;
	private Utente user;
	private Settimana settimana;
	private ArrayList<String> order;
	
	public TextPlanner() {
		lingua = "italiano";
	}
	
	public TextPlanner(String lingua) {
		this.lingua = lingua;
	}

	public TextPlanner(String lingua, Utente user, Settimana settimana) {
		this.lingua = lingua;
		this.user = user;
		this.settimana = settimana;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public ArrayList<String> getOrder(){
		return order;
	}
	
	//what to says_objects
	public void contentDetermination() {
		//add lingua
		JsonObjectBuilder builder = Json.createObjectBuilder()
				.add("lingua", lingua)
				.add("nome utente", user.getNome())
				.add("sesso utente", user.getSesso());
		
		for (Macronutriente m:settimana.getMacronutrienti()) {
			builder.add(m.getNome(), Json.createObjectBuilder()
					.add("punteggio", m.getPunteggio())
					.add("punteggioEnvironment", Double.toString(m.getPunteggioEnvironment()))
					.add("moreIsBetter", m.getMoreIsBetter()));
		}		

		JsonObject value = builder.build();
		createJsonFile(value);	    
	}
		
	//Valore compreso tra 0 e xxxx
	private double getIndexAmbientalePerEmissioni() {
		double index = 0;
		double indexTemp = 0;
		int count = 0;
		for(Macronutriente m:settimana.getMacronutrienti()) {
			indexTemp = m.getPunteggioEnvironment();
			count = m.getPunteggio();
			if(!m.getMoreIsBetter()) {
				//estraggo dal punteggio less is more la quantità di consumo
				switch(m.getPunteggio()) {
					case 5:
						count = 0;
						break;
					case 4:
						count = 1;
						break;
					case 3:
						count = 2;
						break;
					case 2:
						count = 3;
						break;
					case 1:
						count = 4;
						break;
					case 0:
						count = 5;
						break;
				}
			}
			index += count*indexTemp;
		}
		return index;
	}
	

	public void textStructuring() {
		//in questo metodo imposterò l'ordine e la struttura in base allo user model
		order = new ArrayList<>();
		//VERY BAD 	--> BUT --> VERY GOOD
		order.add(PhraseType.VERYBAD);
		order.add(PhraseType.GOOD);
		//BAT 		--> BUT --> GOOD
		order.add(PhraseType.BAD);
		order.add(PhraseType.VERYGOOD);
	}
	
	private void createJsonFile(JsonObject value) {
		FileWriter file = null;
		try {
			
			String fileFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss 'TextPlan.json'", Locale.getDefault()).format(new Date());
			this.fileName = "src/monfroglio/elena/files/ "+fileFormat;
			file = new FileWriter(this.fileName);
			file.write(value.toString());

            file.flush();
            file.close();
		} catch (IOException e) {
			
            e.printStackTrace(); 
            
        } 
	}
	
}
