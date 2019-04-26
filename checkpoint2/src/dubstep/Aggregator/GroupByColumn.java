package dubstep.Aggregator;

import dubstep.Manager.EvaluatorManager;
import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;


public class GroupByColumn extends Aggregator implements Iterable<Tuple> {

    HashMap<String,PrimitiveValue> columnValues = new HashMap<>();
    ColumnDefinition colDef = new ColumnDefinition();

    List<Column> groupByAttrs;

    String itemName;
    String alias;


    public GroupByColumn(SelectExpressionItem _sei, List<Column> groupByColumns) {

        itemName=_sei.getExpression().toString();
        alias=_sei.getAlias();
        this.groupByAttrs=groupByColumns;

    }

    public void accumulate(Tuple tp) {

//        if(colDef.getColumnName()==null){
//            colDef=tp.getColdefinition(itemName);
//        }
//        if(alias!=null){
//            colDef.setColumnName(alias);
//        }


        try{

            String key=""; // key = "" if no groupBy, key = "filed1"+"field2"+... if has groupBy
            if(groupByAttrs!=null){

                for(Column cols:this.groupByAttrs){
                    String aggrByName = cols.getColumnName();
                    key+=tp.getColumnValue(aggrByName);
                }
            }

            if(columnValues.containsKey(key)){

            }else {
                columnValues.put(key,tp.getColumnValue(itemName));
            }
        }catch (Exception e){

            e.printStackTrace();

        }
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
    public int getValueSize(){
        return this.columnValues.size();
    }

    @Override
    public PrimitiveValue getValue(String key){
        return this.columnValues.get(key);
    }

    public ColumnDefinition getDefinition(){
        return this.colDef;
    }
}
