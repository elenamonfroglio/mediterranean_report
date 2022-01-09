package monfroglio.elena.model;

public class Macronutriente {
	public String nome;
	public int punteggio;
	public boolean moreisbetter;
	
	public Macronutriente(String nome, int punteggio, boolean moreisbetter) {
		this.nome = nome;
		this.punteggio = punteggio;
		this.moreisbetter = moreisbetter;
	}
	
	public String toString() {
		String ret = nome + ": "+punteggio;
		if(moreisbetter) ret += ", more is better";
		else	ret += ", less is better";
		return ret;
	}
	
	public void print() {
		System.out.println(this);
	}
	
}
