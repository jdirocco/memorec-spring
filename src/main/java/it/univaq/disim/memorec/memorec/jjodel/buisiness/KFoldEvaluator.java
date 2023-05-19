package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.kfold.Fold;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.kfold.SplittedInput;

public class KFoldEvaluator extends Evaluator {

	public KFoldEvaluator(Recommender recommender, SplitConfiguration conf, int numOfFolds, int nOfItem) {
		super(recommender, conf, nOfItem);
		this.numOfFolds = numOfFolds;
	}
	private int numOfFolds;

	

	@Override
	public Map<Metric, Double>[] computeMetric(List<Model> dataset, List<Metric> metrics) {
		Fold folds = splitKFold(dataset);
		Map<Metric, Double>[] foldsMetric = new HashMap[numOfFolds];
		for (int i = 0; i < numOfFolds; i++) {
			foldsMetric[i] = new HashMap<Metric,Double>();
			List<Map<Metric, Double>> foldMetric = new ArrayList();
			for (Model model : folds.getTesting()[i]) {
				try {
					SplittedInput si = splitInput(SplitConfiguration.C22, model);
					Map<String, Float> recommedations = recommender.recommend(folds.getTraining()[i],
							si.getModelQuery(), si.getActiveContext());
					Map<Metric, Double> matrics = this.computeMetric(recommedations, si.getGroundTruth(), metrics);
					foldMetric.add(matrics);

				} catch (ActiveDeclarationNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (Metric metric : metrics) {
				Double metricFold = foldMetric.stream().mapToDouble(e -> e.get(metric)).sum() / folds.getTesting()[i].size();
				foldsMetric[i].put(metric, metricFold);
			}
		}
		return foldsMetric;
	}

	public int getNumOfFolds() {
		return numOfFolds;
	}

	public void setNumOfFolds(int numOfFolds) {
		this.numOfFolds = numOfFolds;
	}

	public Fold splitKFold(List<Model> dataset) {

		Fold res = new Fold(numOfFolds);
		int step = dataset.size() / numOfFolds;
		for (int i = 0; i < numOfFolds - 1; i++) {
			res.getTesting()[i] = new ArrayList<>();
			res.getTesting()[i].addAll(dataset.subList(i * step, (i * step) + step));
			res.getTraining()[i] = new ArrayList<>();
			res.getTraining()[i].addAll(dataset.subList(0, i * step));
			res.getTraining()[i].addAll(dataset.subList((i * step) + step, dataset.size()));
		}
		res.getTesting()[numOfFolds - 1] = new ArrayList<>();
		res.getTesting()[numOfFolds - 1].addAll(dataset.subList((numOfFolds - 1) * step, dataset.size()));
		res.getTraining()[numOfFolds - 1] = new ArrayList<>();
		res.getTraining()[numOfFolds - 1].addAll(dataset.subList(0, (numOfFolds - 1) * step));

		return res;

	}

}
