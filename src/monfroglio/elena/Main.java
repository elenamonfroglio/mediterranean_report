package monfroglio.elena;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import monfroglio.elena.controller.DatabaseManager;
import monfroglio.elena.controller.ReportRealiser;
import monfroglio.elena.controller.SentencePlanner;
import monfroglio.elena.controller.SenticnetManager;
import monfroglio.elena.controller.TextPlanner;
import monfroglio.elena.model.ParetoComparator;
import monfroglio.elena.model.Pasto;
import monfroglio.elena.model.Settimana;
import monfroglio.elena.model.Utente;


public class Main {
	public static void main(String[] args) throws SQLException {
		DatabaseManager dbmgr = new DatabaseManager();
		ArrayList<Utente> utenti = new ArrayList<>();
		Utente u1 = dbmgr.getUtente("FRRLSS00A41F848C"); // // STRESS = 3 ITALIANO
		Utente u2 = dbmgr.getUtente("FNTDVD76A01F012O"); // // STRESS = 2 ITALIANO
		Utente u3 = dbmgr.getUtente("GLLLNE93A41D600J"); // // STRESS = 0 ITALIANO
		Utente u4 = dbmgr.getUtente("CLMLSE63A41A089X"); // // DIETISTA ITALIANO
		Utente u5 = dbmgr.getUtente("RSSFNC80A01A001I"); // // DIETISTA INGLESE
		Utente u6 = dbmgr.getUtente("SPSGLI78A41G509X"); // // STRESS = 0 INGLESE
		Utente u7 = dbmgr.getUtente("BNCLGU90A01G625S"); // // STRESS = 2 INGLESE
		Utente u8 = dbmgr.getUtente("MRNMTT54A01G076R"); // // STRESS = 3 INGLESE
		Utente u9 = dbmgr.getUtente("CSTSLV04A41A031D"); // // STRESS = 1 INGLESE
		Utente u10 = dbmgr.getUtente("BRBTZN85A01B406S"); // // STRESS = 1 ITALIANO
		
		//utenti.add(u1);
		//utenti.add(u4);
		//utenti.add(u2);
		//utenti.add(u3);
		//utenti.add(u5);
		//utenti.add(u6);
		utenti.add(u7);
		//utenti.add(u8);
		//utenti.add(u9);
		//utenti.add(u10);
		
		for(Utente u:utenti) {
			System.out.println("\n\n"+ u.getNome() + " "+ u.getCognome());
			test1(dbmgr,u);
		}/*
		try {
			test3(dbmgr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public static void test3(DatabaseManager dbmgr) throws SQLException, IOException {
		dbmgr.getItalianReceipts();
	}
	
	public static void test2(DatabaseManager dbmgr) {
		SenticnetManager sm = new SenticnetManager("italiano");
		sm.readCSV();
		System.out.println("fine");
	}
	
	public static void test1(DatabaseManager dbmgr, Utente u) {
		try {
			//recupero user model dal codice fiscale
			
			
			//91000
			//91299
			int idTest = 91001+ (int)(Math.random() * ((91299 - 91001) + 1));
			int idTestPreviousWeek = idTest-1;
			
			//creo una settimana relativa all'utente u
			LocalDate start = LocalDate.of(2022, Month.JANUARY, 3);
			LocalDate end = LocalDate.of(2022, Month.JANUARY, 9);
			Settimana sem = new Settimana(start,end,idTest);
			Settimana prevSem = new Settimana(idTestPreviousWeek);
			//recupero i pasti della settimana e l'indice di mediterraneità
			ArrayList<Pasto> pasti = dbmgr.getEatingHistory(idTest);
			for(Pasto p:pasti) {
				HashMap<String,Float> ret = dbmgr.getConsumptionFromRicetta(p.getIdRicetta());	
				p.setPunteggi(ret);
			}
			sem.setPasti(pasti);
			sem.setIndiceMed(dbmgr.getIndiceMed(sem));
			prevSem.setIndiceMed(dbmgr.getIndiceMed(prevSem));
			sem.setMacronutrienti(dbmgr.getPunteggiComponenti(sem));
			sem.print();
			//Create a textPlanner
			String lingua = u.getLingua();
			TextPlanner tp = new TextPlanner(lingua,u,sem,prevSem);
			tp.contentDetermination();
			tp.textStructuring();
			
			SentencePlanner sp = new SentencePlanner(tp.getFileName(),tp.getOrder(), sem);
			sp.sentenceAggregation();
			sp.lexicalisation();
			
			
			ReportRealiser rr = new ReportRealiser(sp.getFileName(), sp.phrases);
			//rr.createLongSentence_old();
			rr.createLongSentence_new();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void assertTrue(boolean bool) {
		if(bool)	System.out.println("true");
		else		System.out.println("false");
	}

	public static class Point{
		int x;
		int y;
		int z;
		
		Point(int a, int b, int c){
			this.x = a;
			this.y = b;
			this.z = c;
		}
	}
	
}


