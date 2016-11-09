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
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;

public class GenerateIndex {

	public static void main(String args[]) {

		// data files
		String reviewsFile = "temp.csv";
		String tipsFile = "temp.csv";

		try {

			// Map
			// Key: Business IDs
			// values: Reviews/Tips for all business IDs
			Map<String, ArrayList<String>> reviewsCollection = buildReviewsAndTipsList(reviewsFile);
			Map<String, ArrayList<String>> tipsCollection = buildReviewsAndTipsList(tipsFile);

			// Generate index
			indexGeneration(reviewsCollection, tipsCollection);

		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	public static Map<String, ArrayList<String>> buildReviewsAndTipsList(String dataset) throws IOException {

		BufferedReader br = null;
		String line = "";
		String delim = ",";

		Map<String, ArrayList<String>> textCollection = new HashMap<String, ArrayList<String>>();

		try {

			// Read data file
			br = new BufferedReader(new FileReader(dataset));

			// Read each line and extract reviews
			while ((line = br.readLine()) != null) {

				String[] reviewData = line.split(delim);

				// Extract business ID
				String businessId = reviewData[1];

				// Extract reviews from each line
				String reviewText = reviewData[4];

				// If there are multiple reviews for each business, append all
				// reviews in the list
				ArrayList<String> reviewsListPerBusiness = null;
				if (textCollection.get(businessId) != null) {
					reviewsListPerBusiness = textCollection.get(businessId);
				} else {
					reviewsListPerBusiness = new ArrayList<String>();
				}

				reviewsListPerBusiness.add(reviewText);

				// Add reviews list into map
				textCollection.put(businessId, reviewsListPerBusiness);

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

		// build lucene documents using reviews and tips
		for (String businessId : businessesList) {

			System.out.println(businessId);
			Document doc = new Document();

			// Add all the reviews in the document for that business
			for (String review : reviewsCollection.get(businessId)) {
				doc.add(new StringField("Review", review, Field.Store.YES));
			}

			// Add all the reviews in the document for that business
			for (String review : tipsCollection.get(businessId)) {
				doc.add(new StringField("Tip", review, Field.Store.YES));
			}

			writer.addDocument(doc);

		}

		writer.forceMerge(1);
		writer.commit();
		writer.close();

	}

}
