import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by noel on 3/23/17.
 */
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