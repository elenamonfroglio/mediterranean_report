package monfroglio.elena.model;

public class Macronutriente {
	public String nome;
	public int punteggio;
	public boolean moreIsBetter;
	public boolean goodForEnvironment;
	
	public Macronutriente(String nome, int punteggio, boolean moreisbetter, boolean goodForEnvironment) {
		this.nome = nome;
		this.punteggio = punteggio;
		this.moreIsBetter = moreisbetter;
		this.goodForEnvironment = goodForEnvironment;
	}
	
	public String toString() {
		String ret = nome + ": "+punteggio;
		if(moreIsBetter) ret += ", more is better";
		else	ret += ", less is better";
		return ret;
	}
	
	public void print() {
		System.out.println(this);
	}
	
}
