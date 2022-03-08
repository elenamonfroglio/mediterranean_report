package monfroglio.elena.model;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.LinkedList;


@SuppressWarnings("serial")
public class ParetoComparator<Individual> extends
		LinkedList<Comparator<Individual>> implements Comparator<Individual> {

	
	public int compare(Individual a, Individual b) {
		int reference = 0;
		for (Comparator<Individual> comparator : this) {
			if (reference == 0) {
				reference = (int) Math.signum(comparator.compare(a, b));
			} else {
				int comparison = (int) Math.signum(comparator.compare(a, b));
				if (comparison * reference < 0) {
					// one better, another worst : cannot decide
					return 0;
				}
			}
		}
		return reference;
	}
	
	public int getBest(ArrayList<Individual> list) {
		int indexMax = 0;
		
		for(Individual a:list) {
			for(Individual b:list) {
				
			}
		}
		
		
		return indexMax;
	}

}
