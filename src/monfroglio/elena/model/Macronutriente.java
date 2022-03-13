package monfroglio.elena.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Macronutriente {
	private String nome;
	private int punteggio = -1;
	private double punteggioEnvironment = -1;
	private boolean moreIsBetter = false;
	private boolean goodForEnvironment = false;
	
	public Macronutriente(String nome, int punteggio, double punteggioEnvironment, boolean moreisbetter, boolean goodForEnvironment) {
		this.nome = nome;
		this.punteggio = punteggio;
		this.punteggioEnvironment = punteggioEnvironment;
		this.moreIsBetter = moreisbetter;
		this.goodForEnvironment = goodForEnvironment;
	}
	
	public Macronutriente(String nome, int punteggio, double punteggioEnvironment, boolean moreisbetter) {
		this.nome = nome;
		this.punteggio = punteggio;
		this.punteggioEnvironment = punteggioEnvironment;
		this.moreIsBetter = moreisbetter;
	}
	
	public Macronutriente(String nome) {
		this.nome = nome;
		
	}
	
	public String toString() {
		String ret = nome + ": "+punteggio+"\nemissioni carbonio: "+punteggioEnvironment;
		if(moreIsBetter) ret += ", more is better";
		else	ret += ", less is better";
		return ret;
	}
	
	public boolean isMoreBetter(HashMap<String, ArrayList<String>> dictionary) {
		boolean ret = true;
		
		if(isThisType("rmeat",dictionary) || 
				isThisType("poul",dictionary) || isThisType("ffdp",dictionary))
			ret = false;
		
		return ret;
	}

	public static boolean isMoreBetter(String type) {
		boolean ret = true;
		
		if(type.equals("CarneRossa") || 
			type.equals("Pollame") || 
			type.equals("Latticini") ||
			type.equals(MacronutrienteType.CARNEROSSA) ||
			type.equals(MacronutrienteType.POLLAME) ||
			type.equals(MacronutrienteType.LATTICINI))
			ret = false;
		
		return ret;
	}
	
	public void print() {
		System.out.println("\n\n\n" +this);
	}
	
	public String getNome() {
		return nome;
	}
	
	public int getPunteggio() {
		return punteggio;
	}
	
	public double getPunteggioEnvironment() {
		return punteggioEnvironment;
	}
	
	public boolean getMoreIsBetter() {
		return moreIsBetter;
	}
	
	public boolean getGoodForEnvironment() {
		return goodForEnvironment;
	}
	
	public boolean isThisType(String type, HashMap<String, ArrayList<String>> dictionary) {
		ArrayList<String> list = dictionary.get(type);
		boolean ret = false;
		for(String word:list) {
			if(word.replace("_", " ").equals(nome))	return true;
		}
		return ret;
	}
	
	public boolean equalsName(String m) {
		if(nome.equals(m))	return true;
		else if(m.equals("cer") && nome.equals("Cereali"))	return true;
		else if(nome.equals("Cereali") && m.equals("cer"))	return true;
		else if(m.equals("pot") && nome.equals("Patate"))	return true;
		else if(nome.equals("Patate") && m.equals("pot"))	return true;
		else if(m.equals("fru") && nome.equals("Frutta"))	return true;
		else if(nome.equals("Frutta") && m.equals("fru"))	return true;
		else if(m.equals("veg") && nome.equals("Verdura"))	return true;
		else if(nome.equals("Verdura") && m.equals("veg"))	return true;
		else if(m.equals("leg") && nome.equals("Legumi"))	return true;
		else if(nome.equals("Legumi") && m.equals("leg"))	return true;
		else if(m.equals("fish") && nome.equals("Pesce"))	return true;
		else if(nome.equals("Pesce") && m.equals("fish"))	return true;
		else if(m.equals("rmeat") && nome.equals("CarneRossa"))	return true;
		else if(nome.equals("CarneRossa") && m.equals("rmeat"))	return true;
		else if(m.equals("poul") && nome.equals("Pollame"))	return true;
		else if(nome.equals("Pollame") && m.equals("poul"))	return true;
		else if(m.equals("ffdp") && nome.equals("Latticini"))	return true;
		else if(nome.equals("Latticini") && m.equals("ffdp"))	return true;
		else if(m.equals("oil") && nome.equals("UsoOlioOliva"))	return true;
		else if(nome.equals("UsoOlioOliva") && m.equals("oil"))	return true;
		else return false;
	}
	
}
