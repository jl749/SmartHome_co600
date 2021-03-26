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

    public static void main(String[] args) {
        try{
            String name = "Igor";
            DataMining.GET().initARFF_file(name);
            DataMining.GET().writeInstance(new String[] {"11","15","20","54","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"7","16","20","53","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"10","13","19","53","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"4","10","18","53","18~22"}, name);
            DataMining.GET().writeInstance(new String[] {"7","13","19","50","18~22"}, name);
            DataMining.GET().writeInstance(new String[] {"5","14","18","64","18~22"}, name);
            DataMining.GET().writeInstance(new String[] {"12","13","15","43","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"9","13","19","57","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"8","13","19","56","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"12","13","14","58","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"21","15","10","64","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"17","16","10","63","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"20","13","9","63","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"14","10","8","63","18~22"}, name);
            DataMining.GET().writeInstance(new String[] {"17","13","9","60","18~22"}, name);
            DataMining.GET().writeInstance(new String[] {"15","14","8","74","18~22"}, name);
            DataMining.GET().writeInstance(new String[] {"22","13","5","53","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"19","13","9","67","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"18","13","9","66","6~10"}, name);
            DataMining.GET().writeInstance(new String[] {"22","13","4","63","6~10"}, name);
            if(DataMining.GET().getNumInstances(name) > 9 && DataMining.GET().getFileExists(name)){
                System.out.println("DM starting......");
                DataMining.GET().updateDataset(name); //update static Instances
                DataMining.GET().buildModel(); //update J48
                System.out.println(DataMining.GET().classifyInst(new String[] {"8","13","19","56","6~10"}));
                System.out.println(DataMining.GET().classifyInst(new String[] {"30","25","15","86","11~13"}));
            }

        }catch(Exception e){e.printStackTrace();}
    }
}
