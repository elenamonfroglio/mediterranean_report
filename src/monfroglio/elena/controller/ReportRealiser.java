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
	private MultipleLexicon lexicons;
	private NLGFactory nlgFactory;
	private Realiser realiser;
	private JsonObject jsonObject;
	public ArrayList<Phrase> phrases;
	
	public ReportRealiser(String fileName, ArrayList<Phrase> phrases) {
		this.fileName = fileName;	
		jsonObject = readJson();
		lingua = jsonObject.getString("lingua");
		Lexicon myLexicon = new ITXMLLexicon();
		Lexicon foodLexicon = new XMLLexicon("src/monfroglio/elena/files/lexicon/FoodLexiconIta.xml");
		
		switch(lingua){
			case "italiano":
				myLexicon = new ITXMLLexicon();
				foodLexicon = new XMLLexicon("src/monfroglio/elena/files/lexicon/FoodLexiconIta.xml");
				lexicons = new MultipleLexicon("it");
				break;
			case "english":
				myLexicon = new simplenlg.lexicon.english.XMLLexicon();;
				foodLexicon = new XMLLexicon("src/monfroglio/elena/files/lexicon/FoodLexiconEng.xml");
				lexicons = new MultipleLexicon("en");
				break;
		}
		this.phrases = phrases;
		lexicons.addInitialLexicon(myLexicon);
		lexicons.addFinalLexicon(foodLexicon);
		nlgFactory = new NLGFactory(lexicons);
		realiser = new Realiser();
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void createLongSentence_new() {
		
		extractUtente(jsonObject);
		System.out.print("\n\n");
		String output = "";
	    
		System.out.print(output+" ");
	    
		for(Phrase p:phrases) {
			ArrayList<String> macronutrienti = new ArrayList<>();
			for(int i=0;i<p.getPhraseArgs().size();i++) {
				macronutrienti.add(p.getPhraseArgs().get(i));
			}
			SPhraseSpec clause = createGenericPhrase(p);
			output = "";
			if(clause!=null)	output = realiser.realiseSentence(clause);
			if(p.getType().equals(PhraseType.EXCLAMATION))	output = (output.substring(0, output.length() - 1));
		    if(output.length()>18) {
				String substring1 = output.substring(0,18).replace(",", "");
				String substring2 = output.substring(18);
			    output = substring1+substring2;
		    }
			System.out.print(output+" ");
		}
	}
	
	private SPhraseSpec createGenericPhrase(Phrase p) {
		SPhraseSpec clause = nlgFactory.createClause();

		//SOGGETTO
		NPPhraseSpec subject = null;
		if(!p.getSubject().isEmpty())	
			subject = createAndSetSubject(clause,p);
		
		//VERBO 
		if(p.getVerb()!="") 
			createAndSetVerb(clause,p);
		
		//OGGETTO
		NLGElement object = null;
		if(!p.getObject().isEmpty()) {
			if(p.getRelativeObjectPhrase()!=null)		
				object = createAndSetObjectRelativePhrase(clause,p);
			else	object = createAndSetObject(clause,p);
		}
		else if(!p.getAdjp().isEmpty()) 
			createAndSetAdjp(clause,p);
		
		//"CON CEREALI, VERDURE ECC.."
		if(p.getSubjectArgs()!=null && !p.getSubjectArgs().isEmpty()) 
			createAndSetArgs(clause,p,p.getSubjectArgs(),subject,p.getPostModifierSubject());
		
		
		if(p.getPhraseArgs()!=null && !p.getPhraseArgs().isEmpty()) 
			createAndSetArgs(clause,p,p.getPhraseArgs(),null,p.getPostModifierPhrase());			
		
		if(p.getCoordinatedPhrase()!=null) 
			createAndSetCoordinatedPhrase(clause,p);
		
		if(p.getRelativeObjectPhrase()!=null)
			createAndSetRelativePhrase(clause,p.getRelativeObjectPhrase(),object);
		if(p.getRelativeSubjectPhrase()!=null)
			createAndSetRelativePhrase(clause,p.getRelativeSubjectPhrase(),subject);
		
		clause.addFrontModifier(p.getPreModifierPhrase());
			
		return clause;
	}
	
	private VPPhraseSpec createAndSetVerb(SPhraseSpec clause, Phrase p) {
		VPPhraseSpec verb = nlgFactory.createVerbPhrase(p.getVerb());
		//verb.setFeature(Feature.AGGREGATE_AUXILIARY, p.getAuxiliary());
		if(p.getType()==PhraseType.EXCLAMATION) {
			clause.addPostModifier("!");
			if(!p.isFormal())	
				clause.setFeature(Feature.FORM, Form.INFINITIVE);
			else {				
				clause.setFeature(Feature.FORM, Form.SUBJUNCTIVE);
				clause.setFeature(Feature.PERSON, Person.THIRD);
			}
		}else if(p.getForm()!=null) 
			clause.setFeature(Feature.FORM, p.getForm());
		if(!p.getModal().equals("")) {
			clause.setFeature(Feature.MODAL, p.getModal());
			if(lingua.equals("english"))
				verb.setFeature(Feature.PERSON, Person.SECOND);
		}
		verb.setFeature(Feature.PERSON, Person.THIRD);
		clause.setFeature(Feature.NEGATED, p.isNegative());
		clause.setFeature(Feature.TENSE, p.getTense());
		clause.setFeature(Feature.PERFECT, p.isPerfect());
		clause.setVerb(verb);
		return verb;
	}
	
	private NPPhraseSpec createAndSetSubject(SPhraseSpec clause, Phrase p) {
		NPPhraseSpec subject = nlgFactory.createNounPhrase("");
		if(p.getSubject().isEmpty())	{
			if(!p.isFormal())		subject.setFeature(Feature.PERSON, Person.SECOND);
			else					subject.setFeature(Feature.PERSON, Person.THIRD);
			
		}
		else if(p.getSubject().size()==2) 	subject = nlgFactory.createNounPhrase(p.getSubject().get(0),p.getSubject().get(1));	
		else{
			subject = nlgFactory.createNounPhrase(p.getSubject().get(0));
			if(!p.getSubjectArticle().equals("")) {
				subject.setSpecifier(p.getSubjectArticle());
				if(p.getSubjectGender()!=null)	subject.setFeature(LexicalFeature.GENDER,p.getSubjectGender());
			}
			if(!p.getSubjectAdjp().equals(""))	subject.addPreModifier(p.getSubjectAdjp());
			//subject.addModifier(p.getAdjp());
		}
		subject.setPlural(p.getSubjectPlural());
		clause.setSubject(subject);
		return subject;
	}
	
	private CoordinatedPhraseElement createAndSetObject(SPhraseSpec clause, Phrase p) {
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		int i = 0;
		NPPhraseSpec obj = null;
		for(String m: p.getObject()) {
			obj = nlgFactory.createNounPhrase(m);
			if(!p.getAdjp().isEmpty()) {
				//scelgo se mettere l'aggettivo prima o dopo l'oggetto
				if(lingua.equals("english"))	obj.addPreModifier(p.getAdjp().get(i));
				else							obj.addModifier(p.getAdjp().get(i));
			}
			WordElement e = lexicons.getWord(m);
			boolean b = lexicons.hasWord(m);
			WordElement id = lexicons.lookupWord(m);
			Object o1 = e.getFeature(LexicalFeature.GENDER);
			Object o2 = id.getFeature(LexicalFeature.GENDER);
			if(!p.getPreModifierObject().isEmpty()) {
				AdjPhraseSpec adjPreModifier = nlgFactory.createAdjectivePhrase(p.getPreModifierObject().get(i));
				if(p.getAdjpGender()!=null)	adjPreModifier.setFeature(LexicalFeature.GENDER, p.getAdjpGender());	
				adjPreModifier.setPlural(p.getObjectIsPlural());
				obj.addPreModifier(adjPreModifier);
				
				i++;
			}
			coord.addCoordinate(obj);		
		}
		if(p.getObjectArgs()!=null && !p.getObjectArgs().isEmpty()) 
			createAndSetArgs(clause,p,p.getObjectArgs(),obj,p.getPostModifierPhrase());
		if(p.getObject().size()==1 && !p.getObjectArticle().equals(""))	obj.setSpecifier(p.getObjectArticle());;
		clause.setObject(coord);
		return coord;
	}
	
	private PPPhraseSpec createAndSetObjectRelativePhrase(SPhraseSpec clause, Phrase p) {
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		int i = 0;
		NPPhraseSpec obj = null;
		for(String m: p.getObject()) {
			obj = nlgFactory.createNounPhrase(m);
			if(!p.getAdjp().isEmpty()) {
				//scelgo se mettere l'aggettivo prima o dopo l'oggetto
				obj.addModifier(p.getAdjp().get(i));
			}
			if(!p.getPreModifierObject().isEmpty()) {
				AdjPhraseSpec adjPreModifier = nlgFactory.createAdjectivePhrase(p.getPreModifierObject().get(i));
				obj.addPreModifier(adjPreModifier);
				i++;
			}
			coord.addCoordinate(obj);		
		}
		if(p.getObjectArgs()!=null && !p.getObjectArgs().isEmpty()) 
			createAndSetArgs(clause,p,p.getObjectArgs(),obj,p.getPostModifierPhrase());
		if(p.getObject().size()==1 && !p.getObjectArticle().equals(""))	obj.setSpecifier(p.getObjectArticle());;
		clause.setObject(coord);
		PPPhraseSpec ret = nlgFactory.createPrepositionPhrase("di",coord);
		return ret;
	}
	
	private ArrayList<AdjPhraseSpec> createAndSetAdjp(SPhraseSpec clause, Phrase p) {
		ArrayList<AdjPhraseSpec> list = new ArrayList<>();
		for(String adj:p.getAdjp()) {
			AdjPhraseSpec adjPhrase = nlgFactory.createAdjectivePhrase(adj);
			list.add(adjPhrase);
			if(p.getAdjpGender()!=null) adjPhrase.setFeature(LexicalFeature.GENDER, p.getAdjpGender());
			//adjPhrase.setFeature(LexicalFeature.GENDER, p.getAdjpGender());
			//if(!p.getPreModifierObject().isEmpty())		adjPhrase.addPreModifier(p.getPreModifierObject());
			clause.addModifier(adjPhrase);
		}
		
		return list;
	}
	
	private CoordinatedPhraseElement createAndSetArgs(SPhraseSpec clause, Phrase p, ArrayList<String> args, NLGElement elemToBeModfied, String particle) {
		ArrayList<NPPhraseSpec> macronutrientiList = new ArrayList<NPPhraseSpec>();
		boolean plural;
		
		for(String m: args) {
			NPPhraseSpec temp = nlgFactory.createNounPhrase(m);
			if(getGender(m)!=null) {
				temp.setFeature(LexicalFeature.GENDER, getGender(m));
				temp.setPlural(isPlural(m));
				plural = isPlural(m);
				if(args.size()==1 && !p.getArgsArticle().equals(""))	temp.setSpecifier(p.getArgsArticle());
				
			}
			
			macronutrientiList.add(temp);
		}
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();

		if(macronutrientiList.size()!=1) {
			for(NPPhraseSpec elem: macronutrientiList) {
				coord.addCoordinate(elem);
			}
		}else {
			coord.addCoordinate(macronutrientiList.get(0));
		}		

		if(elemToBeModfied==null) { //modifier of Clause
			coord.addPreModifier(particle);
			clause.addPostModifier(coord);
		}
		else { //modifier of subject or object
			coord.addPreModifier(particle);
			if(elemToBeModfied instanceof CoordinatedPhraseElement)
				((CoordinatedPhraseElement) elemToBeModfied).addPostModifier(coord);			
			else
				((NPPhraseSpec) elemToBeModfied).addPostModifier(coord);
		}
		return coord;
	}
	
	private CoordinatedPhraseElement createAndSetCoordinatedPhrase(SPhraseSpec clause, Phrase p) {
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		SPhraseSpec clauseRecursive = createGenericPhrase(p.getCoordinatedPhrase());
		
		clauseRecursive.addFrontModifier(p.getConjunction());
		//clauseRecursive.setFeature(ItalianLexicalFeature.NO_COMMA, false);
		coord.addCoordinate(clauseRecursive);
		
		
		clause.addPostModifier(coord);
		return coord;
	}
	
	private SPhraseSpec createAndSetRelativePhrase(SPhraseSpec clause, Phrase p, NLGElement elemToBeModified) {
		NPPhraseSpec np = nlgFactory.createNounPhrase(elemToBeModified);
		//CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		SPhraseSpec clauseRelative = createGenericPhrase(p);
		//PPPhraseSpec pp = nlgFactory.createPrepositionPhrase(elemToBeModified);
		
		//PPPhraseSpec pp = nlgFactory.createPrepositionPhrase(elemToBeModified);
		//clauseRecursive.addModifier(elemToBeModified);
		clauseRelative.setFeature(ItalianFeature.RELATIVE_PHRASE, np);
		//clauseRecursive.setFeature(ItalianLexicalFeature.NO_COMMA, false);
		
		clause.addPostModifier(clauseRelative);
		return clauseRelative;
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
		Gender ret = null;
		if(macronutriente.equals("patate") || macronutriente.equals("carne rossa") || macronutriente.equals("frutta") 
				|| macronutriente.equals("verdura")) 
			ret = Gender.FEMININE;
		else if(macronutriente.equals("cereali") || macronutriente.equals("legumi") || macronutriente.equals("pesce") 
				|| macronutriente.equals("pollame") || macronutriente.equals("latticini") 
				|| macronutriente.equals("olio"))
			ret = Gender.MASCULINE;
		return ret;
	}
		
	private boolean isPlural(String macronutriente) {
		boolean ret = false;
		if(macronutriente.equals("pesce") || macronutriente.equals("carne rossa") || macronutriente.equals("frutta") 
				|| macronutriente.equals("verdura") || macronutriente.equals("pollame") || macronutriente.equals("olio")
				|| macronutriente.equals("carne bovina") || macronutriente.equals("fish") || macronutriente.equals("red meat")
				|| macronutriente.equals("meat") || macronutriente.equals("fruit") || macronutriente.equals("poultry")
				|| macronutriente.equals("oil") || macronutriente.equals("white meat")) 
			ret = false;
		else if(macronutriente.equals("legumi") || macronutriente.equals("latticini") || macronutriente.equals("patate") 
				|| macronutriente.equals("cereali") || macronutriente.equals("vegetable") || macronutriente.equals("cereal")
				|| macronutriente.equals("potato"))
			ret = true;
		return ret;
	}
	  
}

