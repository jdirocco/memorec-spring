package it.univaq.disim.memorec.memorec.jjodel.buisiness;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.MethodDeclaration;
import it.univaq.disim.memorec.memorec.jjodel.buisiness.dto.Model;


public class DataReader {

	public Model readModel(String filePath) {
		Path path = Paths.get(filePath);
		Model result = new Model();
		result.setName(filePath);
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			String mdOld = "";
			MethodDeclaration methodDeclaration = new MethodDeclaration();
			result.setName(path.getFileName().toString());
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("#");
				if (parts.length == 2) {
					String md = parts[0].replace("'", "").trim();
					String methodInvocation = parts[1].replace("'", "").trim();
					if (!mdOld.equals(md)) {
						methodDeclaration = new MethodDeclaration();
						methodDeclaration.setName(md);
						mdOld = md;
						result.getMethodDeclarations().add(methodDeclaration);
						methodDeclaration.getMethodInvocations().add(methodInvocation);
					} else
						methodDeclaration.getMethodInvocations().add(methodInvocation);
				}
			}
		} catch (Exception e) {
			System.err.println("from file: " + filePath + e.getMessage());
		}
		return result;
	}

	public List<Model> readModels(String modelsFolderPath) {
		List<Model> res = new ArrayList<>();
		try {
			Files.list(Paths.get(modelsFolderPath)).filter(Files::isRegularFile)
					.filter(z -> !z.getFileName().endsWith("List.txt")).forEach(z -> {
						res.add(readModel(z.toString()));
					});
			return res;
		} catch (IOException e) {
			System.out.println("error");
			return res;
		}

	}

}
