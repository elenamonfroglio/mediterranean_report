package monfroglio.elena.model;

public class Emotion {
	private String nome;
	private String type;
	private int polarity;
	
	public Emotion(String nome) {
		this.nome = nome;
		this.type = EmotionType.getType(nome);
		this.polarity = EmotionName.getIntesity(nome);
	}
	
	public boolean equals(Emotion e) {
		return (this.nome.equals(e.nome) && this.type.equals(e.type) && this.polarity==e.polarity);
	}
	
	public String getNome() {
		return nome;
	}
	
	public int getPolarity() {
		return polarity;
	}
	
	public String getType() {
		return type;
	}
}
