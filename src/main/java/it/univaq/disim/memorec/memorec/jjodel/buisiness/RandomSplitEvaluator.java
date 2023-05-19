package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.kfold.SplittedInput;

public class RandomSplitEvaluator extends Evaluator {

	private int precentageSplit;

	public RandomSplitEvaluator(Recommender recommender, SplitConfiguration conf, int percentageSplit, int nOfItems)
			throws Exception {
		super(recommender, conf, nOfItems);
		if (percentageSplit < 1 && percentageSplit > 99)
			throw new Exception("Percentage split value is wrong. It should be in [1,99]");
		this.precentageSplit = percentageSplit;

	}

	public int getPrecentageSplit() {
		return precentageSplit;
	}

	public void setPrecentageSplit(int precentageSplit) {
		this.precentageSplit = precentageSplit;
	}

	@Override
	public Map<Metric, Double>[] computeMetric(List<Model> dataset, List<Metric> metrics) {
		Map<Metric, Double>[] globalMetrics = new HashMap[1];
		List<Map<Metric, Double>> foldMetric = new ArrayList();
		int numOfTesting = (dataset.size() * (100 - precentageSplit)) / 100;
		List<Model> testing = new ArrayList<>();
		List<Model> training = new ArrayList<>(dataset);
		Random rand = new Random();
		for (int i = 0; i < numOfTesting; i++) {
			testing.add(dataset.get(rand.nextInt(numOfTesting)));
		}
		training.removeAll(testing);
		
		
		
		for (int i = 0; i < testing.size(); i++) {
			Model model = testing.get(i);
			try {
				SplittedInput si = splitInput(SplitConfiguration.C22, model);
				Map<String, Float> recommedations = recommender.recommend(training, si.getModelQuery(),
						si.getActiveContext());
				Map<Metric, Double> matrics = this.computeMetric(recommedations, si.getGroundTruth(), metrics);
				foldMetric.add(matrics);

			} catch (ActiveDeclarationNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		globalMetrics[0] = new HashMap<Metric, Double>();
		for (Metric metric : metrics) {
			Double metricFold = foldMetric.stream().mapToDouble(e -> e.get(metric)).sum() / (training.size());
			globalMetrics[0].put(metric, metricFold);
		}
		return globalMetrics;

	}

}
