package monfroglio.elena.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import monfroglio.elena.Main.Point;

public class Settimana {
	private LocalDate giornoInizio;//TBC
	private LocalDate giornoFine;//TBC
	private ArrayList<Pasto> pasti;
	private int idTest;
	private ArrayList<Macronutriente> macronutrienti;
	private int indiceMed;
	
	
	public Settimana(LocalDate giornoInizio, LocalDate giornoFine, ArrayList<Pasto> pasti, int idTest, int indiceMed) {
		this.giornoInizio = giornoInizio;
		this.giornoFine = giornoFine;
		this.pasti = pasti;
		this.idTest = idTest;
		this.indiceMed = indiceMed;
	}
	
	public Settimana(LocalDate giornoInizio, LocalDate giornoFine, int idTest) {
		this.giornoInizio = giornoInizio;
		this.giornoFine = giornoFine;
		this.idTest = idTest;
	}
	
	public Settimana(int idTest) {
		this.idTest = idTest;
	}
	
	public String toString() {
		return "Settimana dal "+giornoInizio.toString()+" al "+giornoFine.toString()+
				": \n\nIndice di mediterraneità: "+indiceMed+
				"\n";
	}
	
	public void print() {
		System.out.println(this);
		//printPasti();
		printMacronutrienti();
	}
	
	public ArrayList<Pasto> getPasti() {
		return pasti;
	}
	
	public void setPasti(ArrayList<Pasto> pasti) {
		this.pasti = pasti;
	}
	
	public ArrayList<Macronutriente> getMacronutrienti() {
		return macronutrienti;
	}
	
	public void setMacronutrienti(ArrayList<Macronutriente> macronutrienti) {
		this.macronutrienti = macronutrienti;
	}

	public int getIndiceMed() {
		return indiceMed;
	}
	
	public int getIdTest() {
		return idTest;
	}
	
	public void setIndiceMed(int indiceMed) {
		this.indiceMed = indiceMed;
	}
	
	public void printPasti() {
		for(Pasto p:pasti) {
			System.out.print(p + ", ");
		}
	}
	
	public void printMacronutrienti() {
		for(Macronutriente m:macronutrienti) {
			System.out.print(m + ";\n");
		}
	}
	
	public Pasto getPastoFromId(int idPasto) {
		Pasto ret = null;
		for(Pasto p:pasti) {
			if(p.getIdRicetta()==idPasto)
				ret = p;
		}
		return ret;
	}
	
	public ArrayList<Macronutriente> getAllMoreIsBetter() {
		ArrayList<Macronutriente> ret = new ArrayList<Macronutriente>(7);
		for(Macronutriente m: macronutrienti){
			if(m.getMoreIsBetter()) {
				ret.add(m);
			}
		}
		return ret;
	}
	
	public ArrayList<Macronutriente> getAllLessIsBetter() {
		ArrayList<Macronutriente> ret = new ArrayList<Macronutriente>(7);
		for(Macronutriente m: macronutrienti){
			if(!m.getMoreIsBetter()) {
				ret.add(m);
			}
		}
		return ret;
	}
	
	public int countBadEnvironment() {
		int ret = 0;
		
		for (Macronutriente m: macronutrienti) {
			if (!m.getGoodForEnvironment()) {
				ret += m.getPunteggio();
			}
		}
		
		return ret;
	}
	
	public Pasto getPastoWithGoodMacrosPareto(ArrayList<Macronutriente> veryGoodMacros) {
		Pasto bestPasto = null;
		
		ArrayList<Point> listOfPoints = new ArrayList<>();
		int index = 0;
		for(Pasto p:pasti) {
			Point point = new Point("p"+index,p.getSubScore(veryGoodMacros));
			index++;
			listOfPoints.add(point);
		}
		
		ArrayList<Point> optimalList = (ArrayList<Point>) listOfPoints.clone();
		for(Point pointA: listOfPoints) {
			for(Point pointB: listOfPoints) {
				if(!pointA.equal(pointB)) {
					if(pointA.isBetter(pointB)) {
						optimalList.remove(pointB);
					}else if(pointB.isBetter(pointA)){
						optimalList.remove(pointA);
					}
				}
			}
		}
		int indexOptimalList = 0;
		if(optimalList.size()!=1) {
			Random r = new Random();
			int low = 0;
			int high = optimalList.size()-1;
			indexOptimalList = r.nextInt(high-low) + low;
		}
		String substring = optimalList.get(indexOptimalList).name.substring(1);
		int finalIndex = Integer.parseInt(substring);
		
		bestPasto = pasti.get(finalIndex);
		return bestPasto;
	}
	
	public Pasto getPastoWithWorstMacrosPareto(ArrayList<Macronutriente> veryBadMacros) {
		Pasto bestPasto = null;
		
		ArrayList<Point> listOfPoints = new ArrayList<>();
		int index = 0;
		for(Pasto p:pasti) {
			Point point = new Point("p"+index,p.getSubScore(veryBadMacros));
			index++;
			listOfPoints.add(point);
		}
		
		ArrayList<Point> optimalList = (ArrayList<Point>) listOfPoints.clone();
		for(Point pointA: listOfPoints) {
			for(Point pointB: listOfPoints) {
				if(!pointA.equal(pointB)) {
					if(pointA.isBetter(pointB)) {
						optimalList.remove(pointA);
					}else if(pointB.isBetter(pointA)){
						optimalList.remove(pointB);						
					}
				}
			}
		}
		
		int indexOptimalList = 0;
		if(optimalList.size()!=1) {
			Random r = new Random();
			int low = 0;
			int high = optimalList.size()-1;
			indexOptimalList = r.nextInt(high-low) + low;
		}
			
		String substring = optimalList.get(indexOptimalList).name.substring(1);
		int finalIndex = Integer.parseInt(substring);
		
		bestPasto = pasti.get(finalIndex);
		return bestPasto;
	}

	public class Point{
		String name;
		ArrayList<Float> coordinates;
		public Point() {
			
		}
		
		public Point(String name,ArrayList<Float> coordinates) {
			this.name = name;
			this.coordinates = coordinates;
		}
		
		public boolean equal(Point bis) {
			for(int i=0;i<coordinates.size();i++) {
				if(!this.coordinates.get(i).equals(bis.coordinates.get(i))) return false;
			}
			return true;
		}
		
		public boolean isBetter(Point a) {
			
			for(Float f1:this.coordinates) {
				for(Float f2:a.coordinates) {
					if(f1<f2) return false;
				}
			}
			return true;
		}
		
		public String toString() {
			String ret = name+":(";
			for(Float f:coordinates) {
				ret += f+"  ";
			}
			ret += ")";
			return ret;
		}
	}

}

