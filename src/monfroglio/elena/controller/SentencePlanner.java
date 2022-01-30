package monfroglio.elena.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;

import monfroglio.elena.model.ArgType;
import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.Phrase;
import monfroglio.elena.model.PhraseType;
import simplenlg.features.Form;
import simplenlg.features.Gender;
import simplenlg.features.Tense;

public class SentencePlanner {
	private ArrayList<Macronutriente> allMacronutrienti;
	private String fileName;
	private String lingua;
	private String nomeUtente;
	private String sessoUtente;
	private int indiceMed;
	private int etaUtente;
	private HashMap<String, ArrayList<String>> dictionary = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> order;
	private ArrayList<String> macronutrientiVeryGood;
	private ArrayList<String> macronutrientiGood;
	private ArrayList<String> macronutrientiBad;
	private ArrayList<String> macronutrientiVeryBad;
	public ArrayList<Phrase> phrases;
	

	public SentencePlanner(String filename, ArrayList<String> order) {
		this.fileName = filename;
		this.order = order;
		allMacronutrienti = new ArrayList<>();
		phrases = new ArrayList<Phrase>();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	private void aggregatorMacronutrienti() {
		
		macronutrientiVeryGood = getAllMacronutrientiWithSamePoints_v2(5);
		macronutrientiGood = getAllMacronutrientiWithSamePoints_v2(3);
		macronutrientiBad = getAllMacronutrientiWithSamePoints_v2(1);
		macronutrientiVeryBad = getAllMacronutrientiWithSamePoints_v2(-1);
		
		JsonObjectBuilder builder = Json.createObjectBuilder()
				.add("lingua", lingua)
				.add("nome utente", nomeUtente)
				.add("sesso utente", sessoUtente);
		
		writeSentencePlan(builder);
		
		JsonObject value = builder.build();
		//createJsonFile(value);
	}
	
	private void loadDictionary() {
		String dictFile = "src/monfroglio/elena/files/dictionaries/ItalianDictionary.json";
		if(lingua.equals("english")){
			dictFile = "src/monfroglio/elena/files/dictionaries/EnglishDictionary.json";
		}
		File initialFile = new File(dictFile);
	    InputStream targetStream;
	    JsonObject object = null;
		try {
			targetStream = new FileInputStream(initialFile);

			JsonReaderFactory factory = Json.createReaderFactory(null);
			JsonReader reader = factory.createReader(targetStream);
			object = reader.readObject();
            reader.close();
            
            Set<String> concepts = object.keySet();
            ArrayList<String> words = new ArrayList<>();

            for(String concept:concepts) {
            	JsonArray temp = object.getJsonArray(concept);
            	 if (temp != null) {   
                     //Iterating JSON array  
                     for (int i=0;i<temp.size();i++){   
                         //Adding each element of JSON array into ArrayList  
                    	 words.add(temp.getString(i));  
                     }   
                 } 
            	 dictionary.put(concept, words);
            	 words = new ArrayList<>();
            }
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//which information to present in individual sentences 
	public void sentenceAggregation() {

		JsonObject jsonObject = readJson();
		
		extractMacronutrienti(jsonObject);
		extractLingua(jsonObject);
		extractUtente(jsonObject);
		
		loadDictionary();
		aggregatorMacronutrienti();
		//RIMUOVO DALL'ORDINE GLOBALE NEL CASO IN CUI AVESSI MACRONUTRIENTI VUOTI
		for(String type: order) {
			if (macronutrientiVeryGood.isEmpty())	order.remove(PhraseType.VERYGOOD);
			else if (macronutrientiGood.isEmpty()) 	order.remove(PhraseType.GOOD);
			else if (macronutrientiBad.isEmpty()) 		order.remove(PhraseType.BAD);
			else if (macronutrientiVeryBad.isEmpty()) 		order.remove(PhraseType.VERYBAD);
		}
	}
	
	//scrive il contenuto degli oggetti nel file Json
	private void writeSentencePlan(JsonObjectBuilder builder) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for(String nomeMacronutriente: macronutrientiVeryGood) 
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		builder.add("very good", arrayBuilder);
		
		for(String nomeMacronutriente: macronutrientiGood) 
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		builder.add("good", arrayBuilder);
		
		for(String nomeMacronutriente: macronutrientiBad) 
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		builder.add("bad", arrayBuilder);
		
		for(String nomeMacronutriente: macronutrientiVeryBad) 
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		builder.add("very bad", arrayBuilder);
		
	}
	
	
	private ArrayList<String> getAllMacronutrientiWithSamePoints_v2(int valuation) {
		ArrayList<String> ret = new ArrayList<>();
		
		for (Macronutriente m:allMacronutrienti) 
			//la condizione in or permette di considerare i good su due valori 2 e 3 e i bad su 1 e 2
			if(m.getPunteggio()==valuation || m.getPunteggio()==(valuation+1))
				ret.add(getWord(m.getNome()));
		
		return ret;
	}
	
	private void extractUtente(JsonObject object) {
		nomeUtente = object.getString("nome utente");
		sessoUtente = object.getString("sesso utente");
		etaUtente = (object.getInt("eta utente"));
	}
	
	private void extractLingua(JsonObject object) {
		lingua = object.getString("lingua");
	}
	
	private void extractMacronutrienti(JsonObject object) {
		indiceMed = object.getInt("indice Med");
		JsonObject jsonObject = object.getJsonObject("Cereali");
		Macronutriente cereali = new Macronutriente("cer",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(cereali);
		
		jsonObject = object.getJsonObject("Patate");
		Macronutriente patate = new Macronutriente("pot",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(patate);
		
		jsonObject = object.getJsonObject("Frutta");
		Macronutriente frutta = new Macronutriente("fru",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(frutta);

		jsonObject = object.getJsonObject("Verdura");
		Macronutriente verdura = new Macronutriente("veg",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(verdura);

		jsonObject = object.getJsonObject("Legumi");
		Macronutriente legumi = new Macronutriente("leg",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(legumi);

		jsonObject = object.getJsonObject("Pesce");
		Macronutriente pesce = new Macronutriente("fish",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(pesce);

		jsonObject = object.getJsonObject("UsoOlioOliva");
		Macronutriente usoOlioOliva = new Macronutriente("oil",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(usoOlioOliva);
		
		jsonObject = object.getJsonObject("CarneRossa");
		Macronutriente carneRossa = new Macronutriente("rmeat",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(carneRossa);

		jsonObject = object.getJsonObject("Pollame");
		Macronutriente pollame = new Macronutriente("poul",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(pollame);

		jsonObject = object.getJsonObject("Latticini");
		Macronutriente latticini = new Macronutriente("ffdp",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(latticini);
	}
	
	//finding the right words and phrases to express information --> senticnet
	public void lexicalisation() {
		lexicalisation_caseA();
		//lexicalisation_caseB();
	}
	
	private String getWord(String concept) {
		return dictionary.get(concept).get(0);
	}
	
	private void lexicalisation_caseA() {
		//ArrayList<String> macronutrientiVeryGoodMoreIsBetter
		String oldItem = "";
		int iter = 0;
		lexicaliseWelcome();
		for(String item: order) {
			switch(item) {
				case PhraseType.VERYBAD:
					if(!macronutrientiVeryBad.isEmpty()) 
						if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							lexicaliseVeryBad(getWord("but"));
						else if(oldItem!="") 	lexicaliseVeryBad(getWord("and"));
						else lexicaliseVeryBad("");
					break;
				case PhraseType.BAD:
					if(!macronutrientiVeryBad.isEmpty()) 
						if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							lexicaliseBad(getWord("but"));
						else if(oldItem!="") 	lexicaliseBad(getWord("and"));
						else lexicaliseBad("");
					break;
				case PhraseType.VERYGOOD:
					if(!macronutrientiVeryBad.isEmpty()) 
						if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							lexicaliseVeryGood(getWord("but"));
						else if(oldItem!="") 	lexicaliseVeryGood(getWord("and"));
						else lexicaliseVeryGood("");
					break;
				case PhraseType.GOOD:
					if(!macronutrientiVeryBad.isEmpty()) 
						if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							lexicaliseGood(getWord("but"));
						else if(oldItem!="") 	lexicaliseGood(getWord("and"));
						else lexicaliseGood("");
					break;
			}
			iter++;
			if(iter==2) {
				oldItem="";
				iter = 0;
			}else {
				oldItem = item;
			}
		}
		
	}
	
	private void lexicaliseWelcome() {
		String saluto = getWord("good-evening");
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		if (etaUtente<18) {
			saluto = getWord("hello");
		}else if(timeOfDay >= 0 && timeOfDay < 12) {
			saluto = getWord("good-morning");
		}else if(timeOfDay >= 12 && timeOfDay < 16) {
			saluto = getWord("good-afternoon");
		}
		ArrayList<String> sub = new ArrayList<>();
		sub.add(nomeUtente);
		
		Phrase phraseWelcome1 = new Phrase(PhraseType.WELCOME,sub,"",new ArrayList<String>(),new ArrayList<String>());
		
		phraseWelcome1.setPreModifierPhrase(saluto);
		phraseWelcome1.setModal("");
		phraseWelcome1.setPostModifierSubject("");
		phraseWelcome1.setActive(true);
		phraseWelcome1.setNegative(false);
		phraseWelcome1.setSubjectArgs(new ArrayList<>());
		phrases.add(phraseWelcome1);
		
		if(indiceMed<12 ) {
			
		}
		sub = new ArrayList<>();
		sub.add(getWord("you"));
		ArrayList<String> obj = new ArrayList<>();
		obj.add(getWord("mscore"));
		Phrase phraseWelcome2 = new Phrase(PhraseType.WELCOME,sub,getWord("to-obtain"),obj,new ArrayList<String>());
		phraseWelcome2.setPreModifierPhrase(getWord("this-week"));
		phraseWelcome2.setForm(Form.NORMAL);
		phraseWelcome2.setTense(Tense.PAST);
		phraseWelcome2.setModal("");
		phraseWelcome2.setPerfect(true);
		phraseWelcome2.setPostModifierSubject("");
		phraseWelcome2.setActive(true);
		phraseWelcome2.setNegative(false);
		phraseWelcome2.setAdjp(new ArrayList<String>());
		phraseWelcome2.setSubjectArgs(new ArrayList<>());
		phrases.add(phraseWelcome2);
	}
	
	private void lexicaliseVeryGood(String connection) {
		Phrase phraseVeryGood;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("you"));
		String verb = getWord("to-do");
		ArrayList<String> object = new ArrayList<String>();
		object.add(getWord("job"));
		phraseVeryGood = new Phrase(PhraseType.VERYGOOD, subject, verb, object, macronutrientiVeryGood);
		phraseVeryGood.setModal("");
		phraseVeryGood.setSubjectArgs(new ArrayList<>());
		phraseVeryGood.setTense(Tense.PAST);
		phraseVeryGood.setForm(Form.NORMAL);
		phraseVeryGood.setPerfect(true);
		phraseVeryGood.setPostModifierSubject("");
		phraseVeryGood.setPreModifierPhrase(connection);
		ArrayList<String> adjp = new ArrayList<String>();
		adjp.add(getWord("very-good"));
		phraseVeryGood.setAdjp(adjp);
		phraseVeryGood.setActive(true);
		phraseVeryGood.setNegative(false);
		phraseVeryGood.setPostModifierPhrase(getWord("with"));
		phrases.add(phraseVeryGood);
	}
	
	private void lexicaliseGood(String connection) {
		Phrase phraseGood;
		ArrayList<String> subject = new ArrayList<>();
		subject.add("la");
		subject.add(getWord("portion"));
		String verb = getWord("to-be");
		ArrayList<String> object = new ArrayList<String>();
		object.add(getWord("very-good"));
		phraseGood = new Phrase(PhraseType.GOOD, subject, verb, object, new ArrayList<>());
		phraseGood.setModal("");
		phraseGood.setSubjectArgs(macronutrientiGood);
		phraseGood.setTense(Tense.PAST);
		phraseGood.setForm(Form.NORMAL);
		phraseGood.setPerfect(false);
		phraseGood.setPreModifierPhrase(connection);
		phraseGood.setPostModifierSubject(getWord("of"));
		ArrayList<String> adjp = new ArrayList<String>();
		adjp.add(getWord("nearly"));
		adjp.add(getWord("very-good"));
		phraseGood.setAdjp(adjp);
		phraseGood.setActive(true);
		phraseGood.setNegative(false);
		phraseGood.setPostModifierPhrase(getWord("of"));
		phrases.add(phraseGood);
	}
	
	private void lexicaliseBad(String connection) {
		Phrase phraseBad;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("you"));
		String modal = getWord("to-can");
		String verb = getWord("to-improve");
		ArrayList<String> object = new ArrayList<String>();
		phraseBad = new Phrase(PhraseType.BAD, subject, verb, object, macronutrientiBad);
		phraseBad.setModal(modal);
		phraseBad.setSubjectArgs(new ArrayList<>());
		phraseBad.setTense(Tense.PRESENT);
		phraseBad.setForm(Form.NORMAL);
		phraseBad.setPerfect(false);
		phraseBad.setPreModifierPhrase(connection);
		phraseBad.setPostModifierSubject("");
		ArrayList<String> adjp = new ArrayList<String>();
		adjp.add(getWord("good"));
		phraseBad.setAdjp(adjp);
		phraseBad.setActive(true);
		phraseBad.setNegative(false);
		phraseBad.setPostModifierPhrase(getWord("with"));
		phrases.add(phraseBad);
	}

	private void lexicaliseVeryBad(String connection) {
		Phrase phraseVeryBad;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("you"));
		String verb = getWord("to-eat");
		ArrayList<String> object = new ArrayList<String>();
		ArrayList<String> adjp = new ArrayList<String>();
		for (String m: macronutrientiVeryBad) {
			object.add(m);
			if(Macronutriente.isMoreBetter(m))	adjp.add(getWord("more"));
			else adjp.add(getWord("less"));
		}
		phraseVeryBad = new Phrase(PhraseType.VERYBAD, subject, verb, object, new ArrayList<>());
		phraseVeryBad.setTense(Tense.PRESENT);
		phraseVeryBad.setModal(getWord("to-must"));
		phraseVeryBad.setSubjectArgs(new ArrayList<>());
		phraseVeryBad.setForm(Form.NORMAL);
		phraseVeryBad.setPostModifierSubject("");
		phraseVeryBad.setPerfect(false);
		phraseVeryBad.setPreModifierPhrase(connection);
		phraseVeryBad.setAdjp(adjp);
		phraseVeryBad.setActive(true);
		phraseVeryBad.setNegative(false);
		phraseVeryBad.setPostModifierPhrase("");
		phrases.add(phraseVeryBad);
	}
	
	private JsonObject readJson() {
		File initialFile = new File(fileName);
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
	
	
	/*private void createJsonFile(JsonObject value) {
		FileWriter file = null;
		try {
			
			String fileFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss 'SentencePlan.json'", Locale.getDefault()).format(new Date());
			this.fileName = "src/monfroglio/elena/files/ "+fileFormat;
			file = new FileWriter(this.fileName);
			file.write(value.toString());

            file.flush();
            file.close();
		} catch (IOException e) {
            e.printStackTrace(); 
		}
            
        
	}*/
	
	private String getArticle(String macronutriente) {
		String ret = "la";
		if(getGender(macronutriente)==Gender.MASCULINE)	ret = "il";
		return ret;
	}
	
	private Gender getGender(String macronutriente) {
		Gender ret = Gender.MASCULINE;
		if(macronutriente.equals("patate") || macronutriente.equals("carne rossa") || macronutriente.equals("frutta") 
				|| macronutriente.equals("verdura")) 
			ret = Gender.FEMININE;
		return ret;
	}
}
