package monfroglio.elena;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;

import monfroglio.elena.controller.DatabaseManager;
import monfroglio.elena.model.Pasto;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;

public class CreateEatingHistory {
	
	public static void main(String args[]){
		DatabaseManager dbm = new DatabaseManager();
		
		
		try {
			populate(dbm,91270,"BRBTZN85A01B406S");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void populate(DatabaseManager dbm, int idStart, String cf) throws SQLException {

		Utente u = dbm.getUtente(cf);
		
		int[] tests = new int[30];
	
		for (int i=0;i<30;i++) {
			tests[i] = idStart+i;
		}
		
		ArrayList<LocalTime> orari = new ArrayList<>();
		orari.add(LocalTime.of(7, 0));
		orari.add(LocalTime.of(10, 0));
		orari.add(LocalTime.of(13, 0));
		orari.add(LocalTime.of(17, 0));
		orari.add(LocalTime.of(20, 0));
		
		int idMeal = 9451;
		LocalDate start = LocalDate.of(2022, Month.JANUARY, 3);
		LocalDate end = start.plusDays(6);
		int k = 0;
		for	(int idTest:tests) {
			start = start.plusDays(k);
			end = start.plusDays(6);
			Settimana sem = new Settimana(start,end,idTest);
			ArrayList<Integer> meals = dbm.getPastiId(sem);

			int day = 0;
			int slot = 0;
			for (Integer idRicetta:meals) {
				LocalDate mealDate = start.plusDays(day);
				LocalTime mealHour = orari.get(slot);
				//in questo step aumento data solo dopo 5 pasti giornalieri
				
				dbm.insertSingleMealEatingHistory(idMeal, u.getCF(), Integer.parseInt(idRicetta.toString()),mealHour,mealDate,slot,start,idTest);
				if(slot<4)	slot++;
				else {
					slot=0;
					day++;
				}
				idMeal++;
				
			}
			k+=7;
		}
	}
}




/*ArrayList<String> listCF = new ArrayList<>();
listCF.add("FRRLSS00A41F848C");
listCF.add("FNTDVD76A01F012O");
listCF.add("GLLLNE93A41D600J");
listCF.add("CLMLSE63A41A089X");
listCF.add("RSSFNC80A01A001I");
listCF.add("SPSGLI78A41G509X");
listCF.add("BNCLGU90A01G625S");
listCF.add("MRNMTT54A01G076R");
listCF.add("CSTSLV04A41A031D");
listCF.add("BRBTZN85A01B406S");

ArrayList<Utente> users = new ArrayList<>();
Utente u;

for(String cf:listCF) {
	try {
		u = dbm.getUtente(cf);
		users.add(u);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}*/
