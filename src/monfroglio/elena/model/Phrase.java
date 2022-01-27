package monfroglio.elena.model;

import java.util.ArrayList;

import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;

public class Phrase {
	String type;
	Tense tense;
	boolean perfect;
	Form form;
	boolean isActive;
	boolean isNegative;
	ArrayList<String> subject;
	String modal;
	String verb;
	ArrayList<String> object;
	ArrayList<String> adjp;
	ArrayList<String> args;
	
	public Phrase(Phrase p) {
		this.type = p.type;
		this.tense = p.tense;
		this.perfect = p.perfect;
		this.form = p.form;
		this.isActive = p.isActive;
		this.isNegative = p.isNegative;
		this.subject = p.subject;
		this.modal = p.modal;
		this.verb = p.verb;
		this.object = p.object;
		this.adjp = p.adjp;
		this.args = p.args;
	}
	
	public Phrase(String type, Tense tense, boolean isActive, boolean isNegative, ArrayList<String> subject, String verb, ArrayList<String> object, ArrayList<String> args) {
		this.type = type;
		this.tense = tense;
		this.isActive = isActive;
		this.isNegative = isNegative;
		this.subject = subject;
		this.verb = verb;
		this.object = object;
		this.args = args;
	}
	
	public Phrase(String type, ArrayList<String> subject, String verb, ArrayList<String> object, ArrayList<String> args) {
		this.type = type;
		this.subject = subject;
		this.verb = verb;
		this.object = object;
		this.args = args;
	}
	
	public void setTense(Tense tense) {
		this.tense = tense;
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

	public void setArgs(ArrayList<String> args) {
		this.args = args;
	}
	
	public void setModal(String modal) {
		this.modal = modal;
	}

	public void setForm(Form form) {
		this.form = form;
	}
	
	public void setPerfect(boolean perfect) {
		this.perfect = perfect;
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
	
	public ArrayList<String> getAdjp() {
		return adjp;
	}
	
	public ArrayList<String> getArgs() {
		return args;
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
