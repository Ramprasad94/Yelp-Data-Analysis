import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;

public class Test {
	public static void main(String args[]) throws IOException{
		
		Map<String, ArrayList<String>> textCollection = new HashMap<String, ArrayList<String>>();
		
		
		String tipFile = "E:\\Search Final Project\\temp_tip_data.csv";
		CSVReader reader = new CSVReader(new FileReader(tipFile));
		
		String[] tipData;
		while((tipData = reader.readNext())!= null){
			ArrayList<String> tipListPerBusiness = null;
			String businessID = tipData[2];
			String tipText = tipData[1];
			if(textCollection.get(businessID) != null){
			tipListPerBusiness = textCollection.get(businessID);
			tipListPerBusiness.add(tipData[1]);	
			}
			else{
				tipListPerBusiness = new ArrayList<String>();
				tipListPerBusiness.add(tipData[1]);	
				textCollection.put(businessID,tipListPerBusiness);
				
				}
		}
		System.out.println(textCollection.size());
		int count = 0;
		for(Map.Entry<String, ArrayList<String>> entry : textCollection.entrySet()){
			String key = entry.getKey();
			System.out.println("Key = "+key);
			ArrayList<String> value = entry.getValue();
			System.out.println("Values:");
			for(String iter : value){
				System.out.println("-->"+iter);
				count++;
			}
			System.out.println("********************************");
			
		}
		System.out.println("Final count = "+ count);
	}
}
