package cike.sqasystem.crawler;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import cike.sqasystem.util.LoginTool;

import com.alibaba.fastjson.JSONObject;



public class WeiboCrawler {

	static String username = "15920411869";
	static String password = "ws199312231805";
	static String userid = "1775020471";
	
	private static HttpClient client;
	
	public static void main(String[] args) {
		
		for(int i=1;i<10;i++){
			try{

//				Scanner usrIn = new Scanner(new File("usr.txt"));
//				username = usrIn.nextLine();
//				password = usrIn.nextLine();
//				userid = usrIn.nextLine();
				
				localLogin();
				
				File dir = new File("C:/Users/Qixuan/Desktop/data/friends1");
//				File dir = new File("friends");
				File[] files = dir.listFiles();
				
				for(File file: files){
					Scanner input;
					try {
						input = new Scanner(file);
						while(input.hasNext()){
							String uid = input.nextLine();
							getAllWeibos(uid);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						System.err.println("Cannot open the user's network file!");
					}
				}
				
				break;
				
			} catch (Exception e) {
				System.out.println("断线重连");
				continue;
			}
		}
	}
	
	
	public static void localLogin(){
		client = new DefaultHttpClient();
		LoginTool weibo = new LoginTool(client, username, password);
		if(weibo.login()){
			System.out.println("登录成功");
		}else{
			System.out.println("登录失败："+weibo.getErrInfo());
		}
	}
	
//	public static boolean hasDone(String uid){
//		
//		File file = new File("C:/Users/Qixuan/Desktop/data/weibos/"+uid+".txt");
//		return file.exists();
//	}
	
	public static void getAllWeibos(String uid){
		
		File file = new File("C:/Users/Qixuan/Desktop/data/weibos1/"+uid+".txt");
//		File file = new File("weibos/"+uid+".txt");
		
		if(file.exists())
			return;
		
		System.out.println(uid);
		
		StringBuffer sbfr = new StringBuffer();
		
		for(int i=1;i<=10;i++){
			String treq1 = "http://weibo.com/p/100505"+uid+"/weibo?page="+i;
			String treq2 = "http://weibo.com/p/aj/mblog/mbloglist?domain=100505&pre_page="+i+"&page="+i+"&count=15&pagebar=0&max_msign=&filtered_min_id=&pl_name=Pl_Official_LeftProfileFeed__11&id=100505"+uid+"&script_uri=/p/100505"+uid+"/weibo&feed_type=0&from=page_100505&mod=TAB";
			String treq3 = "http://weibo.com/p/aj/mblog/mbloglist?domain=100505&pre_page="+i+"&page="+i+"&count=15&pagebar=1&max_msign=&filtered_min_id=&pl_name=Pl_Official_LeftProfileFeed__11&id=100505"+uid+"&script_uri=/p/100505"+uid+"/weibo&feed_type=0&from=page_100505&mod=TAB";
			
			
			StringBuffer strbf = new StringBuffer();
			
			strbf.append(getHTML2(treq1));
			
			strbf.append(getRowWeibos2(treq2));
			
			String st = getRowWeibos3(treq3);
			
			if(st!=null)
				strbf.append(dealWithNextPages(st));
			
			sbfr.append(strbf);
			
			if(!st.contains("下一页"))
				break;
			
		}

		String result = sbfr.toString();
		if(result.trim().length()==0)
			return;
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(file,"UTF-8");
			writer.write(result);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**处理微博内容简单html页面（script内部部分）
	 * @param file 文件名或者String皆可
	 * @throws ParserException
	 */
	public static String dealWithNextPages(String filestr){
		if(filestr==null)
			return "";
		Parser parser;
		try {
			parser = new Parser(filestr);
			parser.setEncoding("UTF-8");
	        NodeFilter filter = new HasAttributeFilter("class","WB_text");       
	        NodeList list = parser.extractAllNodesThatMatch(filter);       
	        StringBuffer strb = new StringBuffer();
	        for (int i = 0; i < list.size(); i++) {           
	        	Node node = list.elementAt(i);
	        	NodeFilter filter2 = new NodeClassFilter(TextNode.class);
	        	NodeList list2 = node.getChildren().extractAllNodesThatMatch(filter2);
	        	for (int j = 0; j < list2.size(); j++) {
	        		String buffer = list2.elementAt(j).getText().trim();
	        		if(buffer.trim().length()>0)
	        			strb.append(buffer);
//	        			System.out.println(buffer);
	        	}
	        }
//	        System.out.println(strb);
	        return strb.toString();
		} catch (Exception e) {
			return " ";
		}
	}

	public static String getHTML2(String reqstr){
		String str = getHTML(reqstr);
		if(str == null)
			return " ";
		return dealWithFitstPage(str);
	}
	
	/**处理微博内容首页HTML，需要从script提取html信息
	 * @param filename
	 * @throws ParserException 
	 */
	public static String dealWithFitstPage(String filename){
		
		Parser parser;
		try {
			parser = new Parser(filename);
			parser.setEncoding("UTF-8");
			NodeFilter filter = new TagNameFilter("script");
			NodeList list = parser.extractAllNodesThatMatch(filter);
			/**
			 * 这里需要注意！！！直接取最后一条不一定安全
			 */
			Node node = list.elementAt(list.size()-1);
			String fullstr = node.getLastChild().getText();
			/**
			 * 这里需要注意！！！依赖于页面的模式。。。
			 */
			String jsstr = fullstr.substring(8, fullstr.length()-1);
//			System.out.println(jsstr);
			JSONObject json = JSONObject.parseObject(jsstr);
			String htmlstr = json.getString("html");
//			System.out.println(htmlstr);
			return dealWithNextPages(htmlstr);
		} catch (Exception e) {
			return " ";
		}
		
	}
	
	/**本函数用于爬取一页并返回html string
	 * @param client
	 * @param reqstr
	 * @return
	 */
	public static String getHTML(String reqstr){
		
		HttpUriRequest request = new HttpGet(reqstr);
		
		try {
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			
			InputStream stream = entity.getContent();
			BufferedReader bf = new BufferedReader(
                    new InputStreamReader(stream,"UTF-8"));
			String s = null;
			StringBuffer sbf = new StringBuffer();
			while ((s=bf.readLine()) != null){
				sbf.append(s);
			}
			String str = sbf.toString();
			
			return str;
			
			
		} catch (Exception e) {
			return " ";
		}
		
	}
	
	
	public static String getRowWeibos2(String reqstr){
		
		
        HttpUriRequest request = new HttpGet(reqstr);   
 
        HttpResponse response;
		try {
			response = client.execute(request);
	         
	        HttpEntity entity = response.getEntity();
	        
			InputStream stream = entity.getContent();
			BufferedReader bf = new BufferedReader(
                      new InputStreamReader(stream));

			String s = null;
			StringBuffer sbf = new StringBuffer();
			while ((s=bf.readLine()) != null){
				sbf.append(s);
			}
			String str = sbf.toString();
			JSONObject res = JSONObject.parseObject(str);
			
			return dealWithNextPages(res.getString("data"));
				
		} catch (Exception e) {
			return " ";
		}
		
	}
	
	public static String getRowWeibos3(String reqstr){
		
        HttpUriRequest request = new HttpGet(reqstr);   
 
        HttpResponse response;
		try {
			response = client.execute(request);
			
	        HttpEntity entity = response.getEntity();
	        
	        try {
				InputStream stream = entity.getContent();
				BufferedReader bf = new BufferedReader(
	                      new InputStreamReader(stream));

				String s = null;
				StringBuffer sbf = new StringBuffer();
				while ((s=bf.readLine()) != null){
					sbf.append(s);
				}
				String str = sbf.toString();
				JSONObject res = JSONObject.parseObject(str);
				String tstr = res.getString("data");
				
				return tstr;
				
//				PrintWriter write = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
//				write.print(res.getString("data"));
//				write.close();
				
			} catch (Exception e) {
				return " ";
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return " ";
         
	}
}
