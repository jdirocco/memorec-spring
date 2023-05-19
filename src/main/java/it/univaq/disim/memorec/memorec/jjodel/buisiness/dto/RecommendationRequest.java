package it.univaq.disim.memorec.memorec.jjodel.buisiness.dto;

public class RecommendationRequest {

	private Model model;
	private String context;

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
}
