package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.MethodDeclaration;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.kfold.SplittedInput;

public abstract class Evaluator {

	protected Recommender recommender;
	protected int nOfItem;
	protected SplitConfiguration configuration;

	public Evaluator(Recommender recommender, SplitConfiguration conf, int nOfItem) {
		this.recommender = recommender;
		this.nOfItem = nOfItem;
		configuration = conf;
	}

	public Map<Metric, Double> computeMetric(Map<String, Float> recommendation, List<String> groundTruth,
			List<Metric> metrics) {
		int daRaccomandare = groundTruth.size();
		Set<String> recSet = new HashSet<>();
		Iterator<String> iterator = recommendation.keySet().iterator();
		for (int i = 0; iterator.hasNext() && i < nOfItem; i++) {
			recSet.add(iterator.next());
		}
		Set<String> gtSet = new HashSet<>(groundTruth);
		recSet.retainAll(gtSet);
		int intesectSize = recSet.size();
		Map<Metric, Double> result = new HashMap<>();
		for (Metric metric : metrics) {
			if (metric == Metric.PRECISION) {
				result.put(Metric.PRECISION, intesectSize > 0 ? (intesectSize*1.0)/nOfItem: 0.0);
			}
			if (metric == Metric.RECALL)
				result.put(Metric.RECALL, intesectSize > 0 ? (intesectSize*1.0)/daRaccomandare: 0.0);
			
			if (metric == Metric.FMEASURE) {
				double recall = intesectSize > 0 ? (intesectSize*1.0)/nOfItem: 0.0;
				double precision = intesectSize > 0 ? (intesectSize*1.0)/daRaccomandare: 0.0;
				result.put(Metric.FMEASURE, (2*precision*recall)>0?(2*precision*recall)/(precision+recall):0.0);
			}

			if (metric == Metric.SUCCESS_RATE) {
				result.put(Metric.SUCCESS_RATE, intesectSize > 0 ? 1.0 : 0.0);
			}
		}
		return result;
	}
	
	public SplittedInput splitInput(SplitConfiguration conf, Model model) {
		SplittedInput splittedInput = new SplittedInput();
		// SELECT MD with more than 1 invocations
		List<MethodDeclaration> candidatesMD = model.getMethodDeclarations().stream()
				.filter(e -> e.getMethodInvocations().size() > 0).collect(Collectors.toList());
		if(model.getMethodDeclarations().size()==0) {
			System.err.println(model.getName());
		}
		if(model.getMethodDeclarations().size() == 1) {
			System.out.println(model.getName() + " ");
		}
		Random rand = new Random();
		MethodDeclaration activeContext = candidatesMD.size()>0? candidatesMD.get(rand.nextInt(candidatesMD.size())): model.getMethodDeclarations().get(0);
		if (conf == SplitConfiguration.C22) {
			Model out = new Model();
			out.setName(model.getName());
			// Select the first SF
			List<String> methodInvocations = new ArrayList<>();
			methodInvocations.add(activeContext.getMethodInvocations().get(0));
			MethodDeclaration newMethodDec = new MethodDeclaration();
			newMethodDec.setName(activeContext.getName());
			newMethodDec.setMethodInvocations(methodInvocations);
			out.getMethodDeclarations().add(newMethodDec);
			List<MethodDeclaration> otherMD = new ArrayList<>(model.getMethodDeclarations());
			otherMD.remove(activeContext);
			out.getMethodDeclarations().addAll(otherMD);
			splittedInput.setActiveContext(activeContext.getName());
			splittedInput.setModelQuery(out);
			splittedInput.setGroundTruth(new ArrayList<>(
					activeContext.getMethodInvocations().subList(1, activeContext.getMethodInvocations().size())));
			return splittedInput;
		}
		// TODO COMPLETE OTHER CASES
		return null;
	}

	public abstract Map<Metric, Double>[] computeMetric(List<Model> dataset, List<Metric> metrics);
}
