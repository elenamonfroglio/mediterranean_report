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
	private int indiceMed;
	private int lastIndiceMed;
	private int etaUtente;
	private int conoscenzaDominio;
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
	
	public void extractBestMeal() {
		ArrayList<String> veryGoodMacros = new ArrayList<>();
		veryGoodMacros.add(MacronutrienteType.CEREALI);
		veryGoodMacros.add(MacronutrienteType.PATATE);
		veryGoodMacros.add(MacronutrienteType.VERDURA);
		Pasto p = thisWeek.getPastoVeryGood(veryGoodMacros,dictionary);
		p.print();	
	}
	
	private void extractUtente(JsonObject object) {
		nomeUtente = object.getString("nome utente");
		sessoUtente = object.getString("sesso utente");
		etaUtente = object.getInt("eta utente");
		conoscenzaDominio = object.getInt("conoscenza dominio");
	}
	
	private void extractLingua(JsonObject object) {
		lingua = object.getString("lingua");
	}
	
	private void extractMacronutrienti(JsonObject object) {
		indiceMed = object.getInt("indice Med");
		lastIndiceMed = object.getInt("last indice Med");
		totalePunteggioEnvironment = object.getInt("totalePunteggioEnvironment");
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
		//in this method I will choose the euristic based on the user model
		//return getMostIntenseWord(concept);
		//return getRandomWord(concept);
		//return getFirstWord(concept);
		return getBestWord(concept, EmotionType.SENSITIVITY);
	}
	
	public void lexicalisation() {
		//ArrayList<String> macronutrientiVeryGoodMoreIsBetter		
		String oldItem = "";
		int iter = 0;
		lexicaliseWelcome();
		Phrase phrase = null;
		for(String item: order) {
			switch(item) {
				case PhraseType.VERYBAD:
					if(!macronutrientiVeryBad.isEmpty()) {
						if(isDietician())	phrase = lexicaliseVeryBadDietician("");
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							phrase = lexicaliseVeryBad(getWord("but"));
						else if(oldItem!="") 	phrase = lexicaliseVeryBad(getWord("and"));
						else phrase = lexicaliseVeryBad("");
						phrases.add(phrase);
					}
					break;
				case PhraseType.BAD:
					if(!macronutrientiBad.isEmpty()) {
						if(isDietician())	phrase = lexicaliseBadDietician("");
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							phrase = lexicaliseBad(getWord("but"));
						else if(oldItem!="") 	phrase = lexicaliseBad(getWord("and"));
						else phrase = lexicaliseBad("");
						phrases.add(phrase);
					}
					break;
				case PhraseType.VERYGOOD:
					if(!macronutrientiVeryGood.isEmpty()) {
						if(isDietician())	break;
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							phrase = lexicaliseVeryGood(getWord("but"));
						else if(oldItem!="") 	phrase = lexicaliseVeryGood(getWord("and"));
						else phrase = lexicaliseVeryGood("");
						phrases.add(phrase);
					}
					break;
				case PhraseType.GOOD:
					if(!macronutrientiGood.isEmpty()) {  
						if(isDietician())	break;
						else if(PhraseType.isOpposite(item, oldItem) && oldItem!="")
							phrase = lexicaliseGood(getWord("but"));
						else if(oldItem!="") 	phrase = lexicaliseGood(getWord("and"));
						else phrase = lexicaliseGood("");
						phrases.add(phrase);
					}
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

		if(totalePunteggioEnvironment!=-1) {
			phrase = lexicaliseEnvironment();
			phrases.add(phrase);
		}
		
		if(etaUtente>18 && lingua.equals("italiano"))	
			setFormalPhrase();
	}
	
	private void setFormalPhrase() {
		for (Phrase p:phrases) {
			p.setFormal(true);
			if(p.getCoordinatedPhrase()!=null)	p.getCoordinatedPhrase().setFormal(true);			
		}
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
		
		p.setPreModifierObject(preModifierObject);
		
		//phrases.add(p);
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
		if(lingua.equals("english"))		sub.add(getWord("you"));
		ArrayList<String> obj = new ArrayList<>();
		obj.add(getWord("mscore"));
		Phrase phraseWelcome2 = new Phrase(PhraseType.WELCOME,sub,getWord("to-obtain"),obj,new ArrayList<String>());
		phraseWelcome2.setPreModifierPhrase(getWord("this-week"));
		phraseWelcome2.setObjectArticle(getWord("a"));
		phraseWelcome2.setTense(Tense.PAST);
		phraseWelcome2.setPerfect(true);
		ArrayList<String> args = new ArrayList<>();
		args.add(Integer.toString(indiceMed));
		phraseWelcome2.setPostModifierPhrase(getWord("equal"));
		phraseWelcome2.setPhraseArgs(args);

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
		p.setVerb(getWord("to-be"));
		
		ArrayList<String> adj = new ArrayList<>();
		
		if(indiceMed<lastIndiceMed) {
			//p.setVerb(getWord("to-get-worse"));
			//p.setModal(getWord("to-be"));
			adj.add(getWord("improved"));
			temp = new Phrase(PhraseType.EXCLAMATION,sub,getWord("congratulation"),new ArrayList<String>());
			temp.setForm(Form.INFINITIVE);
			if(etaUtente>18  && lingua.equals("italiano"))	temp.setFormal(true);
		}else {
			//p.setVerb(getWord("to-improve"));
			adj.add(getWord("not-improved"));
			temp = new Phrase(PhraseType.EXCLAMATION,sub,getWord("to-give-up"),new ArrayList<String>());
			temp.setNegative(true);
			temp.setForm(Form.INFINITIVE);
			if(etaUtente>18  && lingua.equals("italiano"))	temp.setFormal(true);
		}
		
		p.setAdjp(adj);
		if(sessoUtente.equals("F"))	p.setAdjpGender(Gender.FEMININE);
		else	p.setAdjpGender(Gender.MASCULINE);
		
		p.setType(PhraseType.WELCOME);
		
		ArrayList<String> args = new ArrayList<>();
		args.add(getWord("last-week"));
		p.setPostModifierPhrase(getWord("since"));
		p.setPhraseArgs(args);
		
		return p;
		
	}
	
	private Phrase lexicaliseVeryGood(String connection) {
		Phrase phraseVeryGood;
		ArrayList<String> subject = new ArrayList<>();
		if(lingua.equals("english")) 	subject.add(getWord("you"));
		String verb = getWord("to-do");
		ArrayList<String> object = new ArrayList<String>();
		object.add(getWord("job"));
		phraseVeryGood = new Phrase(PhraseType.VERYGOOD, subject, verb, object, macronutrientiVeryGood);
		phraseVeryGood.setTense(Tense.PAST);
		phraseVeryGood.setPerfect(true);
		phraseVeryGood.setPreModifierPhrase(connection);
		ArrayList<String> adjp = new ArrayList<String>();
		adjp.add(getWord("very-good"));
		phraseVeryGood.setAdjp(adjp);
		phraseVeryGood.setObjectArticle(getWord("a"));
		phraseVeryGood.setPostModifierPhrase(getWord("with"));
		//phrases.add(phraseVeryGood);
		return phraseVeryGood;
	}
	
	private Phrase lexicaliseGood(String connection) {
		Phrase phraseGood;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("portion"));
		String verb = getWord("to-be");
		ArrayList<String> adjp = new ArrayList<String>();
		adjp.add(getWord("very-good"));
		phraseGood = new Phrase(PhraseType.GOOD, subject, verb, new ArrayList<>(), new ArrayList<>());
		phraseGood.setSubjectArgs(macronutrientiGood);
		phraseGood.setTense(Tense.PAST);
		phraseGood.setAdjp(adjp);
		phraseGood.setAdjpGender(Gender.FEMININE);
		phraseGood.setSubjectArticle(getWord("the"));
		phraseGood.setPreModifierPhrase(connection);
		phraseGood.setPostModifierSubject(getWord("of"));
		//ArrayList<String> adjp = new ArrayList<String>();
		ArrayList<String> preModifierObject = new ArrayList<String>();
		preModifierObject.add(getWord("nearly"));
		//adjp.add(getWord("very-good"));
		//phraseGood.setAdjp(adjp);
		phraseGood.setPreModifierObject(preModifierObject);
		phraseGood.setPostModifierPhrase(getWord("of"));
		//phrases.add(phraseGood);
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
		phraseBad = new Phrase(PhraseType.BAD, subject, verb, new ArrayList<String>(), macronutrientiBad);
		phraseBad.setModal(modal);
		phraseBad.setPreModifierPhrase(connection);
		if(macronutrientiBad.size()==1)	phraseBad.setArgsArticle(getWord("the"));
		phraseBad.setSubjectArticle(article);		
		phraseBad.setPostModifierPhrase(getWord("with"));
		//phrases.add(phraseBad);
		return phraseBad;
	}
	
	private Phrase lexicaliseBadDietician(String connection) {
		//Phrase p = lexicaliseBad("");

		//phrases.add(p);
		
		ArrayList<String> punteggio1 = new ArrayList<>();
		ArrayList<String> punteggio2 = new ArrayList<>();

		for(String mBad:macronutrientiBad) {
			for(Macronutriente mAll:allMacronutrienti) {
				Macronutriente mTemp = new Macronutriente(mBad);
				if(mTemp.isThisType(mAll.getNome(),dictionary))	{
					if(mAll.getPunteggio()==1)	punteggio1.add(mBad);
					else						punteggio2.add(mBad);
				}
			}
		}
		
		Phrase phrase1;
		ArrayList<String> subject = new ArrayList<>();
		subject.add(getWord("score"));
		String verb = getWord("to-be");
		
		phrase1 = new Phrase(PhraseType.BAD, subject, verb, new ArrayList<>(), new ArrayList<>());
		
		phrase1.setSubjectArgs(punteggio1);
		phrase1.setPostModifierSubject(getWord("of"));
		phrase1.setSubjectArticle(getWord("the"));
		phrase1.setPostModifierPhrase(getWord("equal"));
		ArrayList<String> args1 = new ArrayList<>();
		args1.add("1");
		phrase1.setPhraseArgs(args1);
		
		Phrase phrase2;
		phrase2 = new Phrase(phrase1);
		phrase2.setPreModifierPhrase(getWord("while"));
		phrase2.setSubjectArgs(punteggio2);
		ArrayList<String> args2 = new ArrayList<>();
		args2.add("2");
		phrase2.setPhraseArgs(args2);
		
		phrase1.setCoordinatedPhrase(phrase2);
		
		//phrases.add(phraseBad);
		return phrase1;
	}

	private Phrase lexicaliseVeryBad(String connection) {
		Phrase phraseVeryBad;
		
		
		ArrayList<String> subject = new ArrayList<>();
		List<String> firstMacros = new ArrayList<>();
		List<String> lastMacros = new ArrayList<>();
		if(macronutrientiVeryBad.size()>3) { 
			firstMacros = macronutrientiVeryBad.subList(0, 3);
			lastMacros = macronutrientiVeryBad.subList(3, macronutrientiVeryBad.size());
		}
		else firstMacros = macronutrientiVeryBad;
		
		if(isDietician()) //IF dietista
			subject.add(nomeUtente);
		else if(lingua.equals("english"))	subject.add(getWord("you"));
		
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
		if(lingua.equals("english")) 	subject.add(getWord("you"));
		subject.add(getWord("score"));
		String verb = getWord("to-be");
		ArrayList<String> subjectArgs = new ArrayList<String>();
		for (String m: macronutrientiVeryBad) {
			subjectArgs.add(m);
		}
		phraseVeryBad = new Phrase(PhraseType.VERYBAD, subject, verb, new ArrayList<>(), new ArrayList<>());
		if(isDietician()  && lingua.equals("italiano")) //IF dietista
			phraseVeryBad.setFormal(true);
		//phraseVeryBad.setSubjectArgs(subjectArgs);
		//phraseVeryBad.setPostModifierSubject(getWord("of"));
		phraseVeryBad.setSubjectArticle(getWord("the"));
		phraseVeryBad.setPreModifierPhrase(connection);
		phraseVeryBad.setPostModifierPhrase(getWord("equal"));
		ArrayList<String> args = new ArrayList<>();
		args.add("0");
		phraseVeryBad.setPhraseArgs(args);
		p.setRelativePhrase(phraseVeryBad);
		//phrases.add(phraseVeryBad);
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
		return (conoscenzaDominio==2);
	}
}
