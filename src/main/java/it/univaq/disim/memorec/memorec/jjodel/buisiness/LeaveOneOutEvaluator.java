package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.kfold.SplittedInput;

public class LeaveOneOutEvaluator extends Evaluator {

	public LeaveOneOutEvaluator(Recommender recommender,SplitConfiguration conf, int nOfItems) {
		super(recommender, conf, nOfItems);
	}

	@Override
	public Map<Metric, Double>[] computeMetric(List<Model> dataset, List<Metric> metrics) {
	
		Map<Metric, Double>[] globalMetrics = new HashMap[1];
		List<Map<Metric, Double>> foldMetric = new ArrayList();
		for (int i = 0; i < dataset.size(); i++) {
			Model model = dataset.get(i);
			try {
				SplittedInput si = splitInput(SplitConfiguration.C22, model);
				List training = new ArrayList<>(dataset);
				training.remove(model);
				Map<String, Float> recommedations = recommender.recommend(training,
						si.getModelQuery(), si.getActiveContext());
				Map<Metric, Double> matrics = this.computeMetric(recommedations, si.getGroundTruth(), metrics);
				foldMetric.add(matrics);

			} catch (ActiveDeclarationNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		globalMetrics[0] = new HashMap<Metric,Double>();
		for (Metric metric : metrics) {
			Double metricFold = foldMetric.stream().mapToDouble(e -> e.get(metric)).sum() / (dataset.size());
			globalMetrics[0].put(metric, metricFold);
		}
		return globalMetrics;
	}

}
