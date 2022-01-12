package monfroglio.elena.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Pasto {
	private String alimento;
	private LocalTime orario;
	private LocalDate giorno;
	private int slot;
	//https://jdbc.postgresql.org/documentation/head/8-date-time.html
	
	public Pasto(String alimento, LocalTime orario, LocalDate giorno, int slot) {
		this.alimento = alimento;
		this.orario = orario;
		this.giorno = giorno;
		this.slot = slot;
	}
	
	public Pasto(String alimento) {
		this.alimento = alimento;
		this.orario = null;
		this.giorno = null;
		this.slot = -1;
	}
	
	public void print() {
		System.out.println(this);
	}
	
	public String toString() {
		String ret = alimento;
		if(orario!=null && giorno!=null && slot!=-1) {
			String slotString = "";
			switch (slot) {
				case 1:
					slotString = "colazione";
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
}
