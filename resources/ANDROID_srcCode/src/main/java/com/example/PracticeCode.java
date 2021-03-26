package com.example;


import weka.classifiers.trees.J48;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.experiment.Stats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PracticeCode {
    private static final String PATH = "./";
    public static void readFile(String fname){
        try {
            Instances dataset = new Instances(new BufferedReader(new FileReader(fname)));
            System.out.println(dataset.toSummaryString());

            if (dataset.classIndex() == -1) //if class attribute not set
                dataset.setClassIndex(dataset.numAttributes() - 1);
            int numAttribute = dataset.numAttributes() - 1;
            for (int i = 0; i < numAttribute; i++) {
                if (dataset.attribute(i).isNominal()) {
                    System.out.println(i + "th Attribute is Nominal");
                    System.out.println("attribute " + i + " has " + dataset.attribute(i).numValues() + " values"); // <--
                }
                AttributeStats as = dataset.attributeStats(i);
                System.out.println("attribute " + i + " has " + as.distinctCount + " distinct values");
                if (dataset.attribute(i).isNumeric()) {
                    System.out.println(i + "th Attribute is Numeric");
                    Stats s = as.numericStats;
                    System.out.println("attribute " + i + " has min/max value " + s.min + "/" + s.max); //s.sum sumSqr
                }
                int numInst = dataset.numInstances();
                for (int j = 0; j < numInst; j++) {
                    Instance instance = dataset.instance(j);
                    if (instance.isMissing(i))
                        System.out.println(i + "th attribute at " + j + "th instance missing");
                    if (instance.classIsMissing())
                        System.out.println("class missing at " + j + "th instance");
                    double cV = instance.classValue();
                    System.out.println(instance.classAttribute().value((int) cV));
                }
            }
            //        CSVLoader loader = new CSVLoader(); //ARFFLoader for ARFF
            //        loader.setSource(new File(PATH+fname));
            //        Instances dataset = loader.getDataSet();

            J48 tree = new J48();
            tree.buildClassifier(dataset);
            System.out.println(tree.graph());
        }catch(Exception e){e.printStackTrace();}
    }

    public static void writeFile(Instances dataset){
        try{
            ArffSaver saver = new ArffSaver(); //CSVSaver for CSV
            saver.setInstances(dataset);
            saver.setFile(new File(PATH+"new.arff"));
            saver.writeBatch();
        }catch(IOException e){e.printStackTrace();}
    }

}









