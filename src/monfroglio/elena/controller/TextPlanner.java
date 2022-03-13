package monfroglio.elena.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.Pasto;
import monfroglio.elena.model.PhraseType;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;
import javax.json.*;


public class TextPlanner {
	private String lingua;
	private String fileName;
	private Utente user;
	private Settimana settimana;
	private Settimana lastSettimana;
	private ArrayList<String> order;
	
	public TextPlanner() {
		lingua = "italiano";
	}
	
	public TextPlanner(String lingua) {
		this.lingua = lingua;
	}

	public TextPlanner(String lingua, Utente user, Settimana settimana, Settimana lastSettimana) {
		this.lingua = lingua;
		this.user = user;
		this.settimana = settimana;
		this.lastSettimana = lastSettimana;
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
				.add("eta utente", user.getEta())
				.add("indice Med", settimana.getIndiceMed())
				.add("last indice Med", lastSettimana.getIndiceMed())
				.add("sesso utente", user.getSesso())
				.add("conoscenza dominio", user.getConoscenzaDominio());
		
		int k = 0;
		for (Pasto p: settimana.getPasti()) {
			int dayOfWeek = getOfWeek(p.getGiorno());
		}
		
		for (Macronutriente m:settimana.getMacronutrienti()) {
			builder.add(m.getNome(), Json.createObjectBuilder()
					.add("punteggio", m.getPunteggio())
					.add("punteggioEnvironment", Double.toString(m.getPunteggioEnvironment()))
					.add("moreIsBetter", m.getMoreIsBetter()));
		}		
		if(user.getInteresseAmbientale())
			setIndexAmbientalePerEmissioni(builder);
			//builder.add("totalePunteggioEnvironment", getIndexAmbientalePerEmissioni());
		else 	builder.add("totalePunteggioEnvironment", -1);
		JsonObject value = builder.build();
		createJsonFile(value);	    
	}
	
	private int getOfWeek(Date date) {

		String dayWeek;
		Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
	    return dayOfWeek;
	}
		
	//Valore compreso tra 0 e 976
	private double setIndexAmbientalePerEmissioni(JsonObjectBuilder builder) {
		double index = 0;
		double indexTemp = 0;
		double maxIntake = 0;
		Macronutriente maxIntakeMacronutriente = settimana.getMacronutrienti().get(0);
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
			double intake = count*indexTemp;
			if(intake>maxIntake) {
				maxIntake = intake;
				maxIntakeMacronutriente = m;
			}
			index += intake;
		}
		System.out.println("max intake: "+maxIntake+"\n--Macronutriente with max Intake-- \n"+maxIntakeMacronutriente.toString());
		
		builder.add("totalePunteggioEnvironment", index);
		builder.add("badMacronutrienteEnvironment", maxIntakeMacronutriente.getNome());
		
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
