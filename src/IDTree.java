
import java.util.*;
import java.io.*;

class  DataSet{
    public int democrat;
    public Double population;
    public Double population_change;    //increase, decrease
    public Double age65plus;    //low medium high
    public Double black;    //low medium high
    public Double hispanic; //low medium high
    public Double ed_bachelors; //low medium high
    public Double income;   //low medium high
    public Double poverty;  //low medium high
    public Double density;  //low medium high

    public String population_class;
    public String population_change_class;
    public String age65plus_class;
    public String black_class;
    public String hispanic_class;
    public String ed_bachelors_class;
    public String income_class;
    public String poverty_class;
    public String density_class;
}

class Attribute{
    public String name;
    public HashMap<String, Double[]> attributes;
    Double infoD;
    public Double gain;
    int total;

    Attribute(String name){
        attributes = new HashMap<String, Double[]>();
        this.name = name;
        total=0;
        infoD=0.0;
        gain=0.0;
    }

    public void setValues(String class_name, int yes){
        Double[] table = {0.0,0.0,0.0,0.0};

        if(this.attributes.get(class_name)==null){
            this.attributes.put(class_name, table);
        }

        if(yes==1){
            this.attributes.get(class_name)[0]+=1;
        }else{
            this.attributes.get(class_name)[1]+=1;
        }
        total+=1;
    }

    public void calculateEntrophy(Double infoD, Integer total){
        Double intAge = 0.0;

        Iterator it = attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Double[]> pair = (Map.Entry)it.next();
            Double temp[] = pair.getValue();
            Double comp = (temp[0]+temp[1])/total*calc(temp[0],temp[1]);
            intAge+=comp;
        }

        this.gain=infoD-intAge;
    }

    private Double calc(Double p, Double n){
        Double ratio = -Double.valueOf(p)/(p+n);
        ratio *= Math.log10(-ratio) / Math.log10(2.0);
        ratio -= (Double.valueOf(n)/(p+n))*Math.log10(Double.valueOf(n)/(p+n)) / Math.log10(2.0);
        return ratio;

    }
}

class Node{
    Double p;
    Double n;
    String Name;
    ArrayList<Node> node = new ArrayList<Node>();
}

public class IDTree {
    public ArrayList<Record> training = new ArrayList<Record>();
    public ArrayList<Record> test = new ArrayList<Record>();

    ArrayList<DataSet> trainingN = new ArrayList<DataSet>();
    ArrayList<DataSet> testN = new ArrayList<DataSet>();
    public Record greatest = null;
    public Record smallest = null;
    int totalyes = 0;
    Node root = null;

    public IDTree(){
        String[] initMin = {"0","86","-17.0","4.1","0.0","0.002","3.2","19986","0.9","0.1"};
        String[] initMax = {"0","10116705","72.9","52.9","0.851","0.958","74.4","122238","53.2","69467.5"};
        greatest = new Record(initMax);
        smallest = new Record(initMin);
        root = new Node();
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
                if(data[0].equalsIgnoreCase("1")){
                    totalyes+=1;
                }
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

    public static void main(String[] args){
        IDTree decisionTree = new IDTree();
        decisionTree.loadTrainingDataSet("votes-train.csv");
        decisionTree.loadTestDataSet("votes-test.csv");

        decisionTree.normalize("train", decisionTree.training);
        decisionTree.normalize("test", decisionTree.test);

        decisionTree.binify();
        List<Attribute> attributes = decisionTree.getAttributes();

        decisionTree.makeTree(attributes);

        decisionTree.binifyTest();
        decisionTree.predict();

    }

    public void predict(){
        Node temp = root;
        int output = 0;
        int correct = 0;
        for(DataSet ds : testN){
            while(temp.node.size()!=0){
                if(temp.Name==null)
                    temp = next(temp, ds, "black", ds.black_class);
                else if(temp.node.get(0).Name.contains("Bachelors Edu")){
                    temp = next(temp, ds, "Bachelors Edu", ds.ed_bachelors_class);
                }else if(temp.node.get(0).Name.contains("Income")){
                    temp = next(temp, ds, "Income", ds.income_class);
                }else if(temp.node.get(0).Name.contains("Poverty")){
                    temp = next(temp, ds, "Poverty", ds.poverty_class);
                }else if(temp.node.get(0).Name.contains("Population Change")){
                    temp = next(temp, ds, "Population Change", ds.population_change_class);
                }else if(temp.node.get(0).Name.contains("Hispanic")){
                    temp = next(temp, ds, "Hispanic", ds.hispanic_class);
                }else if(temp.node.get(0).Name.contains("age65plus")){
                    temp = next(temp, ds, "age65plus", ds.age65plus_class);
                }else if(temp.node.get(0).Name.contains("Density")){
                    temp = next(temp, ds, "Density", ds.density_class);
                }else if(temp.node.get(0).Name.contains("Population")){
                    temp = next(temp, ds, "Population", ds.population_class);
                }
            }
            output = temp.p>temp.n ? 1 : 0;
            if(ds.democrat==output)
                correct+=1;
            System.out.println("Actual Output:"+ds.democrat+" Predicted Output:"+output);
        }
        Double accuracy = (Double.valueOf(correct)/testN.size())*100;
        System.out.println("Accuracy:"+accuracy);
    }

    private Node next(Node temp, DataSet ds, String type, String clas){
        Node save = null;
        for(Node node : temp.node){
            String x = type.concat(clas);
            if(node.Name.equalsIgnoreCase(x)) {
                save = node;
            }
        }
        return save;
    }

    public void binify(){
        for(DataSet rec : trainingN){
            rec.population_class = rec.population > 0.5? "High" : "Low";
            rec.population_change_class = rec.population_change > 0? "Increase" : "Decrease";
            rec.age65plus_class = rec.age65plus > 0.5? "High" : "Low";
            rec.black_class = rec.black > 0.5? "High" : "Low";
            rec.hispanic_class = rec.hispanic > 0.5? "High" : "Low";
            rec.ed_bachelors_class = rec.ed_bachelors > 0.5? "High" : "Low";

            if(rec.poverty<0.33){
                rec.poverty_class = "Low";
            }else if(rec.poverty>0.33 && rec.poverty<0.66){
                rec.poverty_class = "Medium";
            }else{
                rec.poverty_class = "High";
            }

            if(rec.income<0.33){
                rec.income_class = "Low";
            }else if(rec.income>0.33 && rec.income<0.66){
                rec.income_class = "Medium";
            }else{
                rec.income_class = "High";
            }

            if(rec.density<0.33){
                rec.density_class = "Low";
            }else if(rec.density>0.33 && rec.density<0.66){
                rec.density_class = "Medium";
            }else{
                rec.density_class = "High";
            }
        }
    }

    public void binifyTest(){
        for(DataSet rec : testN){
            rec.population_class = rec.population > 0.5? "High" : "Low";
            rec.population_change_class = rec.population_change > 0? "Increase" : "Decrease";
            rec.age65plus_class = rec.age65plus > 0.5? "High" : "Low";
            rec.black_class = rec.black > 0.5? "High" : "Low";
            rec.hispanic_class = rec.hispanic > 0.5? "High" : "Low";
            rec.ed_bachelors_class = rec.ed_bachelors > 0.5? "High" : "Low";

            if(rec.poverty<0.33){
                rec.poverty_class = "Low";
            }else if(rec.poverty>0.33 && rec.poverty<0.66){
                rec.poverty_class = "Medium";
            }else{
                rec.poverty_class = "High";
            }

            if(rec.income<0.33){
                rec.income_class = "Low";
            }else if(rec.income>0.33 && rec.income<0.66){
                rec.income_class = "Medium";
            }else{
                rec.income_class = "High";
            }

            if(rec.density<0.33){
                rec.density_class = "Low";
            }else if(rec.density>0.33 && rec.density<0.66){
                rec.density_class = "Medium";
            }else{
                rec.density_class = "High";
            }
        }
    }

    public List<Attribute> getAttributes(){
        List<Attribute> list = new ArrayList<Attribute>();
        Attribute population = new Attribute("Population");
        Attribute population_change = new Attribute("Population Change");
        Attribute age65plus = new Attribute("age65plus");
        Attribute black = new Attribute("black");
        Attribute hispanic = new Attribute("Hispanic");
        Attribute bachelors = new Attribute("Bachelors Edu");
        Attribute density = new Attribute("Density");
        Attribute poverty = new Attribute("Poverty");
        Attribute income = new Attribute("Income");

        for(DataSet rec: trainingN){
            population.setValues(rec.population_class,rec.democrat);
            population_change.setValues(rec.population_change_class, rec.democrat);
            age65plus.setValues(rec.age65plus_class, rec.democrat);
            black.setValues(rec.black_class, rec.democrat);
            hispanic.setValues(rec.hispanic_class, rec.democrat);
            bachelors.setValues(rec.ed_bachelors_class, rec.democrat);
            density.setValues(rec.density_class, rec.democrat);
            poverty.setValues(rec.poverty_class, rec.democrat);
            income.setValues(rec.income_class, rec.democrat);
        }


        Double infoD = -Double.valueOf(totalyes)/trainingN.size();
        infoD *= Math.log10(-infoD) / Math.log10(2.0);
        infoD -= Double.valueOf(trainingN.size()-totalyes)/trainingN.size()*Math.log10(Double.valueOf(trainingN.size()-totalyes)/trainingN.size()) / Math.log10(2.0);

        population.calculateEntrophy(infoD, trainingN.size());
        population_change.calculateEntrophy(infoD, trainingN.size());
        age65plus.calculateEntrophy(infoD, trainingN.size());
        black.calculateEntrophy(infoD, trainingN.size());
        hispanic.calculateEntrophy(infoD, trainingN.size());
        bachelors.calculateEntrophy(infoD, trainingN.size());
        density.calculateEntrophy(infoD, trainingN.size());
        poverty.calculateEntrophy(infoD, trainingN.size());
        income.calculateEntrophy(infoD, trainingN.size());

        list.add(population);
        list.add(population_change);
        list.add(age65plus);
        list.add(black);
        list.add(hispanic);
        list.add(bachelors);
        list.add(density);
        list.add(poverty);
        list.add(income);

        return list;
    }

    public void makeTree(List<Attribute> attributes){
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(root);
        while(attributes.size()!=0){
            int index = chooseBest(attributes);
            Attribute temp = attributes.remove(index);
            ArrayList<Node> tempqueue = new ArrayList<Node>();
            int size = queue.size();
            for(int i=0; i<size; i++) {
                Node tempNode = queue.get(i);
                Iterator it = temp.attributes.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry<String, Double[]> pair = (Map.Entry) it.next();
                    Double t[] = pair.getValue();
                    Node newNode = new Node();
                    newNode.Name = temp.name+pair.getKey();
                    newNode.p=t[0];
                    newNode.n=t[1];
                    tempNode.node.add(newNode);
                    tempqueue.add(newNode);
                }
            }
            queue=tempqueue;
        }
    }

    private int chooseBest(List<Attribute> attributes){
        Double gain = 0.0;
        int temp=0;

        for(int i=0;i<attributes.size();i++){
            if(attributes.get(i).gain>gain){
                temp=i;
                gain=attributes.get(i).gain;
            }
        }

        return temp;
    }
}
