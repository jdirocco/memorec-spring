package it.univaq.disim.memorec.memorec.jjodel.buisiness.kfold;

import java.util.List;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;

public class SplittedInput {

	private Model modelQuery;
	private String activeContext;
	private List<String> groundTruth; 

	public Model getModelQuery() {
		return modelQuery;
	}

	public void setModelQuery(Model modelQuery) {
		this.modelQuery = modelQuery;
	}

	public List<String> getGroundTruth() {
		return groundTruth;
	}

	public void setGroundTruth(List<String> groundTruth) {
		this.groundTruth = groundTruth;
	}

	public String getActiveContext() {
		return activeContext;
	}

	public void setActiveContext(String activeContext) {
		this.activeContext = activeContext;
	}
	
	
}
