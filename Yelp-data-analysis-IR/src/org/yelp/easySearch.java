package org.yelp;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class easySearch {

	public void computeRelevanceScores(Set<Term> queryTerms,IndexReader reader, IndexSearcher searcher) throws IOException, ParseException{
	
		double relevanceScore = 0;
		int TotalDocsInCorpus = reader.maxDoc();  //To determine the total number of documents in the corpus

		for (Term t : queryTerms) {
			System.out.println("\nCurrent term: "+t.text());
		
		//System.out.println();
		
		// Get document frequency
		int df=reader.docFreq(t);
		System.out.println("\nNumber of documents containing the term: "+t.text()+" for field \"Review\": "+df);
		System.out.println();
		
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
							"Review", new BytesRef(t.text()));
			
					int doc;
					if (de != null) {
						while ((doc = de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
						/*	System.out.println("term" + t.text()+" occurs " + de.freq()
									+ " time(s) in doc(" + (de.docID() + startDocNo)
									+ ")");
						*/
							// Get normalized length (1/sqrt(numOfTokens)) of the document
							
							float normDocLeng = dSimi.decodeNormValue(leafContext.reader()
									.getNormValues("Review").get(doc));
						
						//	float docLeng = 1 / (normDocLeng * normDocLeng);
						
						double termFrequency = (de.freq()/normDocLeng);	
						
						relevanceScore = termFrequency * idf;
						
					//System.out.println("The relevance score for term "+t.text()+" with respect to document "+searcher.doc(de.docID() + startDocNo).get("BusinessID")+" is "+relevanceScore);
					storeScores(searcher.doc(de.docID() + startDocNo).get("BusinessID"),relevanceScore);
					}		
			}
		}
		}
	System.out.println("\nThe relevance score for the query with respect to all documents is: ");
	
	for(Map.Entry<String, Double> me : relevanceScores.entrySet()){
		System.out.println("Document: "+me.getKey()+","+" Relevance Score: "+me.getValue());
	}
	
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
		String queryString = "Italian";	//set the query string

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get(pathToIndex)));
		IndexSearcher searcher = new IndexSearcher(reader);

		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("Review", analyzer);
		Query query = parser.parse(queryString);
		Set<Term> queryTerms = new LinkedHashSet<Term>();
		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
		
		try{
		// Get the preprocessed query terms
		easySearch e = new easySearch();
		e.computeRelevanceScores(queryTerms,reader,searcher);
		}catch(IOException e){
			System.out.println("IO exception has occured");
		}catch(ParseException pe){
			System.out.println("A parse exception has occured");
		}
		reader.close();
		}
	
}
	
	

