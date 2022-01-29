package monfroglio.elena.controller;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.lexicon.italian.ITXMLLexicon;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;

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
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import monfroglio.elena.model.Phrase;
import monfroglio.elena.model.PhraseType;
import simplenlg.features.*;
import simplenlg.framework.*;
import simplenlg.phrasespec.*;
import simplenlg.lexicon.Lexicon;

//importo feature italiane
import simplenlg.features.italian.*;
//importo lessico italiano
import simplenlg.lexicon.italian.*;
//importo il realizer francese che richiama i metodi 
//realiseSyntax e realiseMorphology degli elementi linguistici
import simplenlg.realiser.Realiser;

public class ReportRealiser {
	private String fileName;
	private String lingua;
	private String nomeUtente;
	private Gender sessoUtente;
	private Lexicon myLexicon;
	private NLGFactory nlgFactory;
	private Realiser realiser;
	private JsonObject jsonObject;
	public ArrayList<Phrase> phrases;
	
	public ReportRealiser(String fileName, ArrayList<Phrase> phrases) {
		this.fileName = fileName;	
		jsonObject = readJson();
		lingua = jsonObject.getString("lingua");
		
		switch(lingua){
			case "italiano":
				myLexicon = new ITXMLLexicon();
				break;
			case "english":
				myLexicon = new simplenlg.lexicon.english.XMLLexicon();
				break;
		}
		this.phrases = phrases;
		nlgFactory = new NLGFactory(myLexicon);
		realiser = new Realiser();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void createLongSentence_new() {

		extractUtente(jsonObject);
		
		for(Phrase p:phrases) {
			ArrayList<String> macronutrienti = new ArrayList<>();
			for(int i=0;i<p.getArgs().size();i++) {
				macronutrienti.add(p.getArgs().get(i));
			}
			
			if(p.getType().equals(PhraseType.VERYGOOD)) {
				SPhraseSpec clause = createPhraseVeryGood(p, macronutrienti);

				String output = "";
				if(clause!=null)	output = realiser.realiseSentence(clause);
			    System.out.print("\n\n"+output+" ");
			}else if(p.getType().equals(PhraseType.GOOD)) {
				SPhraseSpec clause = createPhraseGood(p, macronutrienti);
				String output = "";
				if(clause!=null)	output = realiser.realiseSentence(clause);
			    System.out.print("\n\n"+output+" ");
			}else if(p.getType().equals(PhraseType.BAD)) {
				SPhraseSpec clause = createPhraseBad(p, macronutrienti);
				String output = "";
				if(clause!=null)	output = realiser.realiseSentence(clause);
			    System.out.print("\n\n"+output+" ");
			}else if(p.getType().equals(PhraseType.VERYBAD)) {
				SPhraseSpec clause = createPhraseVeryBad(p, macronutrienti);
				String output = "";
				if(clause!=null)	output = realiser.realiseSentence(clause);
			    System.out.print("\n\n"+output+" ");
			}
			
		}
	}
	
	public void createLongSentence_old() {

	}
	
	private NLGElement createPhraseWelcome() {
		NLGElement s1 = nlgFactory.createSentence("Buongiorno "+nomeUtente );
		
		return s1;
	}
	
	private SPhraseSpec createPhraseVeryGood(Phrase p, ArrayList<String> macronutrienti) {
		SPhraseSpec clause = nlgFactory.createClause();
		//VERBO "DO"
		clause.setVerb(p.getVerb());
		if(!p.getModal().equals("")) 
			clause.setFeature(Feature.MODAL, p.getModal());
		clause.setFeature(Feature.NEGATED, p.isNegative());
		clause.setFeature(Feature.TENSE, p.getTense());
		if(p.getForm()!=null) 
			clause.setFeature(Feature.FORM, p.getForm());
		clause.setFeature(Feature.PERFECT, p.isPerfect());
		
		//SOGGETTO "YOU"
		NPPhraseSpec subject = null;
		if(p.getSubject().size()==2) 	subject = nlgFactory.createNounPhrase(p.getSubject().get(0),p.getSubject().get(1));	
		else	subject = nlgFactory.createNounPhrase(p.getSubject().get(0));
		clause.setSubject(subject);
		
		//"QUESTA SETTIMANA"
		
		NPPhraseSpec when = nlgFactory.createNounPhrase("settimana");
		when.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		when.addPreModifier("questo");
		clause.addFrontModifier(when);
		
		
		//OGGETTO "A GREAT JOB"
		if(!p.getObject().isEmpty()) {
			NPPhraseSpec object = nlgFactory.createNounPhrase("un",p.getObject().get(0));
			if(!p.getAdjp().isEmpty()) {
				object.addPreModifier(p.getAdjp().get(0));
				clause.setObject(object);
			}
		}
		//"CON CEREALI, VERDURE ECC.."
		ArrayList<NPPhraseSpec> macronutrientiList = new ArrayList<NPPhraseSpec>();
		for(String m: macronutrienti) {
			String article = getArticle(m);
			NPPhraseSpec temp = nlgFactory.createNounPhrase(article,m);
			temp.setFeature(LexicalFeature.GENDER, getGender(m));
			//temp.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
			temp.setPlural(isPlural(m));
			macronutrientiList.add(temp);
			
		}
		
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		for(NPPhraseSpec elem: macronutrientiList) {
			coord.addCoordinate(elem);
		}
		coord.addPreModifier("con");

		clause.addPostModifier(coord);
		
		String output="";		
		return clause;
		
	}
	
	private SPhraseSpec createPhraseGood(Phrase p, ArrayList<String> macronutrienti) {
		SPhraseSpec clause = nlgFactory.createClause();
		//VERBO "ESSERE"
		clause.setVerb(p.getVerb());
		if(!p.getModal().equals("")) 
			clause.setFeature(Feature.MODAL, p.getModal());
		clause.setFeature(Feature.NEGATED, p.isNegative());
		clause.setFeature(Feature.TENSE, p.getTense());
		if(p.getForm()!=null) 
			clause.setFeature(Feature.FORM, p.getForm());
		clause.setFeature(Feature.PERFECT, p.isPerfect());
		
		//SOGGETTO "LA QUANTITÀ"
		NPPhraseSpec subject = null;
		if(p.getSubject().size()==2) 	subject = nlgFactory.createNounPhrase(p.getSubject().get(0),p.getSubject().get(1));	
		else	subject = nlgFactory.createNounPhrase(p.getSubject().get(0));
		
		//"DI CEREALI, VERDURE ECC.."
		ArrayList<NPPhraseSpec> macronutrientiList = new ArrayList<NPPhraseSpec>();
		for(String m: macronutrienti) {
			NPPhraseSpec temp = nlgFactory.createNounPhrase(m);
			temp.setFeature(LexicalFeature.GENDER, getGender(m));
			//temp.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
			temp.setPlural(isPlural(m));
			macronutrientiList.add(temp);
		}
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		for(NPPhraseSpec elem: macronutrientiList) {
			coord.addCoordinate(elem);
		}
		coord.addPreModifier("di");

		subject.addPostModifier(coord);
		
		clause.setSubject(subject);
		
		//ADJP "quasi perfetta"
		if(!p.getObject().isEmpty()) {
			NPPhraseSpec object = nlgFactory.createNounPhrase(p.getObject().get(0));
			if(!p.getAdjp().isEmpty()) {
				object.addPreModifier(p.getAdjp().get(0));
				clause.setObject(object);
			}
		}
		return clause;
	}
	
	private SPhraseSpec createPhraseBad(Phrase p, ArrayList<String> macronutrienti) {
		SPhraseSpec clause = nlgFactory.createClause();
		//VERBO "DO"
		clause.setVerb(p.getVerb());
		if(!p.getModal().equals("")) 
			clause.setFeature(Feature.MODAL, p.getModal());
		clause.setFeature(Feature.NEGATED, p.isNegative());
		clause.setFeature(Feature.TENSE, p.getTense());
		if(p.getForm()!=null) 
			clause.setFeature(Feature.FORM, p.getForm());
		clause.setFeature(Feature.PERFECT, p.isPerfect());
		
		//SOGGETTO "YOU"
		NPPhraseSpec subject = null;
		if(p.getSubject().size()==2) 	subject = nlgFactory.createNounPhrase(p.getSubject().get(0),p.getSubject().get(1));	
		else	subject = nlgFactory.createNounPhrase(p.getSubject().get(0));
		clause.setSubject(subject);
		
		//"QUESTA SETTIMANA"
		
		NPPhraseSpec when = nlgFactory.createNounPhrase("la","settimana");
		when.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		when.addPreModifier("prossima");
		clause.addFrontModifier(when);
		
		
		//OGGETTO "A GREAT JOB"
		if(!p.getObject().isEmpty()) {
			NPPhraseSpec object = nlgFactory.createNounPhrase("un",p.getObject().get(0));
			if(!p.getAdjp().isEmpty()) {
				object.addPreModifier(p.getAdjp().get(0));
				clause.setObject(object);
			}
		}
		//"CON CEREALI, VERDURE ECC.."
		ArrayList<NPPhraseSpec> macronutrientiList = new ArrayList<NPPhraseSpec>();
		for(String m: macronutrienti) {
			String article = getArticle(m);
			NPPhraseSpec temp = nlgFactory.createNounPhrase(article,m);
			temp.setFeature(LexicalFeature.GENDER, getGender(m));
			//temp.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
			temp.setPlural(isPlural(m));
			macronutrientiList.add(temp);
			
		}
		
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		for(NPPhraseSpec elem: macronutrientiList) {
			coord.addCoordinate(elem);
		}
		coord.addPreModifier("con");

		clause.addPostModifier(coord);
			
		return clause;
	}
	
	private SPhraseSpec createPhraseVeryBad(Phrase p, ArrayList<String> macronutrienti) {
		SPhraseSpec clause = nlgFactory.createClause();
		//VERBO "DO"
		clause.setVerb(p.getVerb());
		if(!p.getModal().equals("")) 
			clause.setFeature(Feature.MODAL, p.getModal());
		clause.setFeature(Feature.NEGATED, p.isNegative());
		clause.setFeature(Feature.TENSE, p.getTense());
		if(p.getForm()!=null) 
			clause.setFeature(Feature.FORM, p.getForm());
		clause.setFeature(Feature.PERFECT, p.isPerfect());
		
		//SOGGETTO "YOU"
		NPPhraseSpec subject = null;
		if(p.getSubject().size()==2) 	subject = nlgFactory.createNounPhrase(p.getSubject().get(0),p.getSubject().get(1));	
		else	subject = nlgFactory.createNounPhrase(p.getSubject().get(0));
		clause.setSubject(subject);
		
		//"QUESTA SETTIMANA"
		
		NPPhraseSpec when = nlgFactory.createNounPhrase("la","settimana");
		when.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
		when.addPreModifier("prossima");
		clause.addFrontModifier(when);
		
		
		//OGGETTO "A GREAT JOB"
		if(!p.getObject().isEmpty()) {
			NPPhraseSpec object = nlgFactory.createNounPhrase(p.getObject().get(0));
			if(!p.getAdjp().isEmpty()) {
				object.addPreModifier(p.getAdjp().get(0));
				clause.setObject(object);
			}
		}
		//"CON CEREALI, VERDURE ECC.."
		ArrayList<NPPhraseSpec> macronutrientiList = new ArrayList<NPPhraseSpec>();
		for(String m: macronutrienti) {
			String article = getArticle(m);
			NPPhraseSpec temp = nlgFactory.createNounPhrase(article,m);
			temp.setFeature(LexicalFeature.GENDER, getGender(m));
			//temp.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
			temp.setPlural(isPlural(m));
			macronutrientiList.add(temp);
			
		}
				
		return clause;
	}
	
	private void extractUtente(JsonObject jsonObject) {
		nomeUtente = jsonObject.getString("nome utente");
		String sesso = jsonObject.getString("sesso utente");
		if(sesso.equals("M")) sessoUtente = Gender.MASCULINE;
		else sessoUtente = Gender.FEMININE;
	}

	/*
	private void extractCategories(JsonObject jsonObject) {
		getVeryGoodMoreFeedback(jsonObject);
		getVeryGoodLessFeedback(jsonObject);
		getGoodMoreFeedback(jsonObject);
		getGoodLessFeedback(jsonObject);
		getBadMoreFeedback(jsonObject);
		getBadLessFeedback(jsonObject);
		getVeryBadMoreFeedback(jsonObject);
		getVeryBadLessFeedback(jsonObject);
	}
	private void getVeryBadMoreFeedback(JsonObject jsonObject) {
		veryBadMore = jsonObject.getJsonArray("very bad more");
		//System.out.println(veryBadMore.getString(0));
		//System.out.println(veryBadMore);
	}
	
	private void getVeryBadLessFeedback(JsonObject jsonObject) {
		veryBadLess = jsonObject.getJsonArray("very bad less");
		//System.out.println(veryBadLess.getString(0));
		//System.out.println(veryBadLess);
	}
	
	private void getBadMoreFeedback(JsonObject jsonObject) {
		badMore = jsonObject.getJsonArray("bad more");
		//System.out.println(badMore.getString(0));
		//System.out.println(badMore);
	}
	
	private void getBadLessFeedback(JsonObject jsonObject) {
		badLess = jsonObject.getJsonArray("bad less");
		//System.out.println(badLess.getString(0));
		//System.out.println(badLess);
	}

	private void getGoodMoreFeedback(JsonObject jsonObject) {
		goodMore = jsonObject.getJsonArray("good more");
		//System.out.println(goodMore.getString(0));
		//System.out.println(goodMore);
	}
	
	private void getGoodLessFeedback(JsonObject jsonObject) {
		goodLess = jsonObject.getJsonArray("good less");
		//System.out.println(goodLess.getString(0));
		//System.out.println(goodLess);
	}
	
	private void getVeryGoodMoreFeedback(JsonObject jsonObject) {
		veryGoodMore = jsonObject.getJsonArray("very good more");
		//System.out.println(veryGoodMore.getString(0));
		//System.out.println(veryGoodMore);
	}
	
	private void getVeryGoodLessFeedback(JsonObject jsonObject) {
		veryGoodLess = jsonObject.getJsonArray("very good less");
		//System.out.println(veryGoodLess.getString(0));
		//System.out.println(veryGoodLess);
	}*/
	
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
	
	private boolean isPlural(String macronutriente) {
		boolean ret = true;
		if(macronutriente.equals("pesce") || macronutriente.equals("carne rossa") || macronutriente.equals("frutta") 
				|| macronutriente.equals("verdura") || macronutriente.equals("pollame") || macronutriente.equals("olio")) 
			ret = false;
		return ret;
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
	  
}
