package it.cnr.isti.labsedc.bpmls;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class IntroJSPromptBuilder{
public static String getPromptFile() {
	try{
	Resource learningScenarioXml;
	learningScenarioXml = new UrlResource("classpath:schema/prompt.txt");
	
	BufferedReader buf = new BufferedReader(new InputStreamReader(learningScenarioXml.getInputStream()));
	String line = buf.readLine();
	StringBuilder sb = new StringBuilder(); 
	while(line != null){ 
		sb.append(line).append("\n"); 
		line = buf.readLine(); 
		} 
	String fileAsString = sb.toString();
	
	return fileAsString;
	}catch(Exception e){
		System.out.println(e.toString());
		return "ERROR";
	}
		
	
}
}
