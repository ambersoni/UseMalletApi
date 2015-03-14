/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package quy.datamining.malletapi.lda;

/**
 *
 * @author Quy
 */
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import cc.mallet.types.*;
import java.io.*;

import java.util.*;
import java.util.regex.*;

public class TopicModelTraining {

    public static void main(String[] args) throws Exception {        
        String filePath = "data/sample_vi_148k.txt";
        File serializedFile = new File("data/state.gz");
        int numTopics = 100;
        
        
        //String filePath = "data/ap.txt";
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add( new CharSequenceLowercase() );//\\b(\\p{L}|_)//\\p{L}[\\p{L}\\p{P}]+\\p{L}
        //pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\\\p{L}[\\\\p{L}\\\\p{P}]+\\\\p{L}",Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE)) );
        //pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}_]+\\p{L}",Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE)) );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        //pipeList.add( new TokenSequenceRemoveStopwords(new File("data/en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));
        System.out.println("Import data...");
        //input file
        Reader fileReader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
        instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                                               3, 2, 1)); // data, label, name fields
        
        // Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        
        ParallelTopicModel model = new ParallelTopicModel(numTopics,0.5, 0.1);
        

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(20);

        // Run the model for 50 iterations and stop (this is for testing only, 
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(1000);   
        model.addInstances(instances);        
        model.setSaveSerializedModel(1000, "data/model.txt");  
        model.estimate();  
        //print topic result
        PrintWriter writer = new PrintWriter(new File("data/twords.txt"));
        Object[][] words = model.getTopWords(200);
        for(int i=0; i < words.length;i++){
            writer.println("Topic" + i + ": ");
            for(int j=0; j< words[i].length;j++){
                writer.println("\t" + words[i][j]);
            }            
        }
        writer.close();        
    }

}
