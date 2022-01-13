package monfroglio.elena.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;
import javax.json.*;


public class TextPlanner {
	public String lingua;
	public String fileName;
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
	
	//what to says
	public void contentDetermination() {
		//add lingua
		JsonObjectBuilder builder = Json.createObjectBuilder()
				.add("lingua", lingua);
		
		//add macronutrienti
		for (Macronutriente m:settimana.getMacronutrienti()) {
			String content = getContentFromPunteggio(m);
			builder.add(m.getNome(), content);
		}		

		//add indexAmbientale		
		//int indexAmbientale = getIndexAmbientale_v0();
		double indexAmbientale = getIndexAmbientalePerEmissioni();
		builder.add("indexEnvironment", indexAmbientale);
		
		JsonObject value = builder.build();
		
		createJsonFile(value);
	    
	}
	
	private String getContentFromPunteggio(Macronutriente m) {
		if(m.getMoreIsBetter()) {
			if(m.getPunteggio()==5) 	return "very good";
			else if(m.getPunteggio()==4 || m.getPunteggio()==3)		return "good";
			else if(m.getPunteggio()==2 || m.getPunteggio()==1)		return "bad";
			else	return "very bad";
		}else {
			if(m.getPunteggio()==5)		return "very bad";
			else if(m.getPunteggio()==4 || m.getPunteggio()==3)		return "bad";
			else if(m.getPunteggio()==2 || m.getPunteggio()==1)		return "good";
			else	return "very good";
		}
	}
	
	//=====================================================
	//       TO BE CHANGED AFTER ARTICLES READING
	//=====================================================
	//Valore compreso tra 0 e 20
	private int getIndexAmbientale_v0() {
		int index = 0;
		//
		if (user.getInteresseAmbientale()) {
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
	
	//Valore compreso tra 0 e 40
	private double getIndexAmbientalePerEmissioni() {
		double index = 0;
		double indexTemp = 0;
		int count = 0;
		for(Macronutriente m:settimana.getMacronutrienti()) {
			indexTemp = m.getPunteggioEnvironment();
			count = m.getPunteggio();
			index += indexTemp*count;
		}

		return index;
	}
	

	public void textStructuring() {
		//in questo metodo imposterò l'ordine e la struttura in base allo user model
	}
	
	private void createJsonFile(JsonObject value) {
		FileWriter file = null;
		try {
			
			String fileFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss'.json'", Locale.getDefault()).format(new Date());
			this.fileName = "src/monfroglio/elena/files/"+user.getCognome()+" "+fileFormat;
			file = new FileWriter(this.fileName);
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
