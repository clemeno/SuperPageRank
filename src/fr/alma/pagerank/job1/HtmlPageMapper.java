package fr.alma.pagerank.job1;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

import fr.alma.pagerank.job2.NewRankMapper;
import fr.alma.pagerank.job2.NewRankReducer;


public class HtmlPageMapper {

	private static NumberFormat nf = new DecimalFormat("00");
	
	public void parseUrls( String inputPath, String outputPath ) throws IOException {
		JobConf conf = new JobConf( HtmlPageMapper.class );
		
		conf.set(XmlInputFormat.START_TAG_KEY, "<myDomain>");
        conf.set(XmlInputFormat.END_TAG_KEY, "</myDomain>");
		
        FileInputFormat.setInputPaths(conf, new Path(inputPath));
        conf.setInputFormat(XmlInputFormat.class);
        conf.setMapperClass(ParseLinkMapper.class);
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));
        conf.setOutputFormat(TextOutputFormat.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setReducerClass(LinkPageReducer.class);
        
        JobClient.runJob(conf);
        
		/*FileInputFormat.setInputPaths( conf, new Path( inputPath ) ); 
		conf.setInputFormat(TextInputFormat.class);
		
		conf.setMapperClass(ParseLinkMapper.class);
		
		FileOutputFormat.setOutputPath(conf, new Path(outputPath));
		conf.setOutputFormat(TextOutputFormat.class);
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        conf.setReducerClass(LinkPageReducer.class);
        JobClient.runJob(conf);*/
        
	}
	
	   private void rank(String inputPath, String outputPath) throws IOException {
	        JobConf conf = new JobConf(HtmlPageMapper.class);
	 
	        conf.setOutputKeyClass(Text.class);
	        conf.setOutputValueClass(Text.class);
	 
	        conf.setInputFormat(TextInputFormat.class);
	        conf.setOutputFormat(TextOutputFormat.class);
	 
	        FileInputFormat.setInputPaths(conf, new Path(inputPath));
	        FileOutputFormat.setOutputPath(conf, new Path(outputPath));
	 
	        conf.setMapperClass(NewRankMapper.class);
	        conf.setReducerClass(NewRankReducer.class);
	 
	        JobClient.runJob(conf);
	    }
	
	public static void main( String[] args ) throws IOException {
		HtmlPageMapper pageRanker = new HtmlPageMapper(); 
		pageRanker.parseUrls( "/htmlPages", "/linkFile"); 
		int runs = 0;
        for (; runs < 5; runs++) {
            //Job 2: Calculate new rank
            pageRanker.rank("wiki/ranking/iter"+nf.format(runs), "wiki/ranking/iter"+nf.format(runs + 1));
        }
	}
	
}
