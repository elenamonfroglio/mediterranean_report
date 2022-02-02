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
import java.util.HashMap;
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
				myLexicon = new simplenlg.lexicon.english.XMLLexicon();;
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
		System.out.print("\n\n");
		//NLGElement welcome = createPhraseWelcome();
		String output = "";
		//if(welcome!=null)	output = realiser.realiseSentence(welcome);
	    
		System.out.print(output+" ");
	    
		for(Phrase p:phrases) {
			ArrayList<String> macronutrienti = new ArrayList<>();
			for(int i=0;i<p.getPhraseArgs().size();i++) {
				macronutrienti.add(p.getPhraseArgs().get(i));
			}
			SPhraseSpec clause = createGenericPhrase(p);
			output = "";
			if(clause!=null)	output = realiser.realiseSentence(clause);
		    System.out.print(output+" ");
		}
	}
	
	private SPhraseSpec createGenericPhrase(Phrase p) {
		SPhraseSpec clause = nlgFactory.createClause();
		
		//VERBO 
		if(p.getVerb()!="") {
			createAndSetVerb(clause,p);
		}
		
		//SOGGETTO
		NPPhraseSpec subject = createAndSetSubject(clause,p);
		
		//OGGETTO
		if(!p.getObject().isEmpty()) {
			createAndSetObject(clause,p);
		}
		else if(!p.getAdjp().isEmpty()) {
			createAndSetAdjp(clause,p);
		}
		//"CON CEREALI, VERDURE ECC.."
		if(!p.getSubjectArgs().isEmpty() && p.getSubjectArgs()!=null) 
			createAndSetArgs(clause,p,p.getSubjectArgs(),subject);
		
		if(!p.getPhraseArgs().isEmpty() && p.getPhraseArgs()!=null) 
			createAndSetArgs(clause,p,p.getPhraseArgs(),null);			
		
		
		if(p.getCoordinatedPhrase()!=null) {
			CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
			SPhraseSpec clauseRecursive = createGenericPhrase(p.getCoordinatedPhrase());
			//coord.addCoordinate(clause);
			clauseRecursive.addFrontModifier(p.getConjunction());
			coord.addCoordinate(clauseRecursive);
			//coord.setConjunction(p.getConjunction());
			clause.addPostModifier(coord);
		}
		
		clause.addFrontModifier(p.getPreModifierPhrase());
			
		return clause;
	}
	
	private VPPhraseSpec createAndSetVerb(SPhraseSpec clause, Phrase p) {
		VPPhraseSpec verb = nlgFactory.createVerbPhrase(p.getVerb());
		//verb.setFeature(Feature.AGGREGATE_AUXILIARY, p.getAuxiliary());
		if(p.getType()==PhraseType.EXCLAMATION) {
			clause.addPostModifier("!");
			clause.setFeature(Feature.FORM, Form.INFINITIVE);
		}
		clause.setVerb(verb);
		if(!p.getModal().equals("")) 
			clause.setFeature(Feature.MODAL, p.getModal());
		clause.setFeature(Feature.NEGATED, p.isNegative());
		clause.setFeature(Feature.TENSE, p.getTense());
		if(p.getForm()!=null) 
			clause.setFeature(Feature.FORM, p.getForm());
		clause.setFeature(Feature.PERFECT, p.isPerfect());
		return verb;
	}
	
	private NPPhraseSpec createAndSetSubject(SPhraseSpec clause, Phrase p) {
		NPPhraseSpec subject = nlgFactory.createNounPhrase("");
		if(p.getSubject().isEmpty())	subject.setFeature(Feature.PERSON, Person.SECOND);
		else if(p.getSubject().size()==2) 	subject = nlgFactory.createNounPhrase(p.getSubject().get(0),p.getSubject().get(1));	
		else	subject = nlgFactory.createNounPhrase(p.getSubject().get(0));

		clause.setSubject(subject);
		return subject;
	}
	
	private CoordinatedPhraseElement createAndSetObject(SPhraseSpec clause, Phrase p) {
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		int i = 0;
		for(String m: p.getObject()) {
			NPPhraseSpec obj = nlgFactory.createNounPhrase(m);
			if(!p.getAdjp().isEmpty()) {
				obj.addPreModifier(p.getAdjp().get(i));
				i++;
			}
			coord.addCoordinate(obj);		
		}				
		clause.setObject(coord);
		return coord;
	}
	
	private AdjPhraseSpec createAndSetAdjp(SPhraseSpec clause, Phrase p) {
		AdjPhraseSpec adjPhrase = nlgFactory.createAdjectivePhrase(p.getAdjp().get(0));
		adjPhrase.setFeature(LexicalFeature.GENDER, p.getAdjpGender());
		clause.addModifier(adjPhrase);
		return adjPhrase;
	}
	
	private CoordinatedPhraseElement createAndSetArgs(SPhraseSpec clause, Phrase p, ArrayList<String> args, PhraseElement elemToBeModfied) {
		ArrayList<NPPhraseSpec> macronutrientiList = new ArrayList<NPPhraseSpec>();
		
		for(String m: args) {
			NPPhraseSpec temp = nlgFactory.createNounPhrase(m);
			temp.setFeature(LexicalFeature.GENDER, getGender(m));
			temp.setPlural(isPlural(m));
			macronutrientiList.add(temp);
		}
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		for(NPPhraseSpec elem: macronutrientiList) {
			coord.addCoordinate(elem);
		}
		coord.addPreModifier(p.getPostModifierSubject());

		if(elemToBeModfied==null) clause.addPostModifier(coord);
		else	elemToBeModfied.addPostModifier(coord);
		return coord;
	}
	
	private CoordinatedPhraseElement createAndSetPhraseArgs(SPhraseSpec clause, Phrase p) {
		ArrayList<NPPhraseSpec> macronutrientiList = new ArrayList<NPPhraseSpec>();		
		
		for(String m: p.getPhraseArgs()) {
			NPPhraseSpec temp = nlgFactory.createNounPhrase(m);
			temp.setFeature(LexicalFeature.GENDER, getGender(m));
			temp.setPlural(isPlural(m));
			macronutrientiList.add(temp);
		}
		
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		for(NPPhraseSpec elem: macronutrientiList) {
			coord.addCoordinate(elem);
		}
		coord.addPreModifier(p.getPostModifierPhrase());

		clause.addPostModifier(coord);
		return coord;
	}
	
	private void extractUtente(JsonObject jsonObject) {
		nomeUtente = jsonObject.getString("nome utente");
		String sesso = jsonObject.getString("sesso utente");
		if(sesso.equals("M")) sessoUtente = Gender.MASCULINE;
		else sessoUtente = Gender.FEMININE;
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
			
		} catch (FileNotFoundException e) {
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
	  
}

