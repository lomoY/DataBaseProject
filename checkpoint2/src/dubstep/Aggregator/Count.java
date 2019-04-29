package dubstep.Aggregator;

import dubstep.Manager.EvaluatorManager;
import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Lomo
 *
 */

public class Count extends Aggregator implements Iterable<Tuple>{

    HashMap<String,PrimitiveValue> columnValues = new HashMap<>();

    List<Column> groupByAttrs;

    List<Expression> expressionList;

    public Count(ExpressionList expressionList, List<Column> groupByColumns) {
        if(expressionList!=null){
            this.expressionList=expressionList.getExpressions();
        }
        this.groupByAttrs=groupByColumns;

    }

    public void accumulate(Tuple tp) {

        try{

            String key=""; // key = "" if no groupBy, key = "filed1"+"field2"+... if has groupBy
            if(groupByAttrs!=null){

                for(Column cols:this.groupByAttrs){
                    String aggrByName = cols.getColumnName();
                    key+=tp.getColumnValue(aggrByName);
                }

            }

            if(columnValues.containsKey(key)){

                PrimitiveValue oldValue=columnValues.get(key);

                EvaluatorManager em = new EvaluatorManager();
                PrimitiveValue newValue =em.eval(new Addition(oldValue,new LongValue(1)));
                columnValues.put(key,newValue);

            }else {

                columnValues.put(key,new LongValue(1));
            }
        }catch (Exception e){

            e.printStackTrace();

        }

//        if(newTpList.size()==0){
//
//            for(Expression expression:groupByAttrs){
//                //so far, all expression here is instance of Column
//
//                    String columnName = ((Column) expression).getColumnName();
//                    String tablename =((Column) expression).getTable().getName();
//
//
//                ColumnDefinition definition = tp.getColdefinition(columnName);
//                this.coldefinitions.add(definition);
//            }
//
//        }
//
//        String key="";
//
//        for(Expression expression:groupByAttrs) {
//
//            String columnName = ((Column) expression).getColumnName();
//            String tablename =((Column) expression).getTable().getName();
//            PrimitiveValue value = tp.getColumnValue(columnName);
//            key = key + value.toRawString();
//        }
//
//        if(!newTpList.containsKey(key)){//indicate not present
//
//            Tuple newTp = new Tuple();
//            Column COUNT = new Column(null,"COUNT");
//            newTp.setColumn(COUNT,new LongValue(1));
//            for(Expression expression:groupByAttrs){
//                String columnName = ((Column) expression).getColumnName();
//                String tablename =((Column) expression).getTable().getName();
//                PrimitiveValue value=tp.getColumnValue(columnName);
//                Column col = tp.getRawColumn(columnName);
//                newTp.setColumn(col,value);
//                ColumnDefinition definition = tp.getColdefinition(columnName);
//            }
//
//            newTp.setColdefinition(this.coldefinitions);
//            newTpList.put(key,newTp);
//
//        }else {
//
//            Tuple existTp=newTpList.get(key);
//            LongValue oldValue = (LongValue) existTp.getColumnValue("COUNT");
//            LongValue newValue = new LongValue(oldValue.getValue()+1);
//            existTp.setColumnValue("COUNT",newValue);

        }



//    private class Itr implements Iterator<Tuple>{
//        Iterator it=newTpList.entrySet().iterator();
//        @Override
//        public void remove() {
//
//        }
//
//        @Override
//        public void forEachRemaining(Consumer<? super Tuple> action) {
//
//        }
//
//        @Override
//        public boolean hasNext() {
//            return it.hasNext();
//        }
//
//        @Override
//        public Tuple next() {
//            Map.Entry element = (Map.Entry)it.next();
//            Tuple tp = (Tuple) element.getValue();
//            return tp;
//        }
//    }

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
    public int getValueSize(){
        return this.columnValues.size();
    }

    @Override
    public PrimitiveValue getValue(String key){
        return this.columnValues.get(key);
    }
}