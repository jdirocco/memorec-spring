package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Maps;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.MethodDeclaration;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;

public class Recommender {

	private int numOfNeighbor;

	private SimilarityCalculator similarityCalculator;

	public Recommender(SimilarityCalculator similarityCalculator, int numOfNeighbor) {
		this.numOfNeighbor = numOfNeighbor;
		this.similarityCalculator = similarityCalculator;
	}

	public int getNumOfNeighbor() {
		return numOfNeighbor;
	}

	public void setNumOfNeighbor(int numOfNeighbor) {
		this.numOfNeighbor = numOfNeighbor;
	}

	public Map<String, Float> recommend(List<Model> trainingProjects, Model testingProject, String activeDeclaration, int numberOfRecommendations) throws ActiveDeclarationNotFoundException{
		return recommend(trainingProjects, testingProject, activeDeclaration).entrySet().stream().limit(numberOfRecommendations).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
	}
	public TreeMap<String, Float> recommend(List<Model> trainingProjects, Model testingProject, String activeDeclaration)
			throws ActiveDeclarationNotFoundException {
		Map<String, Float> recommendations = new HashMap<>();
		List<Model> listOfPRs = new ArrayList<>();
		List<String> listOfMIs = new ArrayList<>();
		Map<Model, Float> simScores = similarityCalculator.getSimilarModels(testingProject, trainingProjects);
		List<Model> simProjects = getTopNSimilarProjects(simScores, numOfNeighbor);
		byte matrix[][][] = buildUserItemContextMatrix(testingProject, listOfPRs, listOfMIs, simProjects,
				activeDeclaration);
		Map<String, Float> mdSimScores = new HashMap<String, Float>();
		// Compute the jaccard similarity between the testingMethod and every other
		// method
		// and store the results in mdSimScores
		byte[] testingMethodVector = matrix[matrix.length - 1][matrix[0].length - 1];
		for (int i = 0; i < matrix.length - 1; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				byte[] otherMethodVector = matrix[i][j];
				float sim = similarityCalculator.computeJaccardSimilarity(testingMethodVector, otherMethodVector);
				String key = Integer.toString(i) + "#" + Integer.toString(j);
				mdSimScores.put(key, sim);
			}
		}
		// Sort the results
		StringComparator bvc = new StringComparator(mdSimScores);
		TreeMap<String, Float> simSortedMap = new TreeMap<>(bvc);
		simSortedMap.putAll(mdSimScores);

		// Compute the top-3 most similar methods
		Map<String, Float> top3Sim = Maps.newHashMap();
		int count = 0;
		for (String key : simSortedMap.keySet()) {
			top3Sim.put(key, mdSimScores.get(key));
			count++;
			if (count > 3)
				break;
		}
		float[] ratings = new float[matrix[0][0].length - 1];
		// For every '?' cell (-1.0), compute a rating
		for (int k = 0; k < matrix[0][0].length; k++) {
			if (matrix[matrix.length - 1][matrix[0].length - 1][k] == -1) {
				double totalSim = 0;
				// Iterate over the top-3 most similar methods
				for (String key : top3Sim.keySet()) {
					String line = key.trim();

					String parts[] = line.split("#");
					int slice = Integer.parseInt(parts[0]);
					int row = Integer.parseInt(parts[1]);

					// Compute the average rating of the method declaration
					double avgMDRating = 0;
					for (int m = 0; m < matrix[0][0].length; m++)
						avgMDRating += matrix[slice][row][m];
					avgMDRating /= matrix[0][0].length;

					Model project = listOfPRs.get(slice);
					double projectSim = simScores.get(project);
					double val = projectSim * matrix[slice][row][k];
					double methodSim = top3Sim.get(key);
					totalSim += methodSim;
					ratings[k] += (val - avgMDRating) * methodSim;
				}
				if (totalSim != 0)
					ratings[k] /= totalSim;
				double activeMDrating = 0.8;
				ratings[k] += activeMDrating;
				String methodInvocation = listOfMIs.get(k);
				recommendations.put(methodInvocation, ratings[k]);
			}
		}
		HashMap<String, Float> noJavaRecs = Maps.newHashMap();
		recommendations.keySet().stream().filter(z -> !z.startsWith("java"))
				.forEach(z -> noJavaRecs.put(z, recommendations.get(z)));
	
		TreeMap<String, Float> recSortedMap = new TreeMap<>(noJavaRecs);	
		return recSortedMap;
	}
	
	
	public byte[][][] buildUserItemContextMatrix(Model testingModel, List<Model> listOfTrainingModels,
			List<String> listOfMethodInvocations, List<Model> simModels, String activeDeclaration)
			throws ActiveDeclarationNotFoundException {
		if (!getActiveDeclarationMIs(testingModel, activeDeclaration))
			throw new ActiveDeclarationNotFoundException();
		
		Map<Model, Map<String, Set<String>>> allProjects = new HashMap<>();
		List<Model> listOfPRs = new ArrayList<>();
		Set<String> allMDs = new HashSet<>();
		Set<String> allMIs = new HashSet<>();
		for (Model artifact : simModels) {
			Map<String, Set<String>> tmpMIs = getMDsMIs(artifact);
			allMDs.addAll(tmpMIs.keySet());
			for (Set<String> mis : tmpMIs.values())
				allMIs.addAll(mis);
			allProjects.put(artifact, tmpMIs);
			listOfPRs.add(artifact);
		}
		// The slice for the testing project is located at the end of the matrix
		listOfPRs.add(testingModel);
		Map<String, Set<String>> testingMIs = getMDsMIs(testingModel);
		allMDs.addAll(testingMIs.keySet());
		for (Set<String> s : testingMIs.values())
			allMIs.addAll(s);
		allProjects.put(testingModel, testingMIs);

		// Convert to an ordered list of all method declarations to make sure
		// that the testing method declaration locates at the end of the list
		List<String> listOfMDs = new ArrayList<>(allMDs);
		if (listOfMDs.contains(activeDeclaration))
			listOfMDs.remove(listOfMDs.indexOf(activeDeclaration));
		listOfMDs.add(activeDeclaration);

		// Convert to an ordered list of all method invocations to make sure
		// that all testing method invocations locate at the end of the list
		List<String> listOfMIs = new ArrayList<>(allMIs);
		for (String testingMI : testingMIs.get(activeDeclaration))
			if (listOfMIs.contains(testingMI))
				listOfMIs.remove(listOfMIs.indexOf(testingMI));
		for (String testingMI : testingMIs.get(activeDeclaration))
			listOfMIs.add(testingMI);
		int numOfSlices = listOfPRs.size();
		int numOfRows = listOfMDs.size();
		int numOfCols = listOfMIs.size();
		byte[][][] matrix = new byte[numOfSlices][numOfRows][numOfCols];
		// Populate all cells in the user-item-context ratings matrix using 1s and 0s
		for (int i = 0; i < numOfSlices - 1; i++) {
			Model currentPro = listOfPRs.get(i);
			Map<String, Set<String>> myMDs = allProjects.get(currentPro);
			for (int j = 0; j < numOfRows; j++) {
				String currentMD = listOfMDs.get(j);
				if (myMDs.containsKey(currentMD)) {
					Set<String> myMIs = myMDs.get(currentMD);
					for (int k = 0; k < numOfCols; k++) {
						String currentMI = listOfMIs.get(k);
						if (myMIs.contains(currentMI))
							matrix[i][j][k] = (byte) 1;
					}
				}
			}
		}

		// This is the testing project, ie. the last slice of the 3-D matrix
		Map<String, Set<String>> myMDs = allProjects.get(testingModel);
		for (int j = 0; j < numOfRows - 1; j++) {
			String currentMD = listOfMDs.get(j);
			if (myMDs.containsKey(currentMD)) {
				Set<String> myMIs = myMDs.get(currentMD);
				for (int k = 0; k < numOfCols; k++) {
					String currentMI = listOfMIs.get(k);
					if (myMIs.contains(currentMI))
						matrix[numOfSlices - 1][j][k] = (byte) 1;
				}
			}
		}

		String currentMD = listOfMDs.get(numOfRows - 1);
		Set<String> myMIs = myMDs.get(currentMD);
		for (int k = 0; k < numOfCols; k++)
			if (myMIs.contains(listOfMIs.get(k)))
				matrix[numOfSlices - 1][numOfRows - 1][k] = (byte) 1;
			else
				matrix[numOfSlices - 1][numOfRows - 1][k] = (byte) -1;

		for (Model l : listOfPRs)
			listOfTrainingModels.add(l);

		for (String l : listOfMIs)
			listOfMethodInvocations.add(l);
		return matrix;
	}

	private boolean getActiveDeclarationMIs(Model testingProject, String activeDeclaration)
			throws ActiveDeclarationNotFoundException {
		for (MethodDeclaration md : testingProject.getMethodDeclarations())
			if (md.getName().equals(activeDeclaration))
				return true;
		return false;

	}
	
	private Map<String, Set<String>> getMDsMIs(Model artifact) {
		Map<String, Set<String>> result = new HashMap<>();
		for (MethodDeclaration iterable_element : artifact.getMethodDeclarations()) {
			result.put(iterable_element.getName(), new HashSet<String>(iterable_element.getMethodInvocations()));
		}
		return result;
	}
	
	private List<Model> getTopNSimilarProjects(Map<Model, Float> simScores, int numOfNeighbours2) {
		int count = 0;
		List<Model> results = new ArrayList<>();
		for (Map.Entry<Model, Float> artifactSimValueMapEntry : simScores.entrySet()) {
			if (!(count < numOfNeighbours2))
				return results;

			results.add(artifactSimValueMapEntry.getKey());
			count++;
		}
		return results;
	}
}
