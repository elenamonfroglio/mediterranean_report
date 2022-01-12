package monfroglio.elena.controller;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

public class ReportRealiser {
	Lexicon lexicon;
	NLGFactory nlgFactory;
	Realiser realiser;
	
	public ReportRealiser() {
		lexicon = Lexicon.getDefaultLexicon();
		nlgFactory = new NLGFactory(lexicon);
		realiser = new Realiser(lexicon);
	}
	
	public void createSentence() {
		SPhraseSpec p = nlgFactory.createClause();
		p.setSubject("Cereal");
	    p.setVerb("chase");
	    p.setObject("the monkey");
	    
	    String output2 = realiser.realiseSentence(p);
	    System.out.println(output2);
	}
	  
}
