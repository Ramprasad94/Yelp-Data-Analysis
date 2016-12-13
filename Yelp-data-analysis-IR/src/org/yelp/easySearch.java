package org.yelp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class easySearch {

	public void computeRelevanceScores(Set<Term> queryTerms,IndexReader reader, IndexSearcher searcher, HashMap<String, HashMap<String, Double>> businessCatMap,String category) throws IOException, ParseException{
	
		double relevanceScore = 0;
		int TotalDocsInCorpus = reader.maxDoc();  //To determine the total number of documents in the corpus
		Iterator<Term> itr = queryTerms.iterator();
        System.out.println("The query terms: ");
		while(itr.hasNext()){
            System.out.println(itr.next());
        }
		
		
		for (Term t : queryTerms) {
			System.out.println("\nCurrent term: "+t.text());
		
		//System.out.println();
		
		// Get document frequency
		int df=reader.docFreq(t);
		//System.out.println("\nNumber of documents containing the term: "+t.text()+" for field \"Text\": "+df);
		//System.out.println();
		if(df == 0){
		df = 1;
		}
		//calculating IDF
		
		double idf = Math.log10(1 + (TotalDocsInCorpus/df));
		//System.out.println("\nIDF Value for term: "+t.text()+" : "+idf+"\n");
		
		// Get document length and term frequency
		
		// Use DefaultSimilarity.decodeNormValue(…) to decode normalized document length
		
		ClassicSimilarity dSimi = new ClassicSimilarity();
		// Get the segments of the index
		
		List<LeafReaderContext> leafContexts = reader.getContext().reader()
				.leaves();
		// Processing each segment
		
		for (int i = 0; i < leafContexts.size(); i++) {
			// Get document length
		
			LeafReaderContext leafContext = leafContexts.get(i);
			
			int startDocNo = leafContext.docBase;
			
			// Get frequency of the term "police" from its postings
			PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(),
							"Text", new BytesRef(t.text()));
			
					int doc;
					if (de != null) {
						while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
						/*	System.out.println("term" + t.text()+" occurs " + de.freq()
									+ " time(s) in doc(" + (de.docID() + startDocNo)
									+ ")");
						*/
							// Get normalized length (1/sqrt(numOfTokens)) of the document
							
							float normDocLeng = dSimi.decodeNormValue(leafContext.reader()
									.getNormValues("Text").get(doc));
						
						//	float docLeng = 1 / (normDocLeng * normDocLeng);
						
						double termFrequency = (de.freq()/normDocLeng);	
						
						relevanceScore = termFrequency * idf;
						
					//System.out.println("The relevance score for term "+t.text()+" with respect to document "+searcher.doc(de.docID() + startDocNo).get("BusinessID")+" is "+relevanceScore);
					storeScores(searcher.doc(de.docID() + startDocNo).get("BusinessID"),relevanceScore);
					}		
			}
		}
		}
	//System.out.println("\nThe relevance score for the query with respect to all documents is: ");
//	double maxValue = Integer.MIN_VALUE;
//	String businessId = "";
	for(Map.Entry<String, Double> me : relevanceScores.entrySet()){
//		if(me.getValue() > maxValue){
//			maxValue = me.getValue();
//			businessId = me.getKey();
//		}
		if(businessCatMap.containsKey(me.getKey())){
			HashMap<String, Double> hm = businessCatMap.get(me.getKey());
			hm.put(category, me.getValue());
			
		}
		else{
			HashMap<String, Double> hm = new HashMap<String,Double>();
			hm.put(category, me.getValue());
			businessCatMap.put(me.getKey(), hm);
		}
//		System.out.println("Document: "+me.getKey()+","+" Relevance Score: "+me.getValue());
	}
	
	
//	if(businessCatMap.containsKey(businessId)){
//		HashMap<String, Double> hm = businessCatMap.get(businessId);
//		hm.put(category, maxValue);
//		
//	}
//	else{
//		HashMap<String, Double> hm = new HashMap<String,Double>();
//		hm.put(category, maxValue);
//		businessCatMap.put(businessId, hm);
//	}
//	HashMap<String, Double> hm = new HashMap<String,Double>();
//	hm.put(category, maxValue);
	
//	if(businessCatMap.containsKey(businessId)){
//		HashMap<String, Double> hm = businessCatMap.get(businessId);
//		hm.put(category, maxValue);
//		
//	}
//	else{
//		HashMap<String, Double> hm = new HashMap<String,Double>();
//		hm.put(category, maxValue);
//		businessCatMap.put(businessId, hm);
//	}
	
	}
	
	HashMap<String,Double> relevanceScores = new HashMap<String,Double>();
	
	public void storeScores(String docID, Double score){
		
		if(relevanceScores.containsKey(docID)){
			Double val = relevanceScores.get(docID);
			val = val + score;
			relevanceScores.put(docID, val);
		}
		else{
			
			relevanceScores.put(docID,score);
		}
	
		}
		
	public static void main(String args[]) throws IOException, ParseException{
		
		String pathToIndex = "indexes";
		String queryPath  = "E:/Search Final Project/categories.csv";
		ArrayList<String> categoryString = new ArrayList<String>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(queryPath))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	categoryString.add(line);
		    }
		    // line is not visible here.
		}
		
				
		//System.out.println(categoryString);
		//System.out.println(categoryString.size());
		//String queryString = "Italian";	//set the query string
		HashMap<String, HashMap<String, Double>> businessCatMap = new HashMap<String, HashMap<String, Double>>(); 
		for(String category: categoryString){
			System.out.println("The category is: "+category);
				
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
					.get(pathToIndex)));
			IndexSearcher searcher = new IndexSearcher(reader);
	
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("Text", analyzer);
			
			Query query = parser.parse(category);
			Set<Term> queryTerms = new LinkedHashSet<Term>();
			searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
			
			try{
			// Get the preprocessed query terms
			easySearch e = new easySearch();
			e.computeRelevanceScores(queryTerms,reader,searcher, businessCatMap,category);
			}catch(IOException e){
				System.out.println("IO exception has occured");
			}catch(ParseException pe){
				System.out.println("A parse exception has occured");
			}
			
			reader.close();
		}
		
		for(Map.Entry<String, HashMap<String,Double>> outer : businessCatMap.entrySet()){
			System.out.println("Business Id: " + outer.getKey());
			System.out.println();
			HashMap<String, Double> innerMap = outer.getValue();
			Map<String, Double> sortedMap = sortByValue(innerMap);
			//System.out.println(sortedMap);
			int n =0;
			CSVWriter csvWriter= new CSVWriter(new FileWriter("C:/Users/Ramprasad/Desktop/result.csv", true));
			ArrayList arrayList=new ArrayList<>();
			arrayList.add(outer.getKey());
			for(Map.Entry<String, Double> inner : sortedMap.entrySet()){
				if(n<=2){
				arrayList.add(inner.getKey());
				System.out.print("Category: " + inner.getKey() + "Value  "  + inner.getValue());
				n++;
				}
			}
			Object[] a = arrayList.toArray();
			String[] res = Arrays.copyOf( a, a.length, String[].class);
			csvWriter.writeNext(res);
			csvWriter.close();
			System.out.println();
	}
		
		
		
}//end of main
	//sort a hash map by values . 
	public static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue( Map<K, V> map )
{
    List<Map.Entry<K, V>> list =
        new LinkedList<>( map.entrySet() );
    Collections.sort( list, new Comparator<Map.Entry<K, V>>()
    {
        @Override
        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
        {
            return -1*(( o1.getValue() ).compareTo( o2.getValue() ));
        }
    } );

    Map<K, V> result = new LinkedHashMap<>();
    for (Map.Entry<K, V> entry : list)
    {
        result.put( entry.getKey(), entry.getValue() );
    }
    return result;
}
	}//end of class
	
	

