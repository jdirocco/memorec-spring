package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;

public class Main {
	// TEST MODELS//
	private final static String modelPath = "data/ecore_memorec/juri.txt";
	private final static String modelDatasetPath = "data/ecore_memorec/";

	public static void main(String[] args) throws Exception {
		DataReader dr = new DataReader();
		Model input = dr.readModel(modelPath);
		List<Model> daset = dr.readModels(modelDatasetPath);

		Recommender memoRec = new Recommender(new SimilarityCalculator(), 10);
		Map<String, Float> recommendations = memoRec.recommend(daset, input, "Book");
		recommendations.entrySet().forEach(z -> System.out.println(z.getKey() + " " + z.getValue()));
		
//		List<Metric> metrics = Arrays.asList(Metric.FMEASURE, Metric.PRECISION, Metric.RECALL, Metric.SUCCESS_RATE);
//		
//		Evaluator tenFolEvaluator = new KFoldEvaluator(memoRec, SplitConfiguration.C22, 10, 20);
//		
//		System.out.println();
//		System.out.println("Ten Fold");
//		Map<Metric, Double>[] metricsTenFold = tenFolEvaluator.computeMetric(new ArrayList(daset), metrics);
//		for (int i = 0; i < metricsTenFold.length; i++) {
//			System.out.println("Fold: " + i);
//			for (Entry<Metric, Double> elemento : metricsTenFold[i].entrySet()) {
//				System.out.println("\t" + elemento.getKey().toString() + " = " + elemento.getValue());
//			}
//		}
//		
//		System.out.println();
//		System.out.println("Leave one out");
//		Evaluator leaveOneOutEvaluator = new LeaveOneOutEvaluator(memoRec, SplitConfiguration.C22, 20);
//		Map<Metric, Double>[] metricLeaveOneOut = leaveOneOutEvaluator.computeMetric(new ArrayList(daset), metrics);
//		for (int i = 0; i < metricLeaveOneOut.length; i++) {
//			System.out.println("Fold: " + i);
//			for (Entry<Metric, Double> elemento : metricLeaveOneOut[i].entrySet()) {
//				System.out.println("\t" + elemento.getKey().toString() + " = " + elemento.getValue());
//			}
//		}
//
//		System.out.println();
//		System.out.println("RandomSplit");
//		Evaluator randomSplitEvaluator = new RandomSplitEvaluator(memoRec, SplitConfiguration.C22, 20, 20);
//		Map<Metric, Double>[] metricsRandomSplit = randomSplitEvaluator.computeMetric(new ArrayList(daset), metrics);
//		for (int i = 0; i < metricsRandomSplit.length; i++) {
//			System.out.println("Fold: " + i);
//			for (Entry<Metric, Double> elemento : metricsRandomSplit[i].entrySet()) {
//				System.out.println("\t" + elemento.getKey().toString() + " = " + elemento.getValue());
//			}
//		}
	}
}
