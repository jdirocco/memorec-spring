package it.univaq.disim.memorec.memorec.jjodel.buisiness.kfold;

import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;


public class Fold {
	
	
	public Fold(int nOfFold) {
		this.nOfFold = nOfFold;
		training = new ArrayList[nOfFold];
		testing = new ArrayList[nOfFold];
	}
	
	private List<Model>[]  training;
	private List<Model>[] testing;
	private int nOfFold;

	public int getnOfFold() {
		return nOfFold;
	}

	public void setnOfFold(int nOfFold) {
		this.nOfFold = nOfFold;
	}

	public  List<Model>[] getTesting() {
		return testing;
	}

	public void setTesting(List<Model>[] testing) {
		this.testing = testing;
	}

	public  List<Model>[] getTraining() {
		return training;
	}

	public void setTraining( List<Model>[] training) {
		this.training = training;
	}
	
//	public boolean check() {
//		return nOfFold == training.le() && nOfFold == testing.size();
//	} 

}
