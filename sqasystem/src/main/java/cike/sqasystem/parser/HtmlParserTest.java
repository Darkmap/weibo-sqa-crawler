package cike.sqasystem.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.alibaba.fastjson.JSONObject;


public class HtmlParserTest {
	
	
	
	/**处理一页的 “我的粉丝” 的信息（一页19条）
	 * @param filename html文件路径
	 * @return
	 * @throws ParserException
	 */
	public static List<String> dealFans(String filename) throws ParserException{
		
		List<String> friendlist = new ArrayList<String>();
		
		Parser parser = new Parser(filename);
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
//                System.out.println(tsl[1]);
            }
        }
        
        return friendlist;
	} 
	
	/**处理一页的 “我关注的人” 的信息（一页30条）
	 * @param filename html文件路径
	 * @return
	 * @throws ParserException
	 */
	public static List<String> dealFriends(String filename) throws ParserException{
		
		List<String> friendlist = new ArrayList<String>();
		
		Parser parser = new Parser(filename);
		parser.setEncoding("UTF-8");
		NodeFilter filter = new TagNameFilter("script");
		NodeList list = parser.extractAllNodesThatMatch(filter);
		/**
		 * 这里需要注意！！！直接取第六条不一定安全
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
//                System.out.println(tsl[1]);
            }
        }
        
        return friendlist;
	} 
	
	
	/**处理微博内容首页HTML，需要从script提取html信息
	 * @param filename
	 * @throws ParserException 
	 */
	public static String dealWithFitstPage(String filename){

		try {
			Parser parser = new Parser(filename);
			parser.setEncoding("UTF-8");
			NodeFilter filter = new TagNameFilter("script");
			NodeList list = parser.extractAllNodesThatMatch(filter);
			String fullstr = null;
			for(int i=0;i<list.size();i++){
				Node node = list.elementAt(i);
				fullstr = node.getLastChild().getText();
				if(fullstr.contains("pl.content.homeFeed.index"))
					break;
			}
			/**
			 * 这里需要注意！！！依赖于页面的模式。。。
			 */
			String jsstr = fullstr.substring(8, fullstr.length()-1);
			JSONObject json = JSONObject.parseObject(jsstr);
			String htmlstr = json.getString("html");
			
			return dealWithNextPages(htmlstr);
			
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return " ";
		}
	}
	
	/**处理微博内容简单html页面（script内部部分）
	 * @param file 文件名或者String皆可
	 */
	public static String dealWithNextPages(String file){

		if(file==null)
			return " ";
		try {
			Parser parser = new Parser(file);
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
	        	}
	        }
	        return strb.toString();
		} catch (ParserException e) {
			e.printStackTrace();
			return " ";
		}
	}
	
}