import java.util.ArrayList;
import java.util.List;

/**
 * Created by noel on 3/23/17.
 */
class Cluster {

    public List<Record> points;
    public Record centroid;
    public int id;

    public List<DataSet> pointsN;
    public DataSet centroidN;

    public Cluster(int id) {
        this.id = id;
        this.points = new ArrayList();
        this.pointsN = new ArrayList();
        this.centroid = null;
    }

    public List getPoints() {
        return points;
    }

    public void addPoint(Record point) {
        points.add(point);
    }

    public Record getCentroid() {
        return centroid;
    }

    public void setCentroid(Record centroid) {
        this.centroid = centroid;
    }

    public void clear() {
        points.clear();
    }

    public void plotCluster() {
        System.out.println("[Cluster: " + id+"]");
        System.out.print("[Points: \n");
        for(Record p : points) {
            System.out.print(p.democrat);
        }
        System.out.print("]");
    }

    public void printCluster() {
        System.out.println("[Cluster: " + id+"]");
        System.out.print("[Points: \n");
        for(DataSet p : pointsN) {
            System.out.print(p.democrat);
        }
        System.out.print("]");
    }

    public int countOccurrence(){
        int count = 0;
        for(DataSet ds : this.pointsN){
            if(ds.democrat==1){
                count++;
            }
        }
        return count;
    }

}
