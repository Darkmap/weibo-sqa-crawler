package cike.sqasystem.bean;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UserWithSimi implements Comparable<UserWithSimi>{


	private String uid;
	private double similarity;
	
	
	
	public UserWithSimi(String uid, double similarity) {
		super();
		this.uid = uid;
		this.similarity = similarity;
	}

	public UserWithSimi() {
		
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}


	public int compareTo(UserWithSimi o) {

		if(o.getSimilarity()<this.similarity)
			return -1;
		else if(o.getSimilarity()>this.similarity)
			return 1;
		else
			return 0;
	}
	
	
	
}
