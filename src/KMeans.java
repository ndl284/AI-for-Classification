
import java.util.ArrayList;

/**
 * Created by noel on 12/5/16.
 */
import java.util.*;
import java.io.*;

public class KMeans {

    //Number of Clusters. This metric should be related to the number of points
    private int NUM_CLUSTERS = 2;

    public ArrayList<Record> training = new ArrayList<Record>();
    public ArrayList<Record> test = new ArrayList<Record>();

    private List<Record> points;
    private List<Cluster> clusters;

    public KMeans() {
        this.points = new ArrayList<Record>();
        this.clusters = new ArrayList<Cluster>();
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

    public static void main(String[] args) {

        KMeans kmeans = new KMeans();

        kmeans.loadTrainingDataSet("votes-train.csv");
        kmeans.init();
        kmeans.calculate();
    }

    //Initializes the process
    public void init() {
        //Create Clusters
        //Set Random Centroids
        for (int i = 0; i < NUM_CLUSTERS;i++){
            Cluster cluster = new Cluster(i);
            Record centroid = training.get(i);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }
    }

    private void plotClusters() {
        for (int i = 0; i < NUM_CLUSTERS ;i++){
            Cluster c = clusters.get(i);

            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while (!finish) {
            //Clear cluster state
            clearClusters();

            List<Record> lastCentroids = getCentroids();

            //Assign points to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            List<Record> currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for (int i = 0; i < lastCentroids.size(); i++){
                distance += euclideanDistance(lastCentroids.get(i), currentCentroids.get(i));
            }
            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);

            if (distance == 0) {
                finish = true;
            }
        }

        plotClusters();
    }

    private void clearClusters() {
        for (Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private List getCentroids() {
        List centroids = new ArrayList(NUM_CLUSTERS);
        for (Cluster cluster : clusters) {
            Record aux = cluster.getCentroid();
            Record point = new Record(aux);
            centroids.add(point);
        }
        return centroids;
    }

    private void assignCluster() {
        double min;
        int cluster = 0;
        double distance = 0.0;

        for (Record point : training) {
            min = Double.MAX_VALUE;
            for (int i = 0; i < NUM_CLUSTERS; i++){
                Cluster c = clusters.get(i);
                distance = euclideanDistance(point, c.getCentroid());
                if ( distance<min){
                    min = distance;
                    cluster = i;
                }
            }
            point.setCluster(cluster);
            clusters.get(cluster).addPoint(point);
        }
    }

    private Double euclideanDistance(Record test, Record train){
        Double distance = 0.0;
        distance+= Math.pow(test.population-train.population, 2);
        distance+=Math.pow(test.population_change-train.population_change, 2);
        distance+= Math.pow(test.age65plus-train.age65plus, 2);
        distance+= Math.pow(test.black-train.black, 2);
        distance+= Math.pow(test.hispanic-train.hispanic, 2);
        distance+= Math.pow(test.ed_bachelors-train.ed_bachelors, 2);
        distance+= Math.pow(Double.valueOf(test.income-train.income), 2);
        distance+= Math.pow(test.poverty-train.poverty, 2);
        distance+= Math.pow(test.density-train.density, 2);

        return Math.sqrt(distance);
    }

    private void calculateCentroids() {
        for (Cluster cluster : clusters) {
            double population = 0;
            double population_change = 0;
            double age65plus = 0;
            double black = 0;
            double hispanic = 0;
            double ed_bachelors = 0;
            double income = 0;
            double poverty = 0;
            double density = 0;

            List<Record> list = cluster.getPoints();
            int n_points = list.size();

            for (Record point : list) {
                population+= point.population;
                population_change+= point.population_change;
                age65plus+= point.age65plus;
                black+= point.black;
                hispanic+= point.hispanic;
                ed_bachelors+=point.ed_bachelors;
                income+=point.income;
                poverty+= point.poverty;
                density+= point.density;
            }

            Record centroid = cluster.getCentroid();
            if (n_points>0){
                centroid.population=(int)population/n_points;
                centroid.population_change=population_change/n_points;
                centroid.age65plus=age65plus/n_points;
                centroid.black=black/n_points;
                centroid.hispanic=hispanic/n_points;
                centroid.ed_bachelors=ed_bachelors/n_points;
                centroid.income=(int)income/n_points;
                centroid.poverty=poverty/n_points;
                centroid.density=density/n_points;
            }
        }
    }
}