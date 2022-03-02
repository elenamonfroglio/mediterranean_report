package monfroglio.elena.model;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Pasto {
	private String nome = "";
	private int idRicetta = -1;
	private Time orario;
	private Date giorno;
	private int slot;
	private HashMap<String,Float> punteggi;
	//https://jdbc.postgresql.org/documentation/head/8-date-time.html
	
	public Pasto(String nome, Time orario, Date giorno, int slot) {
		this.nome = nome;
		this.orario = orario;
		this.giorno = giorno;
		this.slot = slot;
	}
	
	public Pasto(int idRicetta, Time orario, Date giorno, int slot) {
		this.idRicetta = idRicetta;
		this.orario = orario;
		this.giorno = giorno;
		this.slot = slot;
	}
	
	public Pasto(int idRicetta, Time orario, Date giorno, int slot, HashMap<String,Float> punteggi) {
		this.idRicetta = idRicetta;
		this.orario = orario;
		this.giorno = giorno;
		this.slot = slot;
		this.punteggi = punteggi;
	}
	
	public Pasto(String nome) {
		this.nome = nome;
		this.orario = null;
		this.giorno = null;
		this.slot = -1;
	}
	
	public int getIdRicetta() {
		return idRicetta;
	}
	
	public Date getGiorno() {
		return giorno;
	}
	
	public HashMap<String,Float> getPunteggi(){
		return punteggi;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setPunteggi(HashMap<String,Float> punteggi) {
		this.punteggi = punteggi;
	}
	
	public void print() {
		System.out.println(this);
	}
	
	public String toString() {
		String ret = "\n\n"+idRicetta+"\n"+nome;
		if(orario!=null && giorno!=null && slot!=-1) {
			String slotString = "";
			switch (slot) {
				case 0:
					slotString = "colazione";
					break;
				case 1:
					slotString = "spuntino";
					break;
				case 2:
					slotString = "pranzo";
					break;
				case 3:
					slotString = "merenda";
					break;
				case 4:
					slotString = "cena";
					break;
			}
			ret += "\ngiorno: "+giorno+"\norario: "+orario+"\nslot:"+slotString;
		}
		return ret;
	}
	
	public float getScore(ArrayList<String> veryGoodMacros,HashMap<String, ArrayList<String>> dictionary) {
		
		float currentScore = 0;
		
		for (HashMap.Entry<String, Float> set :
            punteggi.entrySet()) {
			
			String currentMacro = set.getKey();
			float currentConsumption = set.getValue();
			
			
			//Se il macronutriente corrente è uno di quelli very good, ovvero contenuto in types
			if(veryGoodMacros.indexOf(currentMacro)!=-1) {
				currentScore += fromConsumptionToScore(currentConsumption, Macronutriente.isMoreBetter(currentMacro));
			}
			
       }
		
		return currentScore;
	}
	
	private static int fromConsumptionToScore(float currentConsumption, boolean moreIsBetter) {
		int ret = 0;
		if(moreIsBetter) {
			if(currentConsumption==0)			ret = 0;
			else if(currentConsumption<=1)		ret = 1;
			else if(currentConsumption<=2)		ret = 2;
			else if(currentConsumption<=3)		ret = 3;
			else if(currentConsumption<=4.5)	ret = 4;
			else 						ret = 5;
		}else {
			if(currentConsumption==0)			ret = 5;
			else if(currentConsumption<=1)		ret = 4;
			else if(currentConsumption<=2)		ret = 3;
			else if(currentConsumption<=3)		ret = 2;
			else if(currentConsumption<=4.5)	ret = 1;
			else 						ret = 0;
		}
		return ret;
	}
}
