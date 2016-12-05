package org.yelp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;
import com.opencsv.CSVReader;

public class GenerateIndex {

	public static void main(String args[]) {

		// data files
		String reviewsFile = "../data/ReviewData100k.csv";
		String tipsFile = "../data/TipData100k.csv";

		try {

			// Map
			// Key: Business IDs
			// values: Reviews/Tips for all business IDs
			Map<String, ArrayList<String>> reviewsCollection = buildReviewsAndTipsList(reviewsFile);
			Map<String, ArrayList<String>> tipsCollection = buildTipsList(tipsFile);

			//System.out.println(reviewsCollection.keySet());
			// Generate index
			indexGeneration(reviewsCollection, tipsCollection);
			
			analyseIndexes("indexes");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param dataset
	 * @return
	 * @throws IOException
	 */
	/**
	 * @param dataset
	 * @return
	 * @throws IOException
	 */
	public static Map<String, ArrayList<String>> buildReviewsAndTipsList(String dataset) throws IOException {

		Map<String, ArrayList<String>> textCollection = new HashMap<String, ArrayList<String>>();

		try {

			// Read data file
			CSVReader reader = new CSVReader(new FileReader(dataset));
			String[] reviewData;
			int i = 0;
			while ((reviewData = reader.readNext()) != null) {
				if(reviewData.length == 8){
					System.out.println("Processing line " + i++);
					ArrayList<String> reviewListPerBusiness = null;
					String businessID = reviewData[4];
					if (textCollection.get(businessID) != null) {
						reviewListPerBusiness = textCollection.get(businessID);
						reviewListPerBusiness.add(reviewData[3]);
					} else {
						reviewListPerBusiness = new ArrayList<String>();
						reviewListPerBusiness.add(reviewData[3]);
						textCollection.put(businessID, reviewListPerBusiness);

					}
					
				}
				
			}
			System.out.println("SIZE OF REVIEW COLLECTION:" + textCollection.size());
			int count = 0;
			for (Map.Entry<String, ArrayList<String>> entry : textCollection.entrySet()) {
				String key = entry.getKey();
				// System.out.println("Key = "+key);
				ArrayList<String> value = entry.getValue();
				// System.out.println("Values:");
				for (String iter : value) {
					// System.out.println("-->"+iter);
					count++;
				}
				// System.out.println("********************************");

			}
			System.out.println("Final count = " + count);
			// System.out.println("----------------------------------------------------------END
			// OF REVIEW TEXT
			// PARSING----------------------------------------------");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return textCollection;

	}

	public static Map<String, ArrayList<String>> buildTipsList(String dataset) throws IOException {

		Map<String, ArrayList<String>> textCollection = new HashMap<String, ArrayList<String>>();

		try {

			// Read data file
			CSVReader reader = new CSVReader(new FileReader(dataset));

			String[] tipData;
			while ((tipData = reader.readNext()) != null) {
				ArrayList<String> tipListPerBusiness = null;
				String businessID = tipData[2];
				String tipText = tipData[1];
				if (textCollection.get(businessID) != null) {
					tipListPerBusiness = textCollection.get(businessID);
					tipListPerBusiness.add(tipData[1]);
				} else {
					tipListPerBusiness = new ArrayList<String>();
					tipListPerBusiness.add(tipData[1]);
					textCollection.put(businessID, tipListPerBusiness);

				}

			}

			System.out.println("SIZE OF TIP COLLECTION:" + textCollection.size());
			int count = 0;
			for (Map.Entry<String, ArrayList<String>> entry : textCollection.entrySet()) {
				String key = entry.getKey();
				// System.out.println("Key = "+key);
				ArrayList<String> value = entry.getValue();
				// System.out.println("Values:");
				for (String iter : value) {
					// System.out.println("-->"+iter);
					count++;
				}
				// System.out.println("********************************");

			}
			System.out.println("Final count = " + count);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return textCollection;

	}

	public static void indexGeneration(Map<String, ArrayList<String>> reviewsCollection,
			Map<String, ArrayList<String>> tipsCollection) throws IOException {

		String indexPath = "indexes";

		Directory dir = FSDirectory.open(Paths.get(indexPath));

		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);

		// Generate list of businesses
		List<String> businessesList = new ArrayList<String>(reviewsCollection.keySet());
		businessesList.addAll(tipsCollection.keySet());

		// Remove duplicate business ids
		Set<String> uniqueBusinesses = new HashSet<String>();
		uniqueBusinesses.addAll(businessesList);
		businessesList.clear();
		businessesList.addAll(uniqueBusinesses);
		
		// List all businesses list
		//System.out.println(businessesList);

		// build lucene documents using reviews and tips
		for (String businessId : businessesList) {
			
			if(businessId == null){
				System.out.println("Null business ID found");
				continue;
			}
			
			boolean isBusinessIdExist = false;

			// System.out.println("Business ID = "+businessId);
			Document doc = new Document();

			// Add all the reviews in the document for that business
			if (reviewsCollection.containsKey(businessId)) {

				for (String review : reviewsCollection.get(businessId)) {
					doc.add(new TextField("BusinessID", businessId, Field.Store.YES));
					isBusinessIdExist = true;
					doc.add(new TextField("Text", review.toLowerCase(), Field.Store.YES));
				}
			}

			// Add all the tips in the document for that business
			if (tipsCollection.containsKey(businessId)) {
				for (String tip : tipsCollection.get(businessId)) {
					if(!isBusinessIdExist){
						doc.add(new TextField("BusinessID", businessId, Field.Store.YES));
					}
					doc.add(new TextField("Text", tip.toLowerCase(), Field.Store.YES));
				}

			}

			writer.addDocument(doc);

		}

		writer.forceMerge(1);
		writer.commit();
		writer.close();

	}
	
	public static void analyseIndexes(String indexPath) throws IOException {
		IndexReader iReader = DirectoryReader.open(FSDirectory.open(Paths.get((indexPath))));

		System.out.println("==================================================================");
		// Print the total number of documents in the corpus
		System.out.println("Total number of documents in the corpus: " + iReader.maxDoc());

		Terms vocabulary = MultiFields.getTerms(iReader, "Text");

		// Print the size of the vocabulary for <field>TEXT</field>, applicable
		// when the index has only one segment.
		System.out.println("Size of the vocabulary for this field: " + vocabulary.size());

		// Print the total number of documents that have at least one term for
		// <field>TEXT</field>
		System.out
				.println("Number of documents that have at least one term for this field: " + vocabulary.getDocCount());

		// Print the total number of tokens for <field>TEXT</field>
		System.out.println("Number of tokens for this field: " + vocabulary.getSumTotalTermFreq());

		// Print the total number of postings for <field>TEXT</field>
		System.out.println("Number of postings for this field: " + vocabulary.getSumDocFreq());

		System.out.println("==================================================================");

		// Print the vocabulary for <field>TEXT</field>
		/*
		 * TermsEnum iterator = vocabulary.iterator(); BytesRef byteRef = null;
		 * System.out.println(
		 * "========================Vocabulary-Start==========================")
		 * ; int i = 0; while ((byteRef = iterator.next()) != null) { String
		 * term = byteRef.utf8ToString(); System.out.print(term + " "); i++;
		 * 
		 * if (i > 10) break; } System.out.println(); System.out.println(
		 * "========================Vocabulary-End==========================");
		 */
		System.out.println();
		iReader.close();
	}

}
