package dubstep.Aggregator;

import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.PrimitiveValue;

import java.util.HashMap;
import java.util.List;

public class TupleHashMap extends HashMap {

    public TupleHashMap(List<PrimitiveValue> primitiveValueList , Tuple tuple) {

    }


    @Override
    public Object put(Object key, Object value) {
        return super.put(key, value);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
