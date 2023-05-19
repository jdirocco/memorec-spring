package it.univaq.disim.memorec.memorec.jjodel.buisiness.dto;

import java.util.Map;

public class Recommendation {
	
	private Map<String, Double> recommendation;

	public Map<String, Double> getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(Map<String, Double> recommendation) {
		this.recommendation = recommendation;
	}

}
