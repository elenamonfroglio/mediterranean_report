package monfroglio.elena;

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
	public static void main(String[] args) {
		DatabaseManager dbmgr = new DatabaseManager();
		test1(dbmgr);
		//test3();
	}
	
	public static void test3() {
		ParetoComparator<Point> comparator = new ParetoComparator<Point>();
		comparator.add(new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				return Integer.valueOf(o1.x).compareTo(Integer.valueOf(o2.x));
			}
		});
		comparator.add(new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				return Integer.valueOf(o1.y).compareTo(Integer.valueOf(o2.y));
			}
		});
		comparator.add(new Comparator<Point>() {
			public int compare(Point o1, Point o2) {
				return Integer.valueOf(o1.z).compareTo(Integer.valueOf(o2.z));
			}
		});

		Point p00 = new Point(0, 0, 3);
		Point p01 = new Point(0, 1, 0);
		Point p02 = new Point(0, 2, 1);
		Point p10 = new Point(1, 0, 1);
		Point p11 = new Point(1, 1, 2);
		Point p12 = new Point(1, 2, 2);
		Point p20 = new Point(2, 0, 3);
		Point p21 = new Point(2, 1, 3);
		Point p22 = new Point(2, 2, 3);

		{
			Point ref = p00;
			assertTrue(comparator.compare(ref, p00) == 0);
			assertTrue(comparator.compare(ref, p01) == 0);
			assertTrue(comparator.compare(ref, p02) == 0);
			assertTrue(comparator.compare(ref, p10) == 0);
			assertTrue(comparator.compare(ref, p11) == 0);
			assertTrue(comparator.compare(ref, p12) == 0);
			assertTrue(comparator.compare(ref, p20) < 0);
			assertTrue(comparator.compare(ref, p21) < 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p01;
			assertTrue(comparator.compare(ref, p00) == 0);
			assertTrue(comparator.compare(ref, p01) == 0);
			assertTrue(comparator.compare(ref, p02) < 0);
			assertTrue(comparator.compare(ref, p10) == 0);
			assertTrue(comparator.compare(ref, p11) < 0);
			assertTrue(comparator.compare(ref, p12) < 0);
			assertTrue(comparator.compare(ref, p20) == 0);
			assertTrue(comparator.compare(ref, p21) < 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p02;
			assertTrue(comparator.compare(ref, p00) == 0);
			assertTrue(comparator.compare(ref, p01) > 0);
			assertTrue(comparator.compare(ref, p02) == 0);
			assertTrue(comparator.compare(ref, p10) == 0);
			assertTrue(comparator.compare(ref, p11) == 0);
			assertTrue(comparator.compare(ref, p12) < 0);
			assertTrue(comparator.compare(ref, p20) == 0);
			assertTrue(comparator.compare(ref, p21) == 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p10;
			assertTrue(comparator.compare(ref, p00) == 0);
			assertTrue(comparator.compare(ref, p01) == 0);
			assertTrue(comparator.compare(ref, p02) == 0);
			assertTrue(comparator.compare(ref, p10) == 0);
			assertTrue(comparator.compare(ref, p11) < 0);
			assertTrue(comparator.compare(ref, p12) < 0);
			assertTrue(comparator.compare(ref, p20) < 0);
			assertTrue(comparator.compare(ref, p21) < 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p11;
			assertTrue(comparator.compare(ref, p00) == 0);
			assertTrue(comparator.compare(ref, p01) > 0);
			assertTrue(comparator.compare(ref, p02) == 0);
			assertTrue(comparator.compare(ref, p10) > 0);
			assertTrue(comparator.compare(ref, p11) == 0);
			assertTrue(comparator.compare(ref, p12) < 0);
			assertTrue(comparator.compare(ref, p20) == 0);
			assertTrue(comparator.compare(ref, p21) < 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p12;
			assertTrue(comparator.compare(ref, p00) == 0);
			assertTrue(comparator.compare(ref, p01) > 0);
			assertTrue(comparator.compare(ref, p02) > 0);
			assertTrue(comparator.compare(ref, p10) > 0);
			assertTrue(comparator.compare(ref, p11) > 0);
			assertTrue(comparator.compare(ref, p12) == 0);
			assertTrue(comparator.compare(ref, p20) == 0);
			assertTrue(comparator.compare(ref, p21) == 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p20;
			assertTrue(comparator.compare(ref, p00) > 0);
			assertTrue(comparator.compare(ref, p01) == 0);
			assertTrue(comparator.compare(ref, p02) == 0);
			assertTrue(comparator.compare(ref, p10) > 0);
			assertTrue(comparator.compare(ref, p11) == 0);
			assertTrue(comparator.compare(ref, p12) == 0);
			assertTrue(comparator.compare(ref, p20) == 0);
			assertTrue(comparator.compare(ref, p21) < 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p21;
			assertTrue(comparator.compare(ref, p00) > 0);
			assertTrue(comparator.compare(ref, p01) > 0);
			assertTrue(comparator.compare(ref, p02) == 0);
			assertTrue(comparator.compare(ref, p10) > 0);
			assertTrue(comparator.compare(ref, p11) > 0);
			assertTrue(comparator.compare(ref, p12) == 0);
			assertTrue(comparator.compare(ref, p20) > 0);
			assertTrue(comparator.compare(ref, p21) == 0);
			assertTrue(comparator.compare(ref, p22) < 0);
			System.out.println("");
		}

		{
			Point ref = p22;
			assertTrue(comparator.compare(ref, p00) > 0);
			assertTrue(comparator.compare(ref, p01) > 0);
			assertTrue(comparator.compare(ref, p02) > 0);
			assertTrue(comparator.compare(ref, p10) > 0);
			assertTrue(comparator.compare(ref, p11) > 0);
			assertTrue(comparator.compare(ref, p12) > 0);
			assertTrue(comparator.compare(ref, p20) > 0);
			assertTrue(comparator.compare(ref, p21) > 0);
			assertTrue(comparator.compare(ref, p22) == 0);
			System.out.println("");
		}

	}
	
	public static void test2(DatabaseManager dbmgr) {
		SenticnetManager sm = new SenticnetManager("italiano");
		sm.readCSV();
		System.out.println("fine");
	}
	
	public static void test1(DatabaseManager dbmgr) {
		try {
			//recupero user model dal codice fiscale
			Utente u = dbmgr.getUtente("FRRLSS00A41F848C");
			int idTest = 91234;
			int idTestPreviousWeek = 91001;
			
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
			
			//Create a textPlanner
			TextPlanner tp = new TextPlanner("italiano",u,sem,prevSem);
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


