package monfroglio.elena.model;

import java.util.ArrayList;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Gender;
import simplenlg.features.Tense;

public class Phrase {
	String premodifierPhrase = "";
	String type = "";
	Tense tense = Tense.PRESENT;
	boolean perfect = false;
	Form form = Form.NORMAL;
	boolean isActive = true;
	boolean isNegative = false;
	boolean formal = false;
	ArrayList<String> subject = new ArrayList<>();
	ArrayList<String> subjectArgs = new ArrayList<>();
	String postmodifierSubject = "";
	String modal = "";
	String verb = "";
	ArrayList<String> object = new ArrayList<>();
	ArrayList<String> adjp = new ArrayList<>();
	Gender adjpGender = Gender.MASCULINE;
	String postmodifierPhrase = "";
	ArrayList<String> phraseArgs = new ArrayList<>();
	String conjunction = "";
	Phrase coordinatedPhrase = null;
	
	public Phrase(Phrase p) {
		this.premodifierPhrase = p.premodifierPhrase;
		this.type = p.type;
		this.tense = p.tense;
		this.perfect = p.perfect;
		this.form = p.form;
		this.isActive = p.isActive;
		this.isNegative = p.isNegative;
		this.subject = p.subject;
		this.subjectArgs = p.subjectArgs;
		this.postmodifierSubject = p.postmodifierSubject;
		this.modal = p.modal;
		this.verb = p.verb;
		this.object = p.object;
		this.adjp = p.adjp;
		this.adjpGender = p.adjpGender;
		this.postmodifierPhrase = p.postmodifierPhrase;
		this.phraseArgs = p.phraseArgs;
		this.conjunction = p.conjunction;
		this.coordinatedPhrase = p.coordinatedPhrase;
	}
	
	//modificare per snellire metodi Sentence Planner. Settare a null tutti gli altri campi
	public Phrase(String type, Tense tense, boolean isActive, boolean isNegative, ArrayList<String> subject, String verb, ArrayList<String> object, ArrayList<String> phraseArgs) {
		this.type = type;
		this.tense = tense;
		this.perfect = false;
		this.form = Form.NORMAL;
		this.isActive = isActive;
		this.isNegative = isNegative;
		this.subject = subject;
		this.verb = verb;
		this.object = object;
		this.phraseArgs = phraseArgs;
	}
	
	public Phrase(String type, ArrayList<String> subject, String verb, ArrayList<String> object, ArrayList<String> phraseArgs) {
		this.type = type;
		this.subject = subject;
		this.verb = verb;
		this.object = object;
		this.phraseArgs = phraseArgs;
		this.adjp = new ArrayList<>();
		this.adjpGender = Gender.MASCULINE;
	}

	public Phrase(String type, ArrayList<String> subject, String verb, ArrayList<String> object) {
		this.type = type;
		this.subject = subject;
		this.verb = verb;
		this.object = object;
		this.adjp = new ArrayList<>();
		this.adjpGender = Gender.MASCULINE;
	}
	
	public Phrase() {
		this.adjp = new ArrayList<>();
		this.adjpGender = Gender.MASCULINE;
		
	}
	
	
	public void setPostModifierSubject(String postmodifierSubject) {
		this.postmodifierSubject = postmodifierSubject;
	}

	public void setPostModifierPhrase(String postmodifierPhrase) {
		this.postmodifierPhrase = postmodifierPhrase;
	}
	
	public void setPreModifierPhrase(String premodifierPhrase) {
		this.premodifierPhrase = premodifierPhrase;
	}
	
	public void setFormal(boolean formal) {
		this.formal = formal;
	}
	
	public void setTense(Tense tense) {
		this.tense = tense;
	}
	
	public void setVerb(String verb) {
		this.verb = verb;
	}
	
	public void setObject(ArrayList<String> object) {
		this.object = object;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}
	
	public void setAdjp(ArrayList<String> adjp) {
		this.adjp = adjp;
	}
	
	public void setAdjpGender(Gender adjpGender) {
		this.adjpGender = adjpGender;
	}

	public void setPhraseArgs(ArrayList<String> phraseArgs) {
		this.phraseArgs = phraseArgs;
	}

	public void setSubjectArgs(ArrayList<String> subjectArgs) {
		this.subjectArgs = subjectArgs;
	}
	
	public void setModal(String modal) {
		this.modal = modal;
	}
	
	public void setSubject(ArrayList<String> subject) {
		this.subject = subject;
	}

	public void setForm(Form form) {
		this.form = form;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
	}
	
	public void setConjunction(String conjunction) {
		this.conjunction = conjunction;
	}
	
	public void setCoordinatedPhrase(Phrase coordinatedPhrase) {
		this.coordinatedPhrase = coordinatedPhrase;
	}

	public String getPreModifierPhrase() {
		return premodifierPhrase;
	}
	
	public String getPostModifierSubject() {
		return postmodifierSubject;
	}

	public String getPostModifierPhrase() {
		return postmodifierPhrase;
	}
	
	public Tense getTense() {
		return tense;
	}
	
	public Form getForm() {
		return form;
	}
	
	public String getModal() {
		return modal;
	}
	
	public boolean isFormal() {
		return formal;
	}
	
	public String getType() {
		return type;
	}
	
	public String getVerb() {
		return verb;
	}
	
	public ArrayList<String> getSubject() {
		return subject;
	}
	
	public ArrayList<String> getObject() {
		return object;
	}
	
	public Gender getAdjpGender() {
		return adjpGender;
	}
	
	public ArrayList<String> getAdjp() {
		return adjp;
	}
	
	public ArrayList<String> getPhraseArgs() {
		return phraseArgs;
	}
	
	public ArrayList<String> getSubjectArgs() {
		return subjectArgs;
	}
	
	public String getConjunction() {
		return conjunction;
	}
	
	public Phrase getCoordinatedPhrase() {
		return coordinatedPhrase;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public boolean isNegative() {
		return isNegative;
	}
	
	public boolean isPerfect() {
		return perfect;
	}
}
