package org.yelp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;

public class FindCategories {
	
	
	public static void main(String[] args){
		String pathToIndex = "indexes";
		String queryPath  = "E:/Search Final Project/categories.csv";
		ArrayList<String> categoryString = new ArrayList<String>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(queryPath))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	categoryString.add(line);
		    }
		    // line is not visible here.
		}
	}
	
public void findQuery(Similarity sAlgo, List<String> queryList,String runID,String outFileName) throws IOException, ParseException {
		
		System.out.println("Finding results for " + outFileName);
		String index = "/home/achal/Desktop/Study/Github/Indexing-using-lucene/index";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		
		Analyzer analyzer = new StandardAnalyzer();
		searcher.setSimilarity(sAlgo);
		QueryParser parser = new QueryParser("TEXT", analyzer);


		StringBuilder allResults = new StringBuilder();
		int queryId = 51;
		for (String queryString : queryList) {
			// String queryString = "police";

			
			Query query = parser.parse(QueryParser.escape(queryString));
			//System.out.println("Searching for: " + query.toString("TEXT"));

			TopDocs results = searcher.search(query, 1000);

			// Print number of hits
			int numTotalHits = results.totalHits;
			//System.out.println(numTotalHits + " total matching documents");

			// Print retrieved results
			ScoreDoc[] hits = results.scoreDocs;
			int rank = 1;
			for (int i = 0; i < hits.length; i++) {
				//System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
				Document doc = searcher.doc(hits[i].doc);
				String result = queryId + " " + "0" + " " +doc.get("DOCNO") + " "+ rank + " " + hits[i].score + " " + runID;
				allResults.append(result);
				allResults.append("\n");
				// System.out.println("TEXT: "+doc.get("TEXT"));
				//System.out.println(result);
				rank++;
			}
					
			queryId++;
		}
		
		reader.close();
		
		System.out.println("Writing results into "+ outFileName);
		
		utility utObj = new utility();
		utObj.writeFile(outFileName, allResults);


	}

}
