package fr.alma.pagerank.job2;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class NewRankReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

	private static final float damping = 0.85F;
	
	@Override
	public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> out, Reporter reporter) throws IOException {
        boolean isExistingWikiPage = false;
        String[] split;
        float sumShareOtherPageRanks = 0;
        String links = "";
        String pageWithRank;
 
        // For each otherPage:
        // - check control characters
        // - calculate pageRank share <rank> / count(<links>)
        // - add the share to sumShareOtherPageRanks
        while(values.hasNext()){
            pageWithRank = values.next().toString();
 
            if(pageWithRank.equals("!")) {
                isExistingWikiPage = true;
                continue;
            }
 
            if(pageWithRank.startsWith("|")){
                links = "\t"+pageWithRank.substring(1);
                continue;
            }
 
            split = pageWithRank.split("\\t");
 
            float pageRank = Float.valueOf(split[0]);
            int countOutLinks = Integer.valueOf(split[1]);
 
            sumShareOtherPageRanks += (pageRank/countOutLinks);
        }
 
        if(!isExistingWikiPage) return;
        float newRank = damping * sumShareOtherPageRanks + (1-damping);
 
        out.collect(key, new Text(newRank + links));
		
	}

}
