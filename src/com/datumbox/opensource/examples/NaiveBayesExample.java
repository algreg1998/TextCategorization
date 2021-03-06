                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      /* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.datumbox.opensource.examples;

import com.datumbox.opensource.classifiers.NaiveBayes;
import com.datumbox.opensource.dataobjects.NaiveBayesKnowledgeBase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 * @see <a href="http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/">http://blog.datumbox.com/developing-a-naive-bayes-text-classifier-in-java/</a>
 */
public class NaiveBayesExample {

    /**
     * Reads the all lines from a file and places it a String array. In each 
     * record in the String array we store a training example text.
     * 
     * @param url
     * @return
     * @throws IOException 
     */
    public static String[] readLines(URL url) throws IOException {

        Reader fileReader = new InputStreamReader(url.openStream(), Charset.forName("UTF-8"));
        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines.toArray(new String[lines.size()]);
    }
    
    /**
     * Main method
     * 
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Map<String, URL> testingFiles = new HashMap<>();
        Map<String, URL> trainingFiles = new HashMap<>();
        
        Map<String, String[]> testingExamples = new HashMap<>();
        Map<String, String[]> trainingExamples = new HashMap<>();
        int aH=0,aL=0,aHN=0,aLN=0,pH=0,pL=0;
        String output;
        
        trainingFiles.put("low", NaiveBayesExample.class.getResource("/datasets/train/low.txt"));
        trainingFiles.put("high", NaiveBayesExample.class.getResource("/datasets/train/high.txt"));
        
        //loading examples in memory
        for(Map.Entry<String, URL> entry : trainingFiles.entrySet()) {
            trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
        }
        
        //train classifier
        NaiveBayes nb = new NaiveBayes();
        nb.setChisquareCriticalValue(6.63); //0.01 pvalue
        nb.train(trainingExamples);
        
        //get trained classifier knowledgeBase
        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
        
        nb = null;
        trainingExamples = null;
        
        //Use classifier
        nb = new NaiveBayes(knowledgeBase);
        
//        String exampleEn = "I am English";
//        String outputEn = nb.predict(exampleEn);
        
        
        
        testingFiles.put("low", NaiveBayesExample.class.getResource("/datasets/test/low.txt"));
        testingFiles.put("high", NaiveBayesExample.class.getResource("/datasets/test/high.txt"));
        
        
        for(Map.Entry<String, URL> entry : testingFiles.entrySet()) {
            testingExamples.put(entry.getKey(), readLines(entry.getValue()));
            if(entry.getKey() == "low"){
                pL = readLines(entry.getValue()).length;
            }else if(entry.getKey() == "high"){
                pH = readLines(entry.getValue()).length;
            }
        }
        
        for(Map.Entry<String, String[]> entry : testingExamples.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            for(int i = 0 ; i< value.length;i++){
                output = nb.predict(value[i]);
                if(key == "low"){
                    if(output == key){
                        aL++;                        
                    }else{
                        aLN++;  
                        System.out.println("Predicted "+key+" Actual "+ output +" ||Text : "+value[i]);
                    }
                }else if(key == "high"){
                    if(output == key){
                        aH++;
                    }else{
                        aHN++;
                        System.out.println("Predicted "+key+" Actual "+ output +" ||Text : "+value[i]);
                    }
                }
                
            }
        }
        
        System.out.println("Low || Predicted - "+pL+" Actual - "+aL+" Wrongly predicted:"+aLN);
        System.out.println("High || Predicted - "+pH+" Actual - "+aH+" Wrongly predicted:"+aHN);
        
        

    }
    
}
