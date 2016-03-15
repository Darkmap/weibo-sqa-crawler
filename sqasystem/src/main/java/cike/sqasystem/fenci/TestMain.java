package cike.sqasystem.fenci;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import ICTCLAS.I3S.AC.ICTCLAS50;
import cike.sqasystem.bean.UserWithSimi;


class TestMain
{   //主函数
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException
	{
		
//		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
//		testICTCLAS_ParagraphProcess
		
//		File f = new File("2383985150.txt");
//		System.out.println(f.length());
//		Scanner input = new Scanner(f);
//		System.out.println(input.next());

		
//			strbf.append(a);
//		}
		
//		System.out.println(testICTCLAS_ParagraphProcess(strbf.toString()));
		
		
//		Iterator<String> iter = map.keySet().iterator(); 
//		while (iter.hasNext()) { 
//		    String key = iter.next();
//		    System.out.print(key+"\t\t");
//		    Integer val = map.get(key); 
//		    System.out.println(val);
//		}
		
//		//产出分词结果
//		File dir = new File("C:\\Users\\Qixuan\\Desktop\\data\\weibos1");
//		File[] files = dir.listFiles();
//		for(File f: files){
//			System.out.println(f);
//			file(f.getAbsolutePath(),"C:\\Users\\Qixuan\\Desktop\\data\\results\\"+f.getName());
//		}
		
		
		ArrayList<String> l1 = new ArrayList<String>();
		ArrayList<String> l2 = new ArrayList<String>();
		ArrayList<String> l3 = new ArrayList<String>();
		ArrayList<String> l4 = new ArrayList<String>();
		
		Scanner in1 = new Scanner(new File("C:\\Users\\Qixuan\\Desktop\\data\\friends1\\15920411869.txt"));
		Scanner in2 = new Scanner(new File("C:\\Users\\Qixuan\\Desktop\\data\\friends1\\joyhshy@hotmail.com.txt"));
		Scanner in3 = new Scanner(new File("C:\\Users\\Qixuan\\Desktop\\data\\friends1\\xiaotian3545@hotmail.com.txt"));
		Scanner in4 = new Scanner(new File("C:\\Users\\Qixuan\\Desktop\\data\\friends1\\395709102@qq.com.txt"));
		
		while(in1.hasNext()){
			l1.add(in1.nextLine());
		}
		while(in2.hasNext()){
			l2.add(in2.nextLine());
		}
		while(in3.hasNext()){
			l3.add(in3.nextLine());
		}
		while(in4.hasNext()){
			l4.add(in4.nextLine());
		}

		
		
		
		
		
		//TODO 
		String question = "汇编0000：:0080h什么意思？";
		
		String q_result_str = testICTCLAS_ParagraphProcess(question);
		HashMap<String, Double> question_vector = convertToMap(q_result_str);
		
		
		Iterator<String> iter0 = question_vector.keySet().iterator();
		
		while (iter0.hasNext()) { 
		    String key = iter0.next();
		    System.out.print(key+"\t\t");
		    Double val = question_vector.get(key); 
		    System.out.println(val);
		}
		
		File dir = new File("C:\\Users\\Qixuan\\Desktop\\data\\results\\");
		File[] files = dir.listFiles();
		ArrayList<UserWithSimi> palist = new ArrayList<UserWithSimi>();
		
		for(File f: files){
			String id = f.getName().substring(0,10);
			
			//in
//			if(!l1.contains(id))
//				continue;
			
			//med
			if(l2.contains(id))
				continue;
			if(!l1.contains(id))
				continue;
			
//			//out
//			if(l1.contains(id))
//				continue;
			
			Scanner in = new Scanner(f,"UTF-8");
			String line;
			if(in.hasNext()){
				line = in.nextLine();	
//				System.out.println(line);
				if(line!=null){
					HashMap<String, Double> p_a_vector = convertToMap(line);
					palist.add(new UserWithSimi(f.getName(), computeSimilarity(question_vector, p_a_vector)));
				}
				else
					palist.add(new UserWithSimi(f.getName(), 0.0));
			}
			else
				palist.add(new UserWithSimi(f.getName(), 0.0));
		}
		
		Collections.sort(palist);
		
		PrintWriter out = new PrintWriter(new File("C:\\Users\\Qixuan\\Desktop\\data\\recomendation\\"+"汇编"+"_med.txt"));
		
		for(UserWithSimi u: palist){
			String us = (u.getUid()).substring(0,10);
			out.println(us+"\t"+u.getSimilarity());
		}
		out.close();
		
	}

	
	
	
	public static double computeSimilarity(HashMap<String, Double> qv, HashMap<String, Double> av){
		
		Iterator<String> iter = qv.keySet().iterator();
		Iterator<String> iter2 = av.keySet().iterator();
		
		//Extend Jaccard
		double x2 = 0,xy=0,y2=0;
		while (iter.hasNext()) {
		    String key = iter.next();
		    Double val = qv.get(key);
		    x2 += val*val;
		    if(av.containsKey(key))
		    	xy+=val*av.get(key);
		}
		while (iter2.hasNext()) {
		    String key = iter2.next();
		    Double val = av.get(key);
		    y2 += val*val;
		}
		
		return xy/(x2+y2-xy);
	}
	

	public static HashMap<String, Double> convertToMap(String q_result_str){
		
		HashMap<String, Double> map = new HashMap<String, Double>();
		int count = 0;
		String[] q_list = q_result_str.split(" ");
		for(String s:q_list){
			if(s.contains("/x")||s.contains("/n")){
				count++;
				String[] words = s.split("/");
				String word = words[0];
				if(s.contains("/x"))
					word=word.toLowerCase();
				if(map.containsKey(word))
					map.put(word, map.get(word)+1.0);
				else
					map.put(word, 1.0);
			}
		}
		
		Iterator<String> iter = map.keySet().iterator();
		
		while (iter.hasNext()) { 
		    String key = iter.next();
		    map.put(key, map.get(key)*1.0/count);
		}
		
		return map;
	}
	
	public static void file(String Inputfilename, String Outputfilename) throws UnsupportedEncodingException{
		try
		{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			//分词所需库的路径
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("UTF-8")) == false)
			{
				System.out.println("Init Fail!");
				return;
			}

			//输入文件名
//			String Inputfilename = "C:\\Users\\Qixuan\\Desktop\\weibo_data\\weibo\\2380349312.txt";
			byte[] Inputfilenameb = Inputfilename.getBytes();//将文件名string类型转为byte类型

			//分词处理后输出文件名
//			String Outputfilename = "C:\\Users\\Qixuan\\Desktop\\test_result.txt";
			byte[] Outputfilenameb = Outputfilename.getBytes();//将文件名string类型转为byte类型

			//文件分词(第一个参数为输入文件的名,第二个参数为文件编码类型,第三个参数为是否标记词性集1 yes,0 no,第四个参数为输出文件名)
			testICTCLAS50.ICTCLAS_FileProcess(Inputfilenameb, 1, 1, Outputfilenameb);

//			Scanner in = new Scanner(new File(Outputfilename));
//			while(in.hasNext()){
//				System.out.println(in.next());
//			}


		}
		catch (Exception ex)
		{
		}
	}
	

	/**
	 * @param sInput
	 * @return
	 */
	public static String testICTCLAS_ParagraphProcess(String sInput)
	{
		try
		{
			ICTCLAS50 testICTCLAS50 = new ICTCLAS50();
			String argu = ".";
			//初始化
			if (testICTCLAS50.ICTCLAS_Init(argu.getBytes("UTF-8")) == false){
				System.out.println("Init Fail!");
				return "";
			}


			//设置词性标注集(0 计算所二级标注集，1 计算所一级标注集，2 北大二级标注集，3 北大一级标注集)
			testICTCLAS50.ICTCLAS_SetPOSmap(2);

			//分词
			byte nativeBytes[] = testICTCLAS50.ICTCLAS_ParagraphProcess(sInput.getBytes("UTF-8"), 1, 1);//分词处理
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "UTF-8");
			
			//释放分词组件资源
			testICTCLAS50.ICTCLAS_Exit();
			return nativeStr;
			
		}
		catch (Exception ex){
			return " ";
		}
		

	}



}


	
	

	