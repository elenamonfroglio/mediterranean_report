package monfroglio.elena.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

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
		JsonObject jsonObject = readJson();
		
		extractMacronutrienti(jsonObject);
		extractLingua(jsonObject);
		extractUtente(jsonObject);
		
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
		createJsonFile(value);
	}
	
	//which information to present in individual sentences 
	public void sentenceAggregation() {
		aggregatorMacronutrienti();
		
		//RIMUOVO DALL'ORDINE GLOBALE NEL CASO IN CUI AVESSI MACRONUTRIENTI VUOTI
		for(String type: order) {
			if (macronutrientiVeryGood.isEmpty()) {
				order.remove(PhraseType.VERYGOOD);
			}else if (macronutrientiGood.isEmpty() ) {
				order.remove(PhraseType.GOOD);
			}else if (macronutrientiBad.isEmpty()) {
				order.remove(PhraseType.BAD);
			}else if (macronutrientiVeryBad.isEmpty()) {
				order.remove(PhraseType.VERYBAD);
			}
		}
	}
	
	//scrive il contenuto degli oggetti nel file Json
	private void writeSentencePlan(JsonObjectBuilder builder) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for(String nomeMacronutriente: macronutrientiVeryGood) {
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		}
		builder.add("very good", arrayBuilder);
		
		for(String nomeMacronutriente: macronutrientiGood) {
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		}
		builder.add("good", arrayBuilder);
		
		for(String nomeMacronutriente: macronutrientiBad) {
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		}
		builder.add("bad", arrayBuilder);
		
		for(String nomeMacronutriente: macronutrientiVeryBad) {
			arrayBuilder = arrayBuilder.add(nomeMacronutriente);
		}
		builder.add("very bad", arrayBuilder);
		
	}
	
	private ArrayList<String> getAllMacronutrientiWithSamePoints_v1(int valuation, boolean moreIsBetter) {
		ArrayList<String> ret = new ArrayList<>();
		
		for (Macronutriente m:allMacronutrienti) {
			//la condizione in or permette di considerare i good su due valori 2 e 3 e i bad su 1 e 2
			if(m.getMoreIsBetter()==moreIsBetter) {
				if(m.getPunteggio()==valuation || m.getPunteggio()==(valuation+1)) {
					ret.add(m.getNome());
					//m.print();
				}
			}
		}
		return ret;
	}
	
	private ArrayList<String> getAllMacronutrientiWithSamePoints_v2(int valuation) {
		ArrayList<String> ret = new ArrayList<>();
		
		for (Macronutriente m:allMacronutrienti) {
			//la condizione in or permette di considerare i good su due valori 2 e 3 e i bad su 1 e 2
			if(m.getPunteggio()==valuation || m.getPunteggio()==(valuation+1))
				ret.add(m.getNome());
		}
		return ret;
	}
	
	private void extractUtente(JsonObject object) {
		nomeUtente = object.getString("nome utente");
		sessoUtente = object.getString("sesso utente");
	}
	
	private void extractLingua(JsonObject object) {
		lingua = object.getString("lingua");
	}
	
	private void extractMacronutrienti(JsonObject object) {
		JsonObject jsonObject = object.getJsonObject("Cereali");
		Macronutriente cereali = new Macronutriente("cereali",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(cereali);
		
		jsonObject = object.getJsonObject("Patate");
		Macronutriente patate = new Macronutriente("patate",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(patate);
		
		jsonObject = object.getJsonObject("Frutta");
		Macronutriente frutta = new Macronutriente("frutta",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(frutta);

		jsonObject = object.getJsonObject("Verdura");
		Macronutriente verdura = new Macronutriente("verdura",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(verdura);

		jsonObject = object.getJsonObject("Legumi");
		Macronutriente legumi = new Macronutriente("legumi",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(legumi);

		jsonObject = object.getJsonObject("Pesce");
		Macronutriente pesce = new Macronutriente("pesce",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(pesce);

		jsonObject = object.getJsonObject("UsoOlioOliva");
		Macronutriente usoOlioOliva = new Macronutriente("olio",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(usoOlioOliva);
		
		jsonObject = object.getJsonObject("CarneRossa");
		Macronutriente carneRossa = new Macronutriente("carne rossa",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(carneRossa);

		jsonObject = object.getJsonObject("Pollame");
		Macronutriente pollame = new Macronutriente("pollame",
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(pollame);

		jsonObject = object.getJsonObject("Latticini");
		Macronutriente latticini = new Macronutriente("latticini",
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
	
	private void lexicalisation_caseA() {
		//ArrayList<String> macronutrientiVeryGoodMoreIsBetter
		
		Phrase phraseVeryGood = null;
		Phrase phraseGood = null;
		Phrase phraseBad = null;
		Phrase phraseVeryBad = null;

		//HAI FATTO UN OTTIMO LAVORO CON XXX
		if(!macronutrientiVeryGood.isEmpty()) {
			ArrayList<String> subject = new ArrayList<>();
			subject.add("tu");
			String verb = "fare";
			ArrayList<String> object = new ArrayList<String>();
			object.add("lavoro");
			phraseVeryGood = new Phrase(PhraseType.VERYGOOD, subject, verb, object, macronutrientiVeryGood);
			phraseVeryGood.setTense(Tense.PAST);
			phraseVeryGood.setForm(Form.NORMAL);
			phraseVeryGood.setPerfect(true);
			ArrayList<String> adjp = new ArrayList<String>();
			adjp.add("ottimo");
			phraseVeryGood.setAdjp(adjp);
			phraseVeryGood.setActive(true);
			phraseVeryGood.setNegative(false);
			phrases.add(phraseVeryGood);
		}
		
		//HAI FATTO UN BUON LAVORO CON XXX
		if(!macronutrientiGood.isEmpty()) {
			ArrayList<String> subject = new ArrayList<>();
			subject.add("la");
			subject.add("quantità");
			String verb = "essere";
			ArrayList<String> object = new ArrayList<String>();
			object.add("perfetta");
			phraseGood = new Phrase(PhraseType.GOOD, subject, verb, object, macronutrientiGood);
			phraseGood.setTense(Tense.PAST);
			phraseGood.setForm(Form.NORMAL);
			phraseGood.setPerfect(false);
			ArrayList<String> adjp = new ArrayList<String>();
			//adjp.add("");
			phraseGood.setAdjp(adjp);
			phraseGood.setActive(true);
			phraseGood.setNegative(false);
			phrases.add(phraseGood);
		}

		//NON HAI FATTO UN BUON LAVORO CON XXX
		if(!macronutrientiBad.isEmpty()) {
			ArrayList<String> subject = new ArrayList<>();
			subject.add("tu");
			String verb = "fare";
			ArrayList<String> object = new ArrayList<String>();
			object.add("lavoro");
			phraseBad = new Phrase(PhraseType.BAD, subject, verb, object, macronutrientiBad);
			phraseBad.setTense(Tense.PAST);
			phraseBad.setForm(Form.NORMAL);
			phraseBad.setPerfect(true);
			ArrayList<String> adjp = new ArrayList<String>();
			adjp.add("buono");
			phraseBad.setAdjp(adjp);
			phraseBad.setActive(true);
			phraseBad.setNegative(true);
			phrases.add(phraseBad);
		}

		//NON HAI FATTO UN OTTIMO LAVORO CON XXX
		if(!macronutrientiVeryBad.isEmpty()) {
			ArrayList<String> subject = new ArrayList<>();
			subject.add("tu");
			String verb = "fare";
			ArrayList<String> object = new ArrayList<String>();
			object.add("lavoro");
			phraseVeryBad = new Phrase(PhraseType.VERYBAD, subject, verb, object, macronutrientiVeryBad);
			phraseVeryBad.setTense(Tense.PAST);
			phraseVeryBad.setForm(Form.NORMAL);
			phraseVeryBad.setPerfect(true);
			ArrayList<String> adjp = new ArrayList<String>();
			adjp.add("ottimo");
			phraseVeryBad.setAdjp(adjp);
			phraseVeryBad.setActive(true);
			phraseVeryBad.setNegative(true);
			phrases.add(phraseVeryBad);
		}
		
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
	
	private void createJsonFile(JsonObject value) {
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
	}
	
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
