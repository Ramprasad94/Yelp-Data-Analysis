import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
//import java.io.IOException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
//import org.apache.commons.io.FileUtils;
//import org.json.CDL;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class Jsontocsv
{
	public static void main(String myHelpers[])
	{
		
		JSONParser parser = new JSONParser();
        String jsonString = "{\"infile\": [{\"field1\": 11,\"field2\": 12,\"field3\": 13},{\"field1\": 21,\"field2\": 22,\"field3\": 23},{\"field1\": 31,\"field2\": 32,\"field3\": 33}]}";
//        File file = new File("C:\\Darshan\\Indiana University\\Sem 3\\Search\\Project\\data\\yelp_academic_dataset_tip.json");
        File file = new File("C:\\Darshan\\Indiana University\\Sem 3\\Search\\Project\\data\\demo.json");
        JSONObject output;
        
        File outputFile = new File("C:\\Darshan\\Indiana University\\Sem 3\\Search\\Project\\data\\output.csv");
        
        
        try 
        { 
        	System.out.println("Reading JSON file from Java program"); 
        	
        	
        	BufferedReader br = new BufferedReader(new FileReader(file));
//        	JSONObject json = (JSONObject) parser.parse(fileReader); 
        	PrintWriter pw = new PrintWriter(outputFile);
        	
//        	System.out.println("asdasd   " + br.readLine());
			for(String line; (line = br.readLine()) != null; ) 
			{
			
				System.out.println(line);
				JSONObject json = (JSONObject) parser.parse(line); 
				StringBuilder sb = new StringBuilder();
				String user_id = (String) json.get("user_id");
//				user_id = user_id.replaceAll(",", "\\,");
				sb.append(user_id);
				
				sb.append(',');
				
				String text = (String) json.get("text");
				text = text.replaceAll(",", "\\,");
				sb.append(text);
				sb.append(',');
				
				String business_id = (String) json.get("business_id");
//				business_id = business_id.replaceAll(",", "\\,");
				sb.append(business_id);
				sb.append(',');
				
				sb.append(json.get("likes"));
				sb.append(',');
				
				String date = (String) json.get("date");
//				date = date.replaceAll(",", "\\,");
				sb.append(date);
				sb.append(',');
				
				String type = (String) json.get("type");
//				type = type.replaceAll(",", "\\,");
				sb.append(type);
				
				
				System.out.println(sb.toString());
				pw.write(sb.toString());
				
				
				pw.println();
				
		    }
			pw.close();
			br.close();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
    }
}
//        try 
//        {
//            output = new JSONObject(jsonString);
//
//            JSONArray docs = output.getJSONArray("infile");
//            
//            File file=new File("C:\\Darshan\\Indiana University\\Sem 3\\Search\\Project\\output.csv");
//            String csv = CDL.toString(docs);
//            //FileUtils.writeStringToFile(file, csv, null);
//            PrintWriter pw = new PrintWriter(file);
//            pw.write(csv);
//            pw.close();
//        } 
//        catch (JSONException e) 
//        {
//            e.printStackTrace();
//        } 
//        catch (IOException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }        
//    }
//
//}