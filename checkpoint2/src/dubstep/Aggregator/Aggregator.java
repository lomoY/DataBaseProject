package dubstep.Aggregator;

import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.PrimitiveValue;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lomo
 * Aggregator Marker Interface
 */

public abstract class Aggregator {

    public Map<String,PrimitiveValue> columnValues = new HashMap<>();

    public abstract void accumulate(Tuple tp);
    public int getValueSize(){
        return this.columnValues.size();
    }
    public PrimitiveValue getValue(String key){
        return this.columnValues.get(key);
    }
}
