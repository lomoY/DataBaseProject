package dubstep.Aggregator;

import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Lomo Yang
 * we need to contsruct a new tuple based on the aggregate operation
 * SUM,Min....GroupBy...
 *
 */

public class AggrNode implements Iterator<Tuple> {

//    String: attribute name, Aggregator: sum, min ,max, etc,.
    Map<String,Aggregator> aggreFeilds = new TreeMap<>();
    Map<String,Column> rawColumns = new HashMap<>();
    Map<String,PrimitiveValue> columnValues = new HashMap<>();

    List<Column> colList = new ArrayList<>();
    ArrayList<String> keyList= new ArrayList<>();
    String key="";
    Iterator keyItr;
    Iterator<Tuple> childItr;
    Function aggrFn;
    ExpressionList expressionList;
    List<Column> groupByAttrs;


    /**
     * SELECT COUNT(FIRSTNAME),FIRSTSEASON FROM PLAYERS GROUP BY FIRSTSEASON;
     * @param projectAttrs
     * decide witch and how many aggregator needed
     * @param childItr
     * @param groupByColumns
     */

    public AggrNode(List<SelectExpressionItem> projectAttrs, List<Column> groupByColumns, Iterator<Tuple> childItr) {

        this.childItr=childItr;
        groupByAttrs = groupByColumns;

//        Phase 1: Initialize Aggregate Field
        for(SelectExpressionItem item:projectAttrs){

            if(item.getExpression() instanceof Function){

                Aggregator aggregator=null;
                this.aggrFn=(Function) item.getExpression();
                String aggrfnName = this.aggrFn.getName();//gBfn
                String alias = item.getAlias();
                this.expressionList=this.aggrFn.getParameters();//firstname

                //Initialize Column
                Column col = new Column();
                Table tb  = new Table();
                col.setTable(tb);

                if(aggrfnName.equals("COUNT")){
                    aggregator= new Count(this.expressionList,groupByColumns);
                    if(alias==null){
                        alias="COUNT";
                    }

                }

                if(aggrfnName.equals("MIN")){
                    aggregator= new Min(this.expressionList,groupByColumns);
                    if(alias==null){
                        alias="MIN";
                    }
                }

                if(aggrfnName.equals("MAX")){
                    aggregator= new Max(this.expressionList,groupByColumns);
                    if(alias==null){
                        alias="MAX";
                    }
                }

                if(aggrfnName.equals("SUM")){
                    aggregator = new Sum(this.expressionList,groupByColumns);
                    if(alias==null){
                        alias="SUM";
                    }
                }


                col.setColumnName(alias);
                this.aggreFeilds.put(alias,aggregator);
                this.colList.add(col);

            }else if(item.getExpression() instanceof Column ){

                String alias = item.getAlias();
                Aggregator aggregator= new GroupByColumn(item,groupByColumns);
                if(alias==null){
                    alias=item.getExpression().toString();
                }
                //Initialize Column
                Column col = new Column();
                col.setColumnName(alias);
                Table tb  = new Table();
                col.setTable(tb);
                this.aggreFeilds.put(alias,aggregator);
                this.colList.add(col);

            }
        }

//        Phase 2: Read Tuples and set value to each aggregator

        while(this.childItr.hasNext()){

            Iterator fieldsItr = this.aggreFeilds.entrySet().iterator();
            Tuple tp = this.childItr.next();;
            while(fieldsItr.hasNext()){

                Map.Entry pair = (Map.Entry)fieldsItr.next();
                Aggregator aggregator= (Aggregator) pair.getValue();
                aggregator.accumulate(tp);

            }

//           Set keys
            String key=""; // key = "" if no groupBy, key = "filed1"+"field2"+... if has groupBy
            if(groupByAttrs!=null){

                for(Column cols:this.groupByAttrs){
                    String aggrByName = cols.getColumnName();

                    key+=tp.getColumnValue(aggrByName);

                }
                if(!keyList.contains(key)){
                    keyList.add(key);
                }
            }else {
                if(!keyList.contains(key)){
                    keyList.add(key);
                }
            }
        }

        keyItr=keyList.iterator();
    }

    @Override
    public boolean hasNext() {
        if(keyItr.hasNext()){
            key=(String)keyItr.next();
            return true;
        } else{
            return false;
        }
    }

    @Override
    public Tuple next() {

        Tuple tp = new Tuple();

        Iterator fieldsItr = this.aggreFeilds.entrySet().iterator();

        while(fieldsItr.hasNext()){
            Map.Entry pair = (Map.Entry)fieldsItr.next();
            Aggregator aggregator= (Aggregator) pair.getValue();
            PrimitiveValue colValue=aggregator.getValue(key);
            columnValues.put((String)pair.getKey(),colValue);
        }

        tp.columnValues= this.columnValues;
        tp.columnList = this.colList;
        return tp;
    }

    @Override
    public void remove() {

    }

    @Override
    public void forEachRemaining(Consumer<? super Tuple> action) {

    }

}
