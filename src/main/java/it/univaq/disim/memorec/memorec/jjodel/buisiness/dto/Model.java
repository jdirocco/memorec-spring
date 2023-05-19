package it.univaq.disim.memorec.memorec.jjodel.buisiness.dto;

import java.util.ArrayList;
import java.util.List;

public class Model {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<MethodDeclaration> getMethodDeclarations() {
		return methodDeclarations;
	}

	public void setMethodDeclarations(List<MethodDeclaration> methodDeclarations) {
		this.methodDeclarations = methodDeclarations;
	}

	private List<MethodDeclaration> methodDeclarations = new ArrayList<>();;

}
