package cike.sqasystem.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FriendsClassification {

	public static void main(String[] args) throws FileNotFoundException {
		
		String user = "joyhshy@hotmail.com";
		
		File f1 = new File("C:\\Users\\Qixuan\\Desktop\\data\\friends1\\"+user+"_fan.txt");
		File f2 = new File("C:\\Users\\Qixuan\\Desktop\\data\\friends1\\"+user+"_follow.txt");
		
		File f3 = new File("C:\\Users\\Qixuan\\Desktop\\data\\friends1\\"+user+".txt");
		
		Scanner in1 = new Scanner(f1);
		Scanner in2 = new Scanner(f2);
		
		ArrayList<String> l1 = new ArrayList<String>();
		ArrayList<String> l2 = new ArrayList<String>();
		
		while(in1.hasNext()){
			l1.add(in1.nextLine());
		}
		while(in2.hasNext()){
			l2.add(in2.nextLine());
		}
		
		PrintWriter write = new PrintWriter(f3);
		for(int i=0;i<l1.size();i++){
			String s1 = l1.get(i);
			if(l2.contains(s1)){
				write.println(s1);
			}
		}
		write.close();
	}
}
