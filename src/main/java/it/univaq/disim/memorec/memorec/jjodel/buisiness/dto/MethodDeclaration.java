package it.univaq.disim.memorec.memorec.jjodel.buisiness.dto;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclaration {
	private String name;

	private List<String> methodInvocations = new ArrayList<>();
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getMethodInvocations() {
		return methodInvocations;
	}

	public void setMethodInvocations(List<String> methodInvocation) {
		this.methodInvocations = methodInvocation;
	}
}
