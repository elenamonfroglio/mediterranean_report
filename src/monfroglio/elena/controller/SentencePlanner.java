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
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
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
import monfroglio.elena.model.Concept;
import monfroglio.elena.model.Emotion;
import monfroglio.elena.model.EmotionName;
import monfroglio.elena.model.EmotionType;
import monfroglio.elena.model.Macronutriente;
import monfroglio.elena.model.MacronutrienteType;
import monfroglio.elena.model.Pasto;
import monfroglio.elena.model.Phrase;
import monfroglio.elena.model.PhraseType;
import monfroglio.elena.model.Settimana;
import simplenlg.features.Form;
import simplenlg.features.Gender;
import simplenlg.features.Tense;

public class SentencePlanner {
	private ArrayList<Macronutriente> allMacronutrienti;
	private String fileName;
	private String lingua;
	private String nomeUtente;
	private String sessoUtente;
	private Settimana thisWeek;
	private Pasto veryGoodPasto;
	private Pasto veryBadPasto;
	private String badMacronutrienteEnvironment;
	private int indiceMed;
	private int lastIndiceMed;
	private int etaUtente;
	private int conoscenzaDominio;
	private int stressUtente;
	private double totalePunteggioEnvironment;
	private HashMap<String, ArrayList<String>> dictionary = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> order;
	private ArrayList<String> macronutrientiVeryGood;
	private ArrayList<String> macronutrientiGood;
	private ArrayList<String> macronutrientiBad;
	private ArrayList<String> macronutrientiVeryBad;
	public ArrayList<Phrase> phrases;
	private Phrase temp;
	private SenticnetManager sm;
	

	public SentencePlanner(String filename, ArrayList<String> order, Settimana thisWeek) {
		this.fileName = filename;
		this.order = order;
		this.thisWeek = thisWeek;
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
		loadSenticnet();
		aggregatorMacronutrienti();
		extractBestMeal();
		extractWorstMeal();
		ArrayList<String> orderTemp = (ArrayList<String>) order.clone();
		//RIMUOVO DALL'ORDINE GLOBALE NEL CASO IN CUI AVESSI MACRONUTRIENTI VUOTI
		for(String m: orderTemp) {
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
				ret.add(m.getNome());
				//ret.add(getWord(m.getNome()));
		
		return ret;
	}
	
	public void extractBestMeal() {
		veryGoodPasto = thisWeek.getPastoWithGoodMacrosPareto(macronutrientiVeryGood,dictionary);
		//veryGoodPasto.print();	
	}
	
	public void extractWorstMeal() {
		veryBadPasto = thisWeek.getPastoWithWorstMacrosPareto(macronutrientiVeryBad,dictionary);
		//veryBadPasto.print();	
	}
	
	private void extractUtente(JsonObject object) {
		nomeUtente = object.getString("nome utente");
		sessoUtente = object.getString("sesso utente");
		etaUtente = object.getInt("eta utente");
		stressUtente = object.getInt("stress utente");
		conoscenzaDominio = object.getInt("conoscenza dominio");
	}
	
	private void extractLingua(JsonObject object) {
		lingua = object.getString("lingua");
	}
	
	private void extractMacronutrienti(JsonObject object) {
		indiceMed = object.getInt("indice Med");
		lastIndiceMed = object.getInt("last indice Med");
		totalePunteggioEnvironment = object.getInt("totalePunteggioEnvironment");
		if(totalePunteggioEnvironment!=-1)	badMacronutrienteEnvironment = object.getString("badMacronutrienteEnvironment");
		//System.out.println(totalePunteggioEnvironment);
		JsonObject jsonObject = object.getJsonObject(MacronutrienteType.CEREALI);
		Macronutriente cereali = new Macronutriente(MacronutrienteType.CEREALI,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(cereali);
		
		jsonObject = object.getJsonObject(MacronutrienteType.PATATE);
		Macronutriente patate = new Macronutriente(MacronutrienteType.PATATE,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(patate);
		
		jsonObject = object.getJsonObject(MacronutrienteType.FRUTTA);
		Macronutriente frutta = new Macronutriente(MacronutrienteType.FRUTTA,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(frutta);

		jsonObject = object.getJsonObject(MacronutrienteType.VERDURA);
		Macronutriente verdura = new Macronutriente(MacronutrienteType.VERDURA,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(verdura);

		jsonObject = object.getJsonObject(MacronutrienteType.LEGUMI);
		Macronutriente legumi = new Macronutriente(MacronutrienteType.LEGUMI,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(legumi);

		jsonObject = object.getJsonObject(MacronutrienteType.PESCE);
		Macronutriente pesce = new Macronutriente(MacronutrienteType.PESCE,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(pesce);

		jsonObject = object.getJsonObject(MacronutrienteType.USOOLIOOLIVA);
		Macronutriente usoOlioOliva = new Macronutriente(MacronutrienteType.USOOLIOOLIVA,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(usoOlioOliva);
		
		jsonObject = object.getJsonObject(MacronutrienteType.CARNEROSSA);
		Macronutriente carneRossa = new Macronutriente(MacronutrienteType.CARNEROSSA,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(carneRossa);

		jsonObject = object.getJsonObject(MacronutrienteType.POLLAME);
		Macronutriente pollame = new Macronutriente(MacronutrienteType.POLLAME,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(pollame);

		jsonObject = object.getJsonObject(MacronutrienteType.LATTICINI);
		Macronutriente latticini = new Macronutriente(MacronutrienteType.LATTICINI,
				jsonObject.getInt("punteggio"),
				Double.parseDouble(jsonObject.getString("punteggioEnvironment")),
				jsonObject.getBoolean("moreIsBetter"));
		allMacronutrienti.add(latticini);
	}
	
	private void loadSenticnet() {
		sm = new SenticnetManager("italiano");
		sm.readCSV();
	}
	
	private String getFirstWord(String conceptToFind) {
		Concept ret = null;
		ArrayList<String> wordsToFind = dictionary.get(conceptToFind);
		return wordsToFind.get(0).replace("_", " ");
	}
	
	private String getRandomWord(String conceptToFind) {
		Concept ret = null;
		ArrayList<String> wordsToFind = dictionary.get(conceptToFind);
		
		int size = wordsToFind.size();
		
		int index = (int) ((Math.random() * (size - 0)) + 0);
		
		return wordsToFind.get(index).replace("_", " ");
	}
	
	private String getMostIntenseWord(String conceptToFind) {
		Concept ret = null;
		ArrayList<String> wordsToFind = dictionary.get(conceptToFind);
		if(wordsToFind.size()==1) return wordsToFind.get(0).replace("_", " ");
		ArrayList<Concept> conceptNet = sm.getConcepts();
		ArrayList<String> wordNet = sm.getWords();
		int maxIntensity = -10;
		int currentIntensity = 0;
		for(String w: wordsToFind) {
			int index = wordNet.indexOf(w);
			if(index!=-1) {
				currentIntensity = conceptNet.get(index).getPrimaryEmotion().getIntesity();
				if(currentIntensity>maxIntensity) {
					maxIntensity = currentIntensity;
					ret = conceptNet.get(index);
				}
			}
		}
		if(ret!=null)		return ret.getTitle().replace("_", " ");
		else				return wordsToFind.get(0).replace("_", " ");
	}
	
	private String getBestWord(String conceptToFind, String eType) {
		Concept ret = null;
		ArrayList<String> wordsToFind = dictionary.get(conceptToFind);
		if(wordsToFind.size()==1) return wordsToFind.get(0).replace("_", " ");
		ArrayList<Concept> conceptNet = sm.getConcepts();
		ArrayList<String> wordNet = sm.getWords();
		int maxIntensity = -10;
		int currentIntensity = 0;
		for(String w: wordsToFind) {
			int index = wordNet.indexOf(w);
			if(index!=-1) {
				Concept currentConcept = conceptNet.get(index);
				currentIntensity = conceptNet.get(index).getPrimaryEmotion().getIntesity();
				if(currentIntensity>maxIntensity && currentConcept.hasPrimaryEmotionType(eType)) {
					maxIntensity = currentIntensity;
					ret = conceptNet.get(index);
				}
			}
		}
		if(ret!=null)		return ret.getTitle().replace("_", " ");
		else				return wordsToFind.get(0).replace("_", " ");
	}
	
	
	private String getWord(String concept) {
		String emotionType = "";
		switch(stressUtente) {
			case 0:
				emotionType = EmotionType.SENSITIVITY;
			break;
			case 1:
				emotionType = EmotionType.INTROSPECTION;
			break;
			case 2:
				emotionType = EmotionType.ATTITUDE;
			break;
			case 3:
				emotionType = EmotionType.TEMPER;
			break;
		}
		//in this method I will choose the euristic based on the user model
		//return getMostIntenseWord(concept);
		//return getRandomWord(concept);
		//return getFirstWord(concept);
		return getBestWord(concept, emotionType);
	}
	
	public void lexicalisation() {
		//ArrayList<String> macronutrientiVeryGoodMoreIsBetter		
		String oldItem = "";
		int iter = 0;
		lexicaliseWelcome();
		Phrase phrase = null;
		boolean first = true;
		for(String item: order) {
			switch(item) {
				case PhraseType.VERYBAD:
					if(!macronutrientiVeryBad.isEmpty()) {
						if(isDietician())	phrase = lexicaliseVeryBadDietician("");
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="" && !first)
							phrase = lexicaliseVeryBad(getWord("but"));
						else if(oldItem!="" && !first) 	phrase = lexicaliseVeryBad(getWord("and"));
						else phrase = lexicaliseVeryBad("");
						if(stressUtente==0 || isDietician())	{
							if(phrase!=null) phrases.add(phrase);
							first = false;
							iter++;
							if(iter==2) {
								oldItem="";
								iter = 0;
							}else {
								oldItem = item;
							}
						}
					}
					break;
				case PhraseType.BAD:
					if(!macronutrientiBad.isEmpty()) {
						if(isDietician())	phrase = lexicaliseBadDietician("");
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="" && !first)
							phrase = lexicaliseBad(getWord("but"));
						else if(oldItem!="" && !first) 	phrase = lexicaliseBad(getWord("and"));
						else phrase = lexicaliseBad("");
						if((stressUtente!=3 && stressUtente!=2) || isDietician())	{
							if(phrase!=null) phrases.add(phrase);
							first = false;
							iter++;
							if(iter==2) {
								oldItem="";
								iter = 0;
							}else {
								oldItem = item;
							}
						}
					}
					break;
				case PhraseType.VERYGOOD:
					if(!macronutrientiVeryGood.isEmpty()) {
						if(isDietician())	break;
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="" && !first)
							phrase = lexicaliseVeryGood(getWord("but"));
						else if(oldItem!="" && !first) 	phrase = lexicaliseVeryGood(getWord("and"));
						else phrase = lexicaliseVeryGood("");
						if(stressUtente!=3 || isDietician()){
							if(phrase!=null) phrases.add(phrase);
							first = false;
							iter++;
							if(iter==2) {
								oldItem="";
								iter = 0;
							}else {
								oldItem = item;
							}
						}
					}
					break;
				case PhraseType.GOOD:
					if(!macronutrientiGood.isEmpty()) {  
						if(isDietician())	break;
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="" && !first)
							phrase = lexicaliseGood(getWord("but"));
						else if(oldItem!="" && !first) 	phrase = lexicaliseGood(getWord("and"));
						else phrase = lexicaliseGood("");
						if(stressUtente!=3 || isDietician()) {
							if(phrase!=null) phrases.add(phrase);
							first = false;
							iter++;
							if(iter==2) {
								oldItem="";
								iter = 0;
							}else {
								oldItem = item;
							}
						}
					}
					break;
			}
		}
		if(!isDietician()) {
			Phrase veryGoodPastoPhrase = lexicaliseVeryGoodPasto("");
			phrases.add(veryGoodPastoPhrase);
	
			Phrase veryBadPastoPhrase = lexicaliseVeryBadPasto(getWord("while"));
			phrases.add(veryBadPastoPhrase);
			
		}
		if(totalePunteggioEnvironment!=-1) {
			phrase = lexicaliseEnvironment();
			phrases.add(phrase);
		}
		
		if(!isDietician()) {
			Random r = new Random();
			int low = 1;
			int high = 5;
			int index = r.nextInt(high-low) + low;
			
			Phrase generalKnowledgePhrase = lexicaliseGeneralKonwledge(index);
			phrases.add(generalKnowledgePhrase);
		}
		
		if(etaUtente>18 && lingua.equals("italiano"))	
			setFormalPhrases();
	}
	
	private void setFormalPhrases() {
		for (Phrase p:phrases) {
			p.setFormal(true);
			if(p.getCoordinatedPhrase()!=null)	
				p.getCoordinatedPhrase().setFormal(true);		
			if(p.getRelativeSubjectPhrase()!=null)	
				p.getRelativeSubjectPhrase().setFormal(true);	
			if(p.getRelativeObjectPhrase()!=null)	
				p.getRelativeObjectPhrase().setFormal(true);				
		}
	}
	
	private Phrase lexicaliseGeneralKonwledge(int index) {
		
		Phrase p1 = null;
		Phrase p2 = null;
		ArrayList<String> subject1 = new ArrayList<>();
		ArrayList<String> subject2 = new ArrayList<>();
		ArrayList<String> object1 = new ArrayList<>();
		ArrayList<String> object2 = new ArrayList<>();
		String connection = "";
		String verb = "";
		String subjectArticle = "";
		String objectArticle = "";
		String adjpSubject = "";
		String postModifierObject1 = "";
		String postModifierObject2 = "";
		String postmodifierSubject = "";
		ArrayList<String> preModifierObject = new ArrayList<>();
		ArrayList<String> args1 = new ArrayList<>();
		ArrayList<String> args2 = new ArrayList<>();
		ArrayList<String> objectAdjp = new ArrayList<>();
		Gender subjectGender = Gender.MASCULINE;
		
		switch(index) {
			case 1:
				subject1.add(getWord("diet"));
				adjpSubject = getWord("very-bad");
				subjectArticle = getWord("a");
				verb = getWord("to-kill");
				preModifierObject.add(getWord("more-than"));
				object1.add(getWord("smoke"));
				subjectGender = Gender.FEMININE;
			break;
			case 2:
				subject1.add(getWord("diet"));
				subjectGender = Gender.FEMININE;
				adjpSubject = getWord("good");
				subjectArticle = getWord("a");
				if(lingua.equals("italiano"))	verb = getWord("to-lead");
				else	verb = getWord("to-bring");
				object1.add(getWord("quality"));
				objectArticle = getWord("a");
				objectAdjp.add(getWord("better"));
				args1.add(getWord("sleep"));
				postModifierObject1 = getWord("of");
			break;
			case 3:
				subject1.add(getWord("med-diet"));
				//postmodifierSubject = getWord("mediterranean");
				subjectGender = Gender.FEMININE;
				subjectArticle = getWord("the");
				verb = getWord("to-reduce");
				object1.add(getWord("risk"));
				objectArticle = getWord("the");
				args1.add(getWord("cancer"));
				postModifierObject1 = getWord("of");
			break;
			case 4:
				connection = getWord("if");
				if(lingua.equals("english"))	subject1.add(getWord("you"));
				subjectGender = Gender.FEMININE;
				objectAdjp.add(getWord("mediterranean"));
				verb = getWord("to-follow");
				object1.add(getWord("diet"));
				objectArticle = getWord("the");
				postModifierObject1 = getWord("of");
				if(lingua.equals("english"))	subject2.add(getWord("you"));
				object2.add(getWord("risk"));
				args2.add(getWord("cardiovascular-disease"));
				postModifierObject2 = getWord("cardiovascular-disease");
				p2 = new Phrase(PhraseType.KNOWLEDGE,subject2,getWord("to-reduce"),object2);
				p2.setObjectArticle(getWord("the"));
				if(lingua.equals("italiano"))	p2.setTense(Tense.FUTURE);
				p2.setPostModifierPhrase(getWord("of"));
				p2.setPhraseArgs(args2);
				//p2.setPostModifierPhrase(postModifierObject2);
			break;
			case 5:
				subject1.add(getWord("med-diet"));
				//postmodifierSubject = getWord("mediterranean");
				subjectGender = Gender.FEMININE;
				subjectArticle = getWord("the");
				verb = getWord("to-reduce");
				object1.add(getWord("risk"));
				objectArticle = getWord("the");
				args1.add(getWord("stroke"));
				postModifierObject1 = getWord("of");
			break;
		}
		p1 = new Phrase(PhraseType.KNOWLEDGE,subject1,verb,object1);
		p1.setPreModifierPhrase(connection);
		p1.setSubjectAdjp(adjpSubject);
		p1.setPostModifierSubject(postmodifierSubject);
		p1.setSubjectArticle(subjectArticle);
		p1.setObjectArticle(objectArticle);
		p1.setAdjp(objectAdjp);
		p1.setPhraseArgs(args1);
		p1.setPostModifierPhrase(postModifierObject1);
		p1.setPreModifierObject(preModifierObject);
		p1.setSubjectGender(subjectGender);
		if(p2!=null)	p1.setCoordinatedPhrase(p2);
		
		ArrayList<String> sub0 = new ArrayList<>();
		sub0.add(getWord("to-remember"));
		Phrase remember = new Phrase(PhraseType.KNOWLEDGE, sub0, "", new ArrayList<>());
		remember.setCoordinatedPhrase(p1);
		
		return remember;
	}
	
	private Phrase lexicaliseEnvironment() {
		ArrayList<String> sub = new ArrayList<>();
		if(lingua.equals("english"))	sub.add(getWord("you"));
		ArrayList<String> ob = new ArrayList<>();
		ob.add(getWord("emissions"));
		String verb = getWord("discharge");
		ArrayList<String> preModifierObject = new ArrayList<>();
		Phrase p = new Phrase(PhraseType.ENVIRONMENT,sub,verb,ob,new ArrayList<String>());
		p.setPreModifierPhrase(getWord("finally"));
		ArrayList<String> objectArgs = new ArrayList<>();
		objectArgs.add(getWord("carbon"));
		p.setObjectArgs(objectArgs);
	
		p.setPostModifierPhrase(getWord("of"));
		p.setTense(Tense.PAST);
		p.setPerfect(true);
		
		if(totalePunteggioEnvironment<50)	preModifierObject.add(getWord("too-much"));
		else	preModifierObject.add(getWord("too-much"));
		p.setObjectIsPlural(true);
		p.setPreModifierObject(preModifierObject);
		p.setAdjpGender(Gender.FEMININE);
		
		
		ArrayList<String> subject2 = new ArrayList<>();
		subject2.add(getWord(badMacronutrienteEnvironment));
		ArrayList<String> subjectArgs = new ArrayList<>();
		if(lingua.equals("italiano"))	subjectArgs.add(getWord("consumed"));
		ArrayList<String> object2 = new ArrayList<>();
		object2.add(getWord("impact"));
		ArrayList<String> adjp2 = new ArrayList<>();
		adjp2.add(getWord("environmental"));
		ArrayList<String> preModifierObjectList = new ArrayList<>();
		preModifierObjectList.add(getWord("good"));
		
		Phrase coordinatedPhrase = new Phrase(PhraseType.ENVIRONMENT,subject2,getWord("to-have"),object2);
		coordinatedPhrase.setObjectArticle(getWord("a"));
		coordinatedPhrase.setNegative(true);
		coordinatedPhrase.setAdjp(adjp2);
		coordinatedPhrase.setPreModifierObject(preModifierObjectList);
		coordinatedPhrase.setSubjectArticle(getWord("the"));
		coordinatedPhrase.setSubjectGender(Gender.FEMININE);
		coordinatedPhrase.setSubjectArgs(subjectArgs);
		p.setConjunction(getWord("because"));
		
		
		p.setCoordinatedPhrase(coordinatedPhrase);
		
		return p;
	}
	
	private Phrase lexicaliseWelcome() {
		String saluto = getWord("good-evening");
		Calendar c = Calendar.getInstance();
		int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
		if (etaUtente<18) 
			saluto = getWord("hello");
		else if(timeOfDay >= 0 && timeOfDay < 12) 
			saluto = getWord("good-morning");
		else if(timeOfDay >= 12 && timeOfDay < 16) 
			saluto = getWord("good-afternoon");
		
		ArrayList<String> sub = new ArrayList<>();
		sub.add(nomeUtente);
		
		Phrase phraseWelcome1 = new Phrase(PhraseType.WELCOME,sub,"",new ArrayList<String>(),new ArrayList<String>());
		
		phraseWelcome1.setPreModifierPhrase(saluto);
		if(!isDietician())	phrases.add(phraseWelcome1);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
		
		
		sub = new ArrayList<>();
		sub.add("");
		if(lingua.equals("english")) {
			if(!isDietician())		sub.add(getWord("you"));
		}
		ArrayList<String> obj = new ArrayList<>();
		obj.add(getWord("mscore"));
		Phrase phraseWelcome2 = new Phrase(PhraseType.WELCOME,sub,getWord("to-obtain"),obj,new ArrayList<String>());
		phraseWelcome2.setPreModifierPhrase(getWord("this-week"));
		phraseWelcome2.setObjectArticle(getWord("a"));
		phraseWelcome2.setTense(Tense.PAST);
		if(lingua.equals("italiano"))	phraseWelcome2.setPerfect(true);
		ArrayList<String> args = new ArrayList<>();
		args.add(Integer.toString(indiceMed)+" "+getWord("out-of")+" 55");
		phraseWelcome2.setPostModifierPhrase(getWord("equal"));
		phraseWelcome2.setPhraseArgs(args);
		String postModifierPhrase = "";
		//phraseWelcome2.setPostModifierPhrase(getWord("out-of")+" 55");

		Phrase indPhrase = lexicaliseIndiceMed();
		
		if(isDietician()) {
			sub.add(getWord("patient"));
			phraseWelcome2.setSubject(sub);
			phraseWelcome2.setSubjectArticle(getWord("the"));
		}
		
		phraseWelcome2.setConjunction(getWord("and"));
		phraseWelcome2.setCoordinatedPhrase(indPhrase);
		
		phrases.add(phraseWelcome2);
		//Se è un dietista o devo darle del lei, non faccio esclamazioni
		if(etaUtente<18 && !isDietician()){
			phrases.add(temp);
		}
		return phraseWelcome2;
		
		
	}
	
	private Phrase lexicaliseIndiceMed() {
		Phrase p = new Phrase();
		
		ArrayList<String> sub = new ArrayList<>();

		if(lingua.equals("english")) 	sub.add(getWord("you"));
		
		p.setSubject(sub);
		if(lingua.equals("italiano")) 	p.setVerb(getWord("to-be"));
		else{
			p.setVerb(getWord("to-improve"));
			p.setTense(Tense.PAST);
		}
		
		ArrayList<String> adj = new ArrayList<>();
		ArrayList<String> subTemp = new ArrayList<>();
		if(indiceMed<lastIndiceMed) {
			//p.setVerb(getWord("to-get-worse"));
			//p.setModal(getWord("to-be"));
			if(lingua.equals("italiano")) 	
				adj.add(getWord("improved"));
			subTemp.add(getWord("congratulation"));
			temp = new Phrase(PhraseType.EXCLAMATION,subTemp,"",new ArrayList<String>());
			if(lingua.equals("italiano")) temp.setForm(Form.INFINITIVE);
			if(etaUtente>18  && lingua.equals("italiano"))	temp.setFormal(true);
		}else {
			//p.setVerb(getWord("to-improve"));
			if(lingua.equals("italiano")) 	
				adj.add(getWord("not-improved"));
			else 
				p.setNegative(true);
			
			subTemp.add(getWord("to-not-give-up"));
			temp = new Phrase(PhraseType.EXCLAMATION,subTemp,"",new ArrayList<String>());
			temp.setNegative(true);
			if(lingua.equals("italiano")) temp.setForm(Form.INFINITIVE);
			if(etaUtente>18  && lingua.equals("italiano"))	temp.setFormal(true);
		}
		
		p.setAdjp(adj);
		if(sessoUtente.equals("F"))	p.setAdjpGender(Gender.FEMININE);
		else	p.setAdjpGender(Gender.MASCULINE);
		
		p.setType(PhraseType.WELCOME);
		ArrayList<String> subP = new ArrayList<>();
		subP.add("");
		p.setSubject(subP);
		
		ArrayList<String> args = new ArrayList<>();
		args.add(getWord("last-week"));
		if(lingua.equals("italiano"))	p.setPostModifierPhrase(getWord("since-fem"));
		else	p.setPostModifierPhrase(getWord("since"));
		p.setPhraseArgs(args);
		
		return p;
		
	}
	
	private Phrase lexicaliseVeryGood(String connection) {
		Phrase phraseVeryGood;
		ArrayList<String> subject = new ArrayList<>();
		subject.add("");
		if(lingua.equals("english")) 	subject.add(getWord("you"));
		String verb = getWord("to-do");
		ArrayList<String> object = new ArrayList<String>();
		object.add(getWord("job"));
		ArrayList<String> macronutrientiVeryGoodWords = new ArrayList<>();
		for(String m:macronutrientiVeryGood) {
			macronutrientiVeryGoodWords.add(getWord(m));
		}
		phraseVeryGood = new Phrase(PhraseType.VERYGOOD, subject, verb, object, macronutrientiVeryGoodWords);
		
		phraseVeryGood.setTense(Tense.PAST);
		phraseVeryGood.setPerfect(true);
		phraseVeryGood.setPreModifierPhrase(connection);
		ArrayList<String> adjp = new ArrayList<String>();
		adjp.add(getWord("fantastic"));
		phraseVeryGood.setAdjp(adjp);
		phraseVeryGood.setObjectArticle(getWord("a"));
		phraseVeryGood.setPostModifierPhrase(getWord("with"));
		
		return phraseVeryGood;
	}
	
	private Phrase lexicaliseGood(String connection) {
		Phrase phraseGood;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("portion"));
		String verb = getWord("to-be");
		ArrayList<String> adjp = new ArrayList<String>();
		adjp.add(getWord("nearly"));
		adjp.add(getWord("very-good"));
		
		List<String> firstMacros = new ArrayList<>();
		List<String> lastMacros = new ArrayList<>();
		
		ArrayList<String> macronutrientiGoodWords = new ArrayList<>();
		for(String m:macronutrientiGood) {
			macronutrientiGoodWords.add(getWord(m));
		}
		phraseGood = new Phrase(PhraseType.GOOD, subject, verb, new ArrayList<>(), new ArrayList<>());
		phraseGood.setSubjectArgs(macronutrientiGoodWords);
		phraseGood.setTense(Tense.PAST);
		//phraseGood.setSubjectGender(Gender.FEMININE);
		phraseGood.setAdjp(adjp);
		phraseGood.setAdjpGender(Gender.FEMININE);
		phraseGood.setSubjectArticle(getWord("the"));
		phraseGood.setPreModifierPhrase(connection);
		phraseGood.setPostModifierSubject(getWord("of"));
		ArrayList<String> preModifierObject = new ArrayList<String>();
		preModifierObject.add(getWord("nearly"));
		//phraseGood.setPostModifierPhrase(getWord("of"));
		return phraseGood;
	}
	
	private Phrase lexicaliseBad(String connection) {
		Phrase phraseBad;
		ArrayList<String> subject = new ArrayList<>();
		
		String article = "";
		if(isDietician()) {
			subject.add(getWord("patient"));
			article = getWord("the");
		}else if(lingua.equals("english")) 	
			subject.add(getWord("you"));
		String modal = getWord("to-can");
		String verb = getWord("to-improve");
		
		List<String> firstMacros = new ArrayList<>();
		List<String> lastMacros = new ArrayList<>();
		
		ArrayList<String> macronutrientiBadWords = new ArrayList<>();
		for(String m:macronutrientiBad) {
			macronutrientiBadWords.add(getWord(m));
		}
		if(macronutrientiBadWords.size()>3) { 
			firstMacros = macronutrientiBadWords.subList(0, 3);
			lastMacros = macronutrientiBadWords.subList(3, macronutrientiBadWords.size());
		}else firstMacros = macronutrientiBadWords;
		ArrayList<String> args1 = new ArrayList<>();
		for(String m:firstMacros)
			args1.add(m);
		phraseBad = new Phrase(PhraseType.BAD, subject, verb, new ArrayList<String>(), args1);
		phraseBad.setModal(modal);
		phraseBad.setPreModifierPhrase(connection);
		if(macronutrientiBad.size()==1) {
			phraseBad.setArgsArticle(getWord("the"));
		}
		phraseBad.setSubjectArticle(article);		
		phraseBad.setPostModifierPhrase(getWord("with"));
		
		Phrase coordinatedPhrase = new Phrase(PhraseType.BAD,new ArrayList<>(),"",new ArrayList<String>(), new ArrayList<String>());
		ArrayList<String> args2 = new ArrayList<>();
		coordinatedPhrase.setPreModifierPhrase(getWord("and"));
		coordinatedPhrase.setPostModifierPhrase(getWord("with"));
		for(String m:lastMacros) {
			args2.add(m);
		}
		coordinatedPhrase.setPhraseArgs(args2);
		if(macronutrientiBadWords.size()>3)
			phraseBad.setCoordinatedPhrase(coordinatedPhrase);
		//phrases.add(phraseBad);
		return phraseBad;
	}
	
	private Phrase lexicaliseBadDietician(String connection) {
		//Phrase p = lexicaliseBad("");

		//phrases.add(p);
		
		ArrayList<String> punteggio1 = new ArrayList<>();
		ArrayList<String> punteggio2 = new ArrayList<>();

		for(String mBad:macronutrientiBad) {
			String mBadWord = getWord(mBad);
			for(Macronutriente mAll:allMacronutrienti) {
				Macronutriente mTemp = new Macronutriente(mBadWord);
				if(mTemp.isThisType(mAll.getNome(),dictionary))	{
					if(mAll.getPunteggio()==1)	punteggio1.add(mBadWord);
					else						punteggio2.add(mBadWord);
				}
			}
		}
		
		Phrase phrase1 = null;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("score"));
		String verb = getWord("to-be");
		if(punteggio1.isEmpty()) {
			punteggio1 = (ArrayList<String>) punteggio2.clone();
			punteggio2 = new ArrayList<>(); 
		}
		
		if(punteggio1.isEmpty() && punteggio2.isEmpty())	return null;

		phrase1 = new Phrase(PhraseType.BAD, subject, verb, new ArrayList<>(), new ArrayList<>());
		if(!punteggio1.isEmpty()) {
		
			phrase1.setSubjectArgs(punteggio1);
			phrase1.setPostModifierSubject(getWord("of"));
			phrase1.setSubjectArticle(getWord("the"));
			phrase1.setPostModifierPhrase(getWord("equal"));
			ArrayList<String> args1 = new ArrayList<>();
			args1.add("1");
			phrase1.setPhraseArgs(args1);
		}if(!punteggio2.isEmpty()) {
			
			Phrase phrase2;
			phrase2 = new Phrase(phrase1);
			phrase2.setPreModifierPhrase(getWord("while"));
			phrase2.setSubjectArgs(punteggio2);
			ArrayList<String> args2 = new ArrayList<>();
			args2.add("2");
			phrase2.setPhraseArgs(args2);
			phrase1.setCoordinatedPhrase(phrase2);
		}
		
		//phrases.add(phraseBad);
		return phrase1;
	}

	private Phrase lexicaliseVeryBad(String connection) {
		Phrase phraseVeryBad;
		
		ArrayList<String> subject = new ArrayList<>();
		List<String> firstMacros = new ArrayList<>();
		List<String> lastMacros = new ArrayList<>();
		ArrayList<String> macronutrientiVeryBadWords = new ArrayList<>();
		for(String m:macronutrientiVeryBad) {
			macronutrientiVeryBadWords.add(getWord(m));
		}
		if(macronutrientiVeryBadWords.size()>3) { 
			firstMacros = macronutrientiVeryBadWords.subList(0, 3);
			lastMacros = macronutrientiVeryBadWords.subList(3, macronutrientiVeryBadWords.size());
		}
		else firstMacros = macronutrientiVeryBadWords;
		
		if(isDietician()) //IF dietista
			subject.add(nomeUtente);
		else if(lingua.equals("english"))	subject.add(getWord("you"));
		else subject.add("");
		
		String verb = getWord("to-eat");
		ArrayList<String> object = new ArrayList<String>();
		ArrayList<String> preModifierObject = new ArrayList<String>();
		for (String m: firstMacros) {
			object.add(m);
			Macronutriente macronutriente = new Macronutriente(m);
			if(macronutriente.isMoreBetter(dictionary))	preModifierObject.add(getWord("more"));
			else preModifierObject.add(getWord("less"));
		}
		phraseVeryBad = new Phrase(PhraseType.VERYBAD, subject, verb, object, new ArrayList<>());
		phraseVeryBad.setModal(getWord("to-must"));
		if(isDietician() && lingua.equals("italiano")) //IF dietista
			phraseVeryBad.setFormal(true);	
		phraseVeryBad.setPreModifierPhrase(connection);
		phraseVeryBad.setPreModifierObject(preModifierObject);
		//phrases.add(phraseVeryBad);
		if(macronutrientiVeryBad.size()>3) { 
			Phrase phraseCoord = new Phrase(phraseVeryBad);
			phraseCoord.setVerb(getWord("to-consume"));
			object = new ArrayList<String>();
			for (String m: lastMacros) {
				object.add(m);
				Macronutriente macronutriente = new Macronutriente(m);
				if(macronutriente.isMoreBetter(dictionary))	preModifierObject.add(getWord("more"));
				else preModifierObject.add(getWord("less"));
			}
			phraseCoord.setObject(object);
			phraseVeryBad.setCoordinatedPhrase(phraseCoord);
			phraseVeryBad.setConjunction(getWord("and"));
		}
		return phraseVeryBad;
		
		
		
		
		/*
		if(macronutrientiVeryBad.size()<=3) {
			ArrayList<String> subject = new ArrayList<>();
			//subject.add(getWord("you"));
			if(isDietician()) //IF dietista
				subject.add(nomeUtente);
			String verb = getWord("to-eat");
			ArrayList<String> object = new ArrayList<String>();
			ArrayList<String> preModifierObject = new ArrayList<String>();
			for (String m: macronutrientiVeryBad) {
				object.add(m);
				Macronutriente macronutriente = new Macronutriente(m);
				if(macronutriente.isMoreBetter(dictionary))	preModifierObject.add(getWord("more"));
				else preModifierObject.add(getWord("less"));
			}
			phraseVeryBad = new Phrase(PhraseType.VERYBAD, subject, verb, object, new ArrayList<>());
			phraseVeryBad.setModal(getWord("to-must"));
			if(isDietician()) //IF dietista
				phraseVeryBad.setFormal(true);	
			phraseVeryBad.setPreModifierPhrase(connection);
			phraseVeryBad.setPreModifierObject(preModifierObject);
			//phrases.add(phraseVeryBad);
			return phraseVeryBad;
		}else {
			ArrayList<String> subject = new ArrayList<>();
			List<String> firstMacros = macronutrientiVeryBad.subList(0, 3);
			List<String> lastMacros = macronutrientiVeryBad.subList(3, macronutrientiVeryBad.size());
			//subject.add(getWord("you"));
			if(isDietician()) //IF dietista
				subject.add(nomeUtente);
			String verb = getWord("to-eat");
			ArrayList<String> object = new ArrayList<String>();
			ArrayList<String> preModifierObject = new ArrayList<String>();
			for (String m: firstMacros) {
				object.add(m);
				Macronutriente macronutriente = new Macronutriente(m);
				if(macronutriente.isMoreBetter(dictionary))	preModifierObject.add(getWord("more"));
				else preModifierObject.add(getWord("less"));
			}
			phraseVeryBad = new Phrase(PhraseType.VERYBAD, subject, verb, object, new ArrayList<>());
			phraseVeryBad.setModal(getWord("to-must"));
			if(isDietician()) //IF dietista
				phraseVeryBad.setFormal(true);	
			phraseVeryBad.setPreModifierPhrase(connection);
			phraseVeryBad.setPreModifierObject(preModifierObject);
			//phrases.add(phraseVeryBad);
			Phrase phraseCoord = new Phrase(phraseVeryBad);
			phraseCoord.setVerb(getWord("to-consume"));
			object = new ArrayList<String>();
			for (String m: lastMacros) {
				object.add(m);
				Macronutriente macronutriente = new Macronutriente(m);
				if(macronutriente.isMoreBetter(dictionary))	preModifierObject.add(getWord("more"));
				else preModifierObject.add(getWord("less"));
			}
			phraseCoord.setObject(object);
			phraseVeryBad.setCoordinatedPhrase(phraseCoord);
			phraseVeryBad.setConjunction(getWord("and"));
			
			return phraseVeryBad;
		}*/
	}
	
	private Phrase lexicaliseVeryBadDietician(String connection) {
		Phrase p = lexicaliseVeryBad("");
		Phrase phraseVeryBad;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("patient"));
		String verb = getWord("to-obtain");
		ArrayList<String> subjectArgs = new ArrayList<String>();
		for (String m: macronutrientiVeryBad) {
			subjectArgs.add(getWord(m));
		}
		ArrayList<String> object = new ArrayList<>();
		object.add(getWord("score"));
		phraseVeryBad = new Phrase(PhraseType.VERYBAD, subject, verb, object, new ArrayList<>());
		if(isDietician()  && lingua.equals("italiano")) //IF dietista
			phraseVeryBad.setFormal(true);
		//phraseVeryBad.setSubjectArgs(subjectArgs);
		//phraseVeryBad.setPostModifierSubject(getWord("of"));
		phraseVeryBad.setObjectArticle(getWord("a"));
		phraseVeryBad.setSubjectArticle(getWord("the"));
		phraseVeryBad.setPerfect(true);
		phraseVeryBad.setPreModifierPhrase(connection);
		phraseVeryBad.setPostModifierPhrase(getWord("equal"));
		ArrayList<String> args = new ArrayList<>();
		args.add("0");
		phraseVeryBad.setPhraseArgs(args);
		p.setConjunction(getWord("because"));
		p.setCoordinatedPhrase(phraseVeryBad);
		//phrases.add(phraseVeryBad);
		return p;
	}
	
	private Phrase lexicaliseVeryGoodPasto(String connection) {
		Phrase p = null;
		ArrayList<String> subject1 = new ArrayList<>();
		
		
		String temp = "";
		switch(veryGoodPasto.getSlot()) {
			case 0:
				temp = getWord("breakfast");
				break;
			case 1:
				temp = getWord("morning-snack");
				break;
			case 2:
				temp = getWord("lunch");
				break;
			case 3:
				temp = getWord("afternoon-snack");
				break;
			case 4:
				temp = getWord("dinner");
				break;
		}
		subject1.add(temp);
		
		String verb = getWord("to-be");
		ArrayList<String> subjectArgs = new ArrayList<String>();
		
		// da mettere in metodo apposito
		Calendar cal = Calendar.getInstance();
		cal.setTime(veryGoodPasto.getGiorno());		
		Locale lang = null;
		if (lingua.equals("english"))	lang = Locale.ENGLISH;
		else if(lingua.equals("italiano"))	lang = Locale.ITALIAN;
		String wordDayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, lang);
		
		if(lingua.equals("italiano"))	subjectArgs.add(wordDayOfWeek);
		
		ArrayList<String> obj = new ArrayList<>();
		obj.add(getWord("choice"));
		
		
		p = new Phrase(PhraseType.MEAL, subject1, verb, new ArrayList<>(), new ArrayList<>());
		if(isDietician()  && lingua.equals("italiano")) //IF dietista
			p.setFormal(true);
		if(lingua.equals("italiano")) {
			p.setSubjectArticle(getWord("the"));
			p.setPostModifierSubject(getWord("of"));
		}else {
			p.setSubjectPlural(false);
			p.setSubjectAdjp(wordDayOfWeek);
		}
		p.setSubjectArgs(subjectArgs);
		p.setObject(obj);
		//p.setModal(getWord("to-be"));
		p.setObjectArticle(getWord("a"));
		if(lingua.equals("italiano"))	p.setTense(Tense.PLUS_PAST);
		else p.setTense(Tense.PAST);
		//p.setPerfect(true);
		ArrayList<String> adjp1 = new ArrayList<>();
		adjp1.add(getWord("very-good"));
		p.setAdjp(adjp1);
		
		String conjunction = getWord("because");
		ArrayList<String> subject2 = new ArrayList<>();
		subject2.add(veryGoodPasto.getNome());
		
		ArrayList<String> object2 = new ArrayList<>();
		object2.add(getWord("portion"));
		ArrayList<String> adjp2 = new ArrayList<>();
		adjp2.add(getWord("good"));
		
		String verb2 = getWord("to-have");
		Phrase coordinatedPhraseMore = new Phrase(PhraseType.MEAL, subject2, verb2, object2, new ArrayList<>());
		p.setConjunction(conjunction);
		coordinatedPhraseMore.setSubjectArticle(getWord("the"));
		coordinatedPhraseMore.setAdjp(adjp2);

		if(lingua.equals("italiano"))	coordinatedPhraseMore.setSubjectAdjp(getWord("dish"));
		
		ArrayList<String> objectArgsMore = new ArrayList<>();
		ArrayList<String> objectArgsLess = new ArrayList<>();
		
		HashMap<String,Float> punteggi = veryBadPasto.getPunteggi();
		for (String m: macronutrientiVeryGood) {
			if(punteggi.get(m)>0) {
				if(m.equals(MacronutrienteType.CARNEROSSA) 
						|| m.equals(MacronutrienteType.LATTICINI) 
						|| m.equals(MacronutrienteType.POLLAME)) {
						objectArgsLess.add(getWord(m));
				}else	objectArgsMore.add(getWord(m));
			}
		}
		coordinatedPhraseMore.setObjectArticle(getWord("a"));
		coordinatedPhraseMore.setObjectArgs(objectArgsMore);
		coordinatedPhraseMore.setPostModifierPhrase(getWord("of"));
		
		if(!objectArgsLess.isEmpty()) {
			ArrayList<String> object3 = new ArrayList<>();
			object3.add(getWord("portion"));
			ArrayList<String> adjp3 = new ArrayList<>();
			adjp3.add(getWord("poor"));
			Phrase coordinatedPhraseLess = new Phrase(PhraseType.MEAL, new ArrayList<String>(), getWord("to-have"), object3, objectArgsLess);
			coordinatedPhraseLess.setPostModifierPhrase(getWord("of"));
		}
		
		p.setCoordinatedPhrase(coordinatedPhraseMore);
		
		return p;
	}
	
	private Phrase lexicaliseVeryBadPasto(String connection) {
		Phrase p = null;
		String verb1 = getWord("to-advise-against");
		
		ArrayList<String> subjectArgs = new ArrayList<String>();
		ArrayList<String> sub1 = new ArrayList<>();
		sub1.add(getWord("experts"));
		
		// da mettere in metodo apposito
		Calendar cal = Calendar.getInstance();
		cal.setTime(veryBadPasto.getGiorno());		
		Locale lang = null;
		if (lingua.equals("english"))	lang = Locale.ENGLISH;
		else if(lingua.equals("italiano"))	lang = Locale.ITALIAN;
		String wordDayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, lang);
		
		subjectArgs.add(wordDayOfWeek);
		
		ArrayList<String> obj1 = new ArrayList<>();
		obj1.add(veryBadPasto.getNome());
		
		
		p = new Phrase(PhraseType.MEAL, sub1, verb1,  obj1, new ArrayList<>());
		if(isDietician()  && lingua.equals("italiano")) //IF dietista
			p.setFormal(true);
		
		p.setObjectArticle(getWord("the"));
		ArrayList<String> preModifierObject = new ArrayList<>();
		preModifierObject.add(getWord("dish"));
		if(lingua.equals("italiano"))	p.setPreModifierObject(preModifierObject);
		p.setSubjectArticle(getWord("the"));
		p.setSubjectPlural(true);
		if(lingua.equals("italiano"))	p.setTense(Tense.CONDITIONAL);
		ArrayList<String> obj2 = new ArrayList<>();
		ArrayList<String> args2 = new ArrayList<>();
		ArrayList<String> adj2 = new ArrayList<>();
		
		String temp = "";
		switch(veryBadPasto.getSlot()) {
			case 0:
				temp = getWord("breakfast");
				break;
			case 1:
				temp = getWord("morning-snack");
				break;
			case 2:
				temp = getWord("lunch");
				break;
			case 3:
				temp = getWord("afternoon-snack");
				break;
			case 4:
				temp = getWord("dinner");
				break;
		}
		args2.add(temp);
		obj2.add(wordDayOfWeek);
		adj2.add(getWord("last"));
		
		//p.setObjectArgs(args);
		//p.setPostModifierPhrase(getWord("of"));
		
		//p.setPreModifierPhrase(getWord("next-week"));
		
		String verb2 = getWord("to-eat");
		ArrayList<String> sub2 = new ArrayList<>();
		if(lingua.equals("english"))	sub2.add(getWord("you"));
		else sub2.add("");
		Phrase relativePhrase = new Phrase(PhraseType.MEAL, sub2, verb2,  obj2, new ArrayList<>());
		if(lingua.equals("italiano")) {
			relativePhrase.setObjectArticle(getWord("the"));
			relativePhrase.setPerfect(true);
		}
		else	relativePhrase.setTense(Tense.PAST);
		relativePhrase.setPhraseArgs(args2);
		relativePhrase.setPostModifierPhrase(getWord("at"));
		relativePhrase.setAdjp(adj2);
		
		
		ArrayList<String> sub3 = new ArrayList<>();
		sub3.add(getWord("portion"));
		Phrase coordinatedPhrase = new Phrase(PhraseType.MEAL, sub3, getWord("to-be"), new ArrayList<>(), new ArrayList<>());
		coordinatedPhrase.setSubjectArticle(getWord("the"));
		coordinatedPhrase.setPreModifierPhrase(getWord("because"));
		ArrayList<String> adjp2 = new ArrayList<>();
		if(lingua.equals("italiano")) {
			coordinatedPhrase.setNegative(true);
			adjp2.add(getWord("good"));
		}else 
			adjp2.add(getWord("not-good"));
		
		ArrayList<String> subjectArgs2 = new ArrayList<>();
		HashMap<String,Float> punteggi = veryBadPasto.getPunteggi();
		for (String m: macronutrientiVeryBad) {
			if(punteggi.get(m)>0)
				subjectArgs2.add(getWord(m));
		}
		coordinatedPhrase.setSubjectArgs(subjectArgs2);
		coordinatedPhrase.setPostModifierSubject(getWord("of"));
		
		//adjp2.add(getWord("good"));
		coordinatedPhrase.setAdjp(adjp2);
		coordinatedPhrase.setAdjpGender(Gender.FEMININE);
		
		relativePhrase.setCoordinatedPhrase(coordinatedPhrase);
		
		

		p.setRelativeObjectPhrase(relativePhrase);
		return p;
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
	
	private boolean isDietician() {
		return (conoscenzaDominio==1);
	}
}
