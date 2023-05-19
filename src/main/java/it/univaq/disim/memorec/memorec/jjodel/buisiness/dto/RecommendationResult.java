package it.univaq.disim.memorec.memorec.jjodel.buisiness.dto;

public class RecommendationResult {
	public RecommendationResult(String recommendedItem, Float score) {
		super();
		this.recommendedItem = recommendedItem;
		this.score = score;
	}

	private String recommendedItem;
	private Float score;

	public String getRecommendedItem() {
		return recommendedItem;
	}

	public void setRecommendedItem(String recommendedItem) {
		this.recommendedItem = recommendedItem;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}
}
