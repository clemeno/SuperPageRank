package fr.alma.pagerank.job1;


import java.io.IOException;
import java.nio.charset.CharacterCodingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class ParseLinkMapper extends MapReduceBase implements Mapper<LongWritable,Text, Text,Text> {

	private static final Pattern wikiLinksPattern = Pattern.compile("href=\"http:(.*?)\""); //^(https?://)" );
	
	@Override
	public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		// TODO Auto-generated method stub
		 String[] titleAndText = parseTitleAndText(value);
		 
	        String pageString = titleAndText[0];
	        Text page = new Text(pageString.replace(' ', '_'));
	 
	        Matcher matcher = wikiLinksPattern.matcher(titleAndText[1]);
	        while (matcher.find()) {
	        	
	            String otherPage = matcher.group();	            
	            otherPage = getWikiPageFromLink(otherPage,titleAndText[0].substring(4).replace("_",""));
	            
	            if(otherPage == null || otherPage.isEmpty())
	                continue;
	           
	           
	            output.collect(page, new Text(otherPage));
	        }
	        
	}

	
	private String getWikiPageFromLink(String otherPage,String domaine) {
		//System.out.println("domaine : "+domaine);
		//System.out.println("other "+otherPage);
		if(!otherPage.contains(domaine)){
			
			return otherPage.substring(6,otherPage.length()-1);
		}
		return "";
	}


	private String[] parseTitleAndText(Text value) throws CharacterCodingException {
		
        String[] titleAndText = new String[2];
        
        int start = value.find("<fileTitle>");
        int end = value.find("</fileTitle>", start);
        start += 11; //add <title> length.
        
        titleAndText[0] = Text.decode(value.getBytes(), start, end-start);

        start = value.find("<body");
        start = value.find(">", start);
        end = value.find("</body>", start);
        start += 1;
        
        if(start == -1 || end == -1) {
            return new String[]{"",""};
        }
        
        titleAndText[1] = Text.decode(value.getBytes(), start, end-start);
        System.out.println("titleAndText "+titleAndText[0]);
        return titleAndText;
	}
	

}
