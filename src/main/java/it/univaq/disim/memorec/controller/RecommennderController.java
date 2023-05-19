package it.univaq.disim.memorec.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import java.util.Map;
import java.util.Map.Entry;

@RestController
public class RecommennderController {
	private List<Model> sfDataset;
	private List<Model> classDataset;
	private DataReader dr = new DataReader();
	private final static String classDatasetPath = "data/ecore_memorec/";
	private final static String sfDatasetPath = "data/ecore_memorec/";
	public RecommennderController() {
		sfDataset = dr.readModels(sfDatasetPath);
		classDataset = dr.readModels(classDatasetPath);
		//Model input = dr.readModel(modelPath);
	}
	private final static String modelPath = "data/ecore_memorec/juri.txt";

	@PostMapping(value = "/structuralFeatures",  consumes = "application/json", produces = {
			"application/json", "application/xml" })
	public @ResponseBody List<RecommendationResult> getStrucutralFeatureRecommendations(
			@RequestBody RecommendationRequest input) {
		Recommender memoRec = new Recommender(new SimilarityCalculator(), 10);
		List<RecommendationResult> result = Lists.newArrayList();
		try {
			Map<String, Float> recMap = memoRec.recommend(sfDataset, input.getModel(), input.getContext());
			for (Entry<String, Float> entry : recMap.entrySet())
				result.add(new RecommendationResult(entry.getKey(), entry.getValue()));
		} catch (ActiveDeclarationNotFoundException e) {
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return result;
	}
	
	@PostMapping(value = "/classes",  consumes = "application/json", produces = {
			"application/json", "application/xml" })
	public @ResponseBody List<RecommendationResult> getClassRecommendations(
			@RequestBody RecommendationRequest input) {
		Recommender memoRec = new Recommender(new SimilarityCalculator(), 10);
		List<RecommendationResult> result = Lists.newArrayList();
		try {
			Map<String, Float> recMap = memoRec.recommend(classDataset, input.getModel(), input.getContext());
			for (Entry<String, Float> entry : recMap.entrySet())
				result.add(new RecommendationResult(entry.getKey(), entry.getValue()));
		} catch (ActiveDeclarationNotFoundException e) {
			
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return result;
	}
	
	
	@GetMapping("/hello")
	public @ResponseBody Model sayHello() {
		
		return dr.readModel(modelPath);
	}
	

}