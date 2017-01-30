import java.util.*;
import java.io.*;
import java.text.*;

class Perceptron
{
    static int MAX_ITER = 100;
    static double LEARNING_RATE = 0.1;
    static int theta = 0;

    ArrayList<Record> training = new ArrayList<Record>();
    ArrayList<Record> test = new ArrayList<Record>();

    ArrayList<DataSet> trainingN = new ArrayList<DataSet>();
    ArrayList<DataSet> testN = new ArrayList<DataSet>();

    public Record greatest = null;
    public Record smallest = null;

    public Perceptron(){
        String[] initMin = {"0","86","-17.0","4.1","0.0","0.002","3.2","19986","0.9","0.1"};
        String[] initMax = {"0","10116705","72.9","52.9","0.851","0.958","74.4","122238","53.2","69467.5"};
        greatest = new Record(initMax);
        smallest = new Record(initMin);
    }

    public void loadTrainingDataSet(String filename){
        BufferedReader bf= null;
        String line = "";

        try {
            bf = new BufferedReader(new FileReader(filename));
            bf.readLine();
            while((line=bf.readLine())!=null){
                String[] data = line.split(",");
                training.add(new Record(data));
            }
        }catch(Exception e){
            System.out.println("Error occurred while reading file"+e);
        }
    }

    public void loadTestDataSet(String filename){
        BufferedReader bf= null;
        String line = "";

        try {
            bf = new BufferedReader(new FileReader(filename));
            bf.readLine();
            while((line=bf.readLine())!=null){
                String[] data = line.split(",");
                test.add(new Record(data));
            }
        }catch(Exception e){
            System.out.println("Error occurred while reading file"+e);
        }
    }

    public void normalize(String setType, ArrayList<Record> data){
        for(Record row : data){
            DataSet ds = new DataSet();
            ds.democrat=row.democrat;
            ds.population = Double.valueOf(row.population-smallest.population)/(greatest.population-smallest.population);
            ds.population_change = row.population_change;
            ds.age65plus = (row.age65plus-smallest.age65plus)/(greatest.age65plus-smallest.age65plus);
            ds.black = (row.black-smallest.black)/(greatest.black-smallest.black);
            ds.hispanic = (row.hispanic-smallest.hispanic)/(greatest.hispanic-smallest.hispanic);
            ds.ed_bachelors = (row.ed_bachelors-smallest.ed_bachelors)/(greatest.ed_bachelors-smallest.ed_bachelors);
            ds.poverty = (row.poverty-smallest.poverty)/(greatest.poverty-smallest.poverty);
            ds.income = Double.valueOf(row.income-smallest.income)/(greatest.income-smallest.income);
            ds.density = (row.density-smallest.density)/(greatest.density-smallest.density);

            if(setType=="train"){
                this.trainingN.add(ds);
            }else{
                this.testN.add(ds);
            }
        }
    }

    public static void main(String args[]){

        Perceptron deducer = new Perceptron();
        deducer.loadTrainingDataSet("votes-train.csv");
        deducer.loadTestDataSet("votes-test.csv");

        deducer.normalize("train", deducer.training);
        deducer.normalize("test", deducer.test);

        double[] weights = new double[10];// 9 for input and 1 for bias for input variables and one for bias
        double localError, globalError;
        int i, p, iteration, output;

        weights[0] = randomNumber(0,1);// w1
        weights[1] = randomNumber(0,1);// w2
        weights[2] = randomNumber(0,1);// w3
        weights[3] = randomNumber(0,1);// w4
        weights[4] = randomNumber(0,1);// w5
        weights[5] = randomNumber(0,1);// w6
        weights[6] = randomNumber(0,1);// w7
        weights[7] = randomNumber(0,1);// w8
        weights[8] = randomNumber(0,1);// w9
        weights[9] = randomNumber(0,1);// this is the bias

        iteration = 0;
        do {
            iteration++;
            globalError = 0;
            //loop through all instances (complete one epoch)
            for(DataSet r : deducer.trainingN) {
                output = calculateOutput(theta,weights, r);
                localError = r.democrat - output;
                weights[0] += LEARNING_RATE * localError * r.population;
                weights[1] += LEARNING_RATE * localError * r.population_change;
                weights[2] += LEARNING_RATE * localError * r.age65plus;
                weights[3] += LEARNING_RATE * localError * r.black;
                weights[4] += LEARNING_RATE * localError * r.hispanic;
                weights[5] += LEARNING_RATE * localError * r.ed_bachelors;
                weights[6] += LEARNING_RATE * localError * r.income;
                weights[7] += LEARNING_RATE * localError * r.poverty;
                weights[8] += LEARNING_RATE * localError * r.density;
                weights[9] += LEARNING_RATE * localError;
                globalError += (localError*localError);
            }
        } while (globalError != 0 && iteration<=MAX_ITER);

        double accuratecount = 0.0;
        for(DataSet t : deducer.testN){
            output = calculateOutput(theta, weights, t);
            if(output == t.democrat){
                accuratecount++;
            }
            System.out.println("Actual Output:"+t.democrat+" Predicted Output:"+output);
        }
        double percent = accuratecount*100/deducer.testN.size();
        System.out.println("Accuracy="+percent);
    }//end main


    public static double randomNumber(int min , int max) {
        DecimalFormat df = new DecimalFormat("#.####");
        double d = min + Math.random() * (max - min);
        String s = df.format(d);
        double x = Double.parseDouble(s);
        return x;
    }

    static int calculateOutput(int theta, double weights[], DataSet row)
    {
        double sum = row.population * weights[0] + row.population_change * weights[1] + row.age65plus * weights[2] + row.black * weights[3]
                        + row.hispanic * weights[4] + row.ed_bachelors * weights[5] + row.income * weights[6]
                        + row.poverty * weights[7] + row.density * weights[8] + weights[9];
        return (sum >= theta) ? 1 : 0;
    }

}