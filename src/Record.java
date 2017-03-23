/**
 * Created by noel on 3/23/17.
 */
class Record {
    public int democrat;
    public Integer population;
    public Double population_change;
    public Double age65plus;
    public Double black;
    public Double hispanic;
    public Double ed_bachelors;
    public Integer income;
    public Double poverty;
    public Double density;


    public int cluster;

    public Record(){

    }

    public Record(String[] data){
        democrat = Integer.valueOf(data[0]);
        population = Integer.valueOf(data[1]);
        population_change = Double.valueOf(data[2]);
        age65plus = Double.valueOf(data[3]);
        black = Double.valueOf(data[4]);
        hispanic = Double.valueOf(data[5]);
        ed_bachelors = Double.valueOf(data[6]);
        income = Integer.valueOf(data[7]);
        poverty = Double.valueOf(data[8]);
        density = Double.valueOf(data[9]);
    }

    public Record(Record r){
        democrat = r.democrat;
        population = r.population;
        population_change = r.population_change;
        age65plus = r.age65plus;
        black = r.black;
        hispanic = r.hispanic;
        ed_bachelors = r.ed_bachelors;
        income = r.income;
        poverty = r.poverty;
        density = r.density;
    }

    public void setCluster(int cluster){
        this.cluster=cluster;
    }
}
