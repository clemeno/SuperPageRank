package fr.alma.pagerank.pageContent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

public class GetHtmlContent {
	
	private static Set<String> Urls=new HashSet<String>();

	public static void main(String args[]) throws IOException{
		Urls.add("http://www.univ-nantes.fr/");
		Urls.add("http://www.nantes.fr");
		Urls.add("http://www.choeurunivnantes.fr/");
		Urls.add("http://www.lunam.fr/");
		Urls.add("http://www.youtube.com/univnantes");
		
		System.setProperty("http.proxyHost", "cache.etu.univ-nantes.fr");
		System.setProperty("http.proxyPort","3128");
		
		for (String url : Urls) {
			URL urlToReach = new URL(url);
			URLConnection con = urlToReach.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			System.out.println("url: "+url);
			int lu=0;
			FileWriter fstream = new FileWriter(url.substring(7).replaceAll("/", "_")+".txt");
			body=body.trim();
			BufferedWriter out = new BufferedWriter(fstream);
			 out.write("<myDomain>");
			 out.write("<fileTitle>"+url.substring(7).replace("/","_")+"</fileTitle>");
			 out.flush();
			 
			while(body.length()>lu){

		     out = new BufferedWriter(fstream);
		    
		    if(body.substring(lu).length()>100){
		    	System.out.println(body.substring(lu).length());
		    	out.write(body.substring(lu,lu+100));
		    	out.flush();
		    }else{
		    	System.out.println(body.substring(lu).length());
		    	out.write(body.substring(lu));
		    	out.write("</myDomain>");
		    	out.flush();
		    }
		    	lu=lu+100;
		   
			}
			
		    //out.write("</body>");
			//System.out.println(body);
		}
		
	}
}
