package cike.sqasystem.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.alibaba.fastjson.JSONObject;

import cike.sqasystem.util.LoginTool;


public class FriendsCrawler {


	private static String username;
	private static String password;
	private static String userid;
	
	private static HttpClient client;
	
	public static void main(String[] args) {

//		Scanner input;
//		try {
//			input = new Scanner(new File(args[0]));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			System.err.println("配置文件打开错误");
//			return;
//		}
		
//		username = "xiaotian3545@hotmail.com";
//		password = "sj15902023515";
//		userid = "1876715005";
		
//		username = "395709102@qq.com";
//		password = "zgy2259345";
//		userid = "2166758942";
		
		username = "joyhshy@hotmail.com";
		password = "joy104088";
		userid = "1890040815";
		
//		username = "735830492@qq.com";
//		password = "panW519";
//		userid = "2295904832";
		
//		username = "15920411869";
//		password = "ws199312231805";
//		userid = "1775020471";
		
//		
//		String username = input.next();
//		String password = input.next();
//		String userid = input.next();
		

		localLogin();

		int count = 0;
		
//		String file = "C:/Users/Qixuan/Desktop/data/"+username+".txt";
		Scanner in;
		try {
			in = new Scanner(new File("follow.txt"));
			while(in.hasNext()){
				String a = in.nextLine();
//				System.out.println(a);
				count += getAllFriends(a);
				System.out.println("count:"+count);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
	
	/**检查字符串中有没有“下一页”
	 * @param str
	 * @return
	 */
	public static boolean hasNextpage(String str){
		
		if(str.contains("下一页"))
			return true;
		else
			return false;
	}
	
	
	public static int getAllFriends(String userid){
		
//		List followerlist = getFollowers(userid);
//		System.out.println(followerlist.size()+"follower");
//		List fanlist = getFans(userid);
//		System.out.println(fanlist.size()+"fans");
		
		List followerlist = getNima(userid);
		
		try {
			PrintWriter write = new PrintWriter(new File("all/"+userid+"_follow.txt"));
			for(String uid: (ArrayList<String>) followerlist)
				write.println(uid);
			write.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
//		try {
//			PrintWriter write = new PrintWriter(new File("C:/Users/Qixuan/Desktop/data/friends/"+username+"_fan.txt"));
//			for(String uid: (ArrayList<String>) fanlist)
//				write.println(uid);
//			write.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		return followerlist.size();
		
	}
	
	public static List getFollowers(String uid){
		List followerlist = new ArrayList<String>();
		
		for(int i=1;i<11;i++){
			String req = "http://weibo.com/"+uid+"/myfollow?t=1&f=1&page="+i;
			String html = getHTML(client, req);
//			System.out.println(html);
			if(!hasNextpage(html))
				break;
			else
				System.out.println("第"+i+"页");
			followerlist.addAll(dealFollowers(html));
			
				
		}
		
		return followerlist;
	}
	
	public static List getFans(String uid){
		List fanlist = new ArrayList<String>();
		
		for(int i=1;i<11;i++){
			String req = "http://weibo.com/"+uid+"/myfans?t=1&f=1&page="+i;
			String html = getHTML(client, req);
//			System.out.println(html);
			if(!hasNextpage(html))
				break;
			else
				System.out.println("第"+i+"页");
			fanlist.addAll(dealFans(html));
		}
		
		return fanlist;
	}
	
	public static List getNima(String uid){
		List fanlist = new ArrayList<String>();
		
		for(int i=1;i<11;i++){
			String req = "http://weibo.com/p/100505"+uid+"/follow?page="+i;
			String html = getHTML(client, req);
//			System.out.println(html);
			if(!hasNextpage(html))
				break;
			else
				System.out.println("第"+i+"页");
			fanlist.addAll(dealNima(html));
		}
		
		return fanlist;
	}
	
	public static List dealNima(String htmlStr){
		
		List friendlist = new ArrayList<String>();
		
    	String reg = "uid=[0-9]+";	//uid=XXXXXXXXX的正则表达式
        //使用正则表达式进行字符串匹配
        Pattern pattern = Pattern.compile(reg);
        int start = 0;
        int end = 0;
        String str = htmlStr;
        
//        System.out.println(str);

        Matcher matcher = pattern.matcher(str);
        int i=0;
        while(matcher.find()){
//        	System.out.println("groupCount:"+matcher.groupCount());
        	String[] ts = matcher.group().split("=");
//        	System.out.println(ts[1]);
        	if(!friendlist.contains(ts[1]))
        		friendlist.add(ts[1]);
        }
        
        return friendlist;
	} 
	
	public static List dealFans(String htmlStr){
		
		List friendlist = new ArrayList<String>();
		
		Parser parser;
		try {
			parser = new Parser(htmlStr);
			parser.setEncoding("UTF-8");
			NodeFilter filter = new TagNameFilter("script");
			NodeList list = parser.extractAllNodesThatMatch(filter);
			/**
			 * 这里需要注意！！！直接取第7条不一定安全
			 */
			Node node = list.elementAt(6);
			String fullstr = node.getLastChild().getText();
			/**
			 * 这里需要注意！！！依赖于页面的模式。。。
			 */
			String jsstr = fullstr.substring(41, fullstr.length()-1);
			JSONObject json = JSONObject.parseObject(jsstr);
			String htmlstr = json.getString("html");
			Parser parser2 = new Parser(htmlstr);
			parser2.setEncoding("UTF-8");
	        NodeFilter filter2 = new HasAttributeFilter("class","clearfix S_line5");
	        NodeList list2 = parser2.extractAllNodesThatMatch(filter2);
	        for(int i=0;i<list2.size();i++){
	        	String str = list2.elementAt(i).getText();
	        	String reg = "uid=[0-9]+";	//uid=XXXXXXXXX的正则表达式
	            //使用正则表达式进行字符串匹配
	            Pattern pattern = Pattern.compile(reg);
	            Matcher matcher = pattern.matcher(str);
	            if (matcher.find()) {
	                String[] tsl = matcher.group().split("=");
	                friendlist.add(tsl[1]);
	                System.out.println(tsl[1]);
	            }
	        }
		} catch (ParserException e) {
			return friendlist;
		}
        
        return friendlist;
	} 
	
	/**处理关注页面
	 * @param htmlStr
	 * @return
	 */
	public static List dealFollowers(String htmlStr){
		
		List friendlist = new ArrayList<String>();
		
		Parser parser;
		try {
			parser = new Parser(htmlStr);
			parser.setEncoding("UTF-8");
			NodeFilter filter = new TagNameFilter("script");
			NodeList list = parser.extractAllNodesThatMatch(filter);
			/**
			 * 这里需要注意！！！直接取第7条不一定安全
			 */
			Node node = list.elementAt(6);
			String fullstr = node.getLastChild().getText();
			/**
			 * 这里需要注意！！！依赖于页面的模式。。。
			 */
			String jsstr = fullstr.substring(41, fullstr.length()-1);
			System.out.println(jsstr);
			JSONObject json = JSONObject.parseObject(jsstr);
			String htmlstr = json.getString("html");
			
			Parser parser2 = new Parser(htmlstr);
			parser2.setEncoding("UTF-8");
	        NodeFilter filter2 = new HasAttributeFilter("class","S_link2");
	        NodeFilter filter3 = new HasAttributeFilter("node-type","set_group");
	        NodeList list2 = parser2.extractAllNodesThatMatch(filter2);
	        NodeList list3 = list2.extractAllNodesThatMatch(filter3);
	        for(int i=0;i<list3.size();i++){
	        	String str = list3.elementAt(i).getText();
	        	String reg = "uid=[0-9]+";	//uid=XXXXXXXXX的正则表达式
	            //使用正则表达式进行字符串匹配
	            Pattern pattern = Pattern.compile(reg);
	            Matcher matcher = pattern.matcher(str);
	            if (matcher.find()) {
	                String[] tsl = matcher.group().split("=");
	                friendlist.add(tsl[1]);
	                System.out.println(tsl[1]);
	            }
	        }
		} catch (ParserException e) {
			return friendlist;
		}
		
        
        return friendlist;
	} 
	
	/**本函数用于爬取一页并返回html string
	 * @param client
	 * @param reqstr
	 * @return
	 */
	public static String getHTML(HttpClient client, String reqstr){
		
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
			
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
