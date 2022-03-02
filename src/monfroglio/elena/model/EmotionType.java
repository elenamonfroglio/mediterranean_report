package monfroglio.elena.model;

public abstract class EmotionType {
	public static final String INTROSPECTION = "introspection";
	public static final String TEMPER = "temper";
	public static final String ATTITUDE = "attitude";
	public static final String SENSITIVITY = "sensitivity";
	
	public static String getType(String name) {
		String ret = "";
		if(name.equals(EmotionName.ECSTASY) || 
				name.equals(EmotionName.JOY) ||
				name.equals(EmotionName.CONTENTMENT) ||
				name.equals(EmotionName.MELANCHOLY) ||
				name.equals(EmotionName.SADNESS) || 
				name.equals(EmotionName.GRIEF))
			ret = EmotionType.INTROSPECTION;
		else if(name.equals(EmotionName.BLISS) || 
				name.equals(EmotionName.CALMNESS) ||
				name.equals(EmotionName.SERENITY) ||
				name.equals(EmotionName.ANNOYANCE) ||
				name.equals(EmotionName.ANGER) || 
				name.equals(EmotionName.RAGE))
			ret = EmotionType.TEMPER;
		else if(name.equals(EmotionName.DELIGHT) || 
				name.equals(EmotionName.PLEASENTNESS) ||
				name.equals(EmotionName.ACCEPTANCE) ||
				name.equals(EmotionName.DISLIKE) ||
				name.equals(EmotionName.DISGUST) || 
				name.equals(EmotionName.LOATHING))
			ret = EmotionType.ATTITUDE;
		else if(name.equals(EmotionName.ENTHUSIASM) || 
				name.equals(EmotionName.EAGERNESS) ||
				name.equals(EmotionName.RESPONSIVENESS) ||
				name.equals(EmotionName.ANXIETY) ||
				name.equals(EmotionName.FEAR) || 
				name.equals(EmotionName.TERROR))
			ret = EmotionType.SENSITIVITY;
		return ret;
	}
}
