package com.example;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DataMining {
    public static final DataMining INSTANCE = new DataMining();
    private static String path;
    private static J48 tree = null;
    private static Instances dataset = null;

    protected DataMining(){}

    public static DataMining GET(){ return INSTANCE; }

    public static void setPath(String path1){
        path=path1;
    }

    public static void updateDataset(String username){
        try{
            dataset = new Instances(new BufferedReader(new FileReader(path + username + "_trainingSet.arff")));
            dataset.setClassIndex(dataset.numAttributes() - 1); //set class att
            System.out.println(dataset.toSummaryString());
        }catch (Exception e) {e.printStackTrace();}
    }

    /*CALL WHEN SET TEMP - after executing writeInstance() buildModel*/
    /*WE ONLY BUILD MODEL WHEN getNumInstances() > 9*/
    /*REQUIRE updateDataset() call before*/
    public static void buildModel(){
        try {
            tree = new J48();
            int n = (int) (dataset.numInstances()*0.1);
            String[] options = new String[]{"-C", "0.6", "-M", ""+n};
            tree.setOptions(options);
            tree.buildClassifier(dataset);
            System.out.println(tree.graph());
        }catch(Exception e){e.printStackTrace();}

    }

    /*input instance array does not contain class attribute, length=5*/
    public static String classifyInst(String[] instance) throws IOException{
        if(instance.length != 5)
            throw new IOException("invalid input array format (array length should be 5)");
        try{
            Instance inst  = new DenseInstance(6);
            Attribute time = new Attribute("time", new ArrayList<String>(Arrays.asList("6~10", "11~13", "14~17", "18~22", "others")), 4);
            inst.setValue(time, instance[4]);
            inst.setValue(0, Double.parseDouble(instance[0]));
            inst.setValue(1, Double.parseDouble(instance[1]));
            //inst.setValue(2, Double.parseDouble(instance[2]));
            inst.setValue(3, Double.parseDouble(instance[3]));
            inst.setValue(time, instance[4]);
            inst.setDataset(dataset);

            dataset.add(0, inst);
            double[] prob=tree.distributionForInstance(dataset.get(0));
            //System.out.println(Arrays.toString(prob));
            double accuracy=prob[0]/prob[1]+prob[0];
            if(accuracy < 60)
                return "do_nothing";

            double cV = tree.classifyInstance(dataset.get(0));
            dataset.delete(0);
            return inst.classAttribute().value((int)cV);
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    /*CALL WHEN SET TEMP - add new instance to existing arff*/
    public static void writeInstance(String[] instance, String username) throws IOException {
        if(instance.length != 5)
            throw new IOException("invalid input array format (array length should be 5)");

        String[] newInstance = Arrays.copyOf(instance, 6);
        int n = newInstance.length;
        if(Double.parseDouble(instance[1]) > Double.parseDouble(instance[2]))
            newInstance[n-1] = "C";
        else if(Double.parseDouble(instance[1]) < Double.parseDouble(instance[2]))
            newInstance[n-1] = "H";
        else
            newInstance[n-1] = "?";

        StringBuilder sb=new StringBuilder();
        for(int i=0 ; i<n-1 ; i++){
            sb.append(newInstance[i]);
            sb.append(",");
        }
        sb.append(newInstance[n-1]);
        System.out.println(sb.toString());

        //https://stackoverflow.com/questions/12652706/how-can-i-delete-line-from-txt
        RandomAccessFile rwStream = null;
        if(getNumInstances(username) > 99){
            try{
                rwStream = new RandomAccessFile(path + username + "_trainingSet.arff","rw");
                int i=0;    int rmvLine=10;
                long offset=0;
                for(i=0 ; i<rmvLine ; i++){
                    if(rwStream.readLine() == null)
                        throw new IOException("line10 does not exist");
                    if(i==rmvLine-2) //before last loop
                        offset = rwStream.getFilePointer();
                }
                long length = rwStream.getFilePointer()-offset; //length of line
                byte[] buffer = new byte[4096];
                int read = -1; // will store byte reads from file.read()
                while ((read = rwStream.read(buffer)) > -1){ //shift all bytes to fill line 10
                    rwStream.seek(rwStream.getFilePointer() - read - length);
                    rwStream.write(buffer, 0, read);
                    rwStream.seek(rwStream.getFilePointer() + length);
                }
                rwStream.setLength(rwStream.length() - length); //truncate by length
            } catch(Exception e){e.printStackTrace();}
            finally {
                try{rwStream.close();} catch (Exception e) { e.printStackTrace(); }
            }
        }

        //append line
        BufferedWriter fstream = null;
        PrintWriter out = null;
        try {
            fstream = new BufferedWriter(new FileWriter(path + username + "_trainingSet.arff", true));
            out = new PrintWriter(fstream);
            out.println(sb.toString());
        }catch(Exception e){e.printStackTrace();}
        finally{
            try{out.close();}catch(Exception e){e.printStackTrace();}
            try{fstream.close();}catch(Exception e){e.printStackTrace();}
        }
    }

    /*THIS METHOD WILL OVERWRITE WHATEVER PREVIOUSLY WERE THERE*/
    public static void initARFF_file(String username){
        FileWriter fstream = null;
        try{
            fstream = new FileWriter(path+username+"_trainingSet.arff"); //you may set file path here
            fstream.write("@relation tempControlData\n");
            fstream.write("@attribute tempOutside numeric\n");
            fstream.write("@attribute tempInside numeric\n");
            fstream.write("@attribute tempSet numeric\n");
            fstream.write("@attribute humidity numeric\n");
            fstream.write("@attribute time {6~10, 11~13, 14~17, 18~22, others}\n");
            fstream.write("@attribute class {H, C}\n\n"); //Heating, Cooling
            fstream.write("@data\n");
        }catch(Exception e){ e.printStackTrace(); }
        finally {
            try{fstream.close();}catch(Exception e){e.printStackTrace();}
        }
    }

    /*check if file exists*/
    public static boolean getFileExists(String username){
        File f = new File(path+username+"_trainingSet.arff");
        if(f.exists() && !f.isDirectory())
            return true;
        return false;
    }

    /*return num of instances in arff*/
    public static int getNumInstances(String username){
        try{
            return (new ConverterUtils.DataSource(path + username + "_trainingSet.arff").getDataSet()).numInstances();
        }catch(Exception e){e.printStackTrace();}
        return -1;
    }
}
