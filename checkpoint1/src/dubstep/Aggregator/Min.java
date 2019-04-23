package dubstep.Aggregator;

import dubstep.Manager.EvaluatorManager;
import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.schema.Column;
import java.util.*;
import java.util.function.Consumer;

public class Min extends Aggregator implements Iterable<Tuple> {

    Map<String,PrimitiveValue> columnValues = new HashMap<>();//like columnValue

    List<Column> groupByAttrs;//GroupByAttrs:FIRSTSEASON,LASTSEASON

    List<Expression> expressionList;



    public Min(ExpressionList expressionList, List<Column> groupByColumns) {

        this.expressionList=expressionList.getExpressions();
        this.groupByAttrs=groupByColumns;
    }

    public void accumulate(Tuple tp){

        try{

            String key=""; // key = "" if no groupBy, key = "filed1"+"field2"+... if has groupBy
            if(groupByAttrs!=null){
                for(Column cols:this.groupByAttrs){
                    String aggrByName = cols.getColumnName();
                    key+=tp.getColumnValue(aggrByName);
                }
            }

            EvaluatorManager em = new EvaluatorManager(this.expressionList.get(0));
            PrimitiveValue newValue= em.evaluateAttr(tp);

            if(!columnValues.containsKey(key)|| em.eval(new MinorThan(newValue,columnValues.get(key))).toBool()){
                columnValues.put(key,newValue);
            }

        }catch (Exception e){e.printStackTrace();}

    }

    @Override
    public int getValueSize(){
        return this.columnValues.size();
    }

    @Override
    public void forEach(Consumer<? super Tuple> action) {

    }

    @Override
    public Spliterator<Tuple> spliterator() {
        return null;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return null;
    }

    @Override
    public PrimitiveValue getValue(String key){
        return this.columnValues.get(key);
    }
}
