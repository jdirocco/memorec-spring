package it.univaq.disim.memorec.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.ActiveDeclarationNotFoundException;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.DataReader;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.Recommender;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.SimilarityCalculator;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.RecommendationRequest;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.RecommendationResult;

@RestController
public class RecommenderController {
	private List<Model> sfDataset;
	private List<Model> classDataset;
	private DataReader dr = new DataReader();
	private final static String classDatasetPath = "data/pkg_cls/";
	private final static String sfDatasetPath = "data/cls_sf/";
	private final static String sf_modelPath = "data/cls_sf_test.txt";
	private final static String cls_modelPath = "data/pkg_cls/183_005_056_org.talend.cwm.ext400-257203706.txt";

	public RecommenderController() {
		sfDataset = dr.readModels(sfDatasetPath);
		classDataset = dr.readModels(classDatasetPath);
		// Model input = dr.readModel(modelPath);
	}

	@GetMapping("esempio")
	public @ResponseBody Model getRequest() {
		Model packageDataset = dr.readModel(cls_modelPath);
		return packageDataset;
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(value = "/structuralFeatures", consumes = "application/json", produces = { "application/json",
			"application/xml" })
	public @ResponseBody List<RecommendationResult> getStrucutralFeatureRecommendations(
			@RequestBody RecommendationRequest input) {
		Recommender memoRec = new Recommender(new SimilarityCalculator(), 10);
		List<RecommendationResult> result = Lists.newArrayList();
		try {
			Map<String, Float> recMap = memoRec.recommend(sfDataset, input.getModel(), input.getContext());
			for (Entry<String, Float> entry : recMap.entrySet())
				result.add(new RecommendationResult(entry.getKey(), entry.getValue()));
		} catch (ActiveDeclarationNotFoundException e) {

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return result;
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping(value = "/classes", consumes = "application/json", produces = { "application/json",
			"application/xml" })
	public @ResponseBody List<RecommendationResult> getClassRecommendations(@RequestBody RecommendationRequest input) {
		Recommender memoRec = new Recommender(new SimilarityCalculator(), 10);
		List<RecommendationResult> result = Lists.newArrayList();

		try {
			Map<String, Float> recMap = memoRec.recommend(classDataset, input.getModel(),
					input.getContext());
			for (Entry<String, Float> entry : recMap.entrySet())
				result.add(new RecommendationResult(entry.getKey(), entry.getValue()));
		} catch (ActiveDeclarationNotFoundException e) {

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return result;
	}

}
