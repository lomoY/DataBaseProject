package dubstep.Aggregator;

import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import java.util.*;
import java.util.function.Consumer;

public class CountBackUp implements Iterable<Tuple>{
    HashMap<String,Tuple> newTpList = new HashMap<>();

    List<String> groupbyName = new LinkedList<>();
    LongValue amount = new LongValue(0);
    List<ColumnDefinition> coldefinitions = new ArrayList<>();
    List<Column> groupByAttrs;//GroupByAttrs:FIRSTSEASON,LASTSEASON

    /**
     * count,int
     * groubyName, string
     *
     */

//    ExpressionList expressionList;// FIRSTNAME
    List<Expression> expressionList;

    public CountBackUp(ExpressionList expressionList, List<Column> groupByColumns) {

        this.expressionList=expressionList.getExpressions();
        this.groupByAttrs=groupByColumns;
        ArrayList<PrimitiveValue> row= new ArrayList<>(groupByAttrs.size());

        ColumnDefinition count = new ColumnDefinition();
        count.setColumnName("COUNT");
        ColDataType countDataType = new ColDataType();
        countDataType.setDataType("int");
        count.setColDataType(countDataType);
        coldefinitions.add(count);
    }

    public void accumulate(Tuple tp) {

        int isNewCount = groupByAttrs.size();
        int existIndex=0;//这个可能要改
        /**
         * expressionList 所要count的东西
         * */
        if(newTpList.size()==0){
            Tuple newTp = new Tuple();
            Column COUNT = new Column(null,"COUNT");
            newTp.setColumn(COUNT,new LongValue(1));
            String key="";
            for(Expression expression:groupByAttrs){
                String attName = expression.toString();
                PrimitiveValue value=tp.getColumnValue(attName);
                key=key+value.toRawString();
                Column col = tp.getRawColumn(attName);
                newTp.setColumn(col,value);
                ColumnDefinition definition = tp.getColdefinition(attName);
                this.coldefinitions.add(definition);
            }

            newTp.setColdefinition(this.coldefinitions);
            newTpList.put(key,newTp);
        }else{

            String key="";

            int isMatch = groupByAttrs.size();
            for(Expression expression:groupByAttrs) {
                String attName = expression.toString();
                PrimitiveValue value = tp.getColumnValue(attName);
                key = key + value.toRawString();
            }


            if(!newTpList.containsKey(key)){//表示不存在
                Tuple newTp = new Tuple();
                Column COUNT = new Column(null,"COUNT");
                newTp.setColumn(COUNT,new LongValue(1));

                for(Expression expression:groupByAttrs){
                    String attName = expression.toString();
                    PrimitiveValue value=tp.getColumnValue(attName);
                    Column col = tp.getRawColumn(attName);
                    newTp.setColumn(col,value);
                }
                newTp.setColdefinition(this.coldefinitions);
                newTpList.put(key,newTp);
            }else {
                Tuple existTp=newTpList.get(key);
                LongValue oldValue = (LongValue) existTp.getColumnValue("COUNT");
                LongValue newValue = new LongValue(oldValue.getValue()+1);
                existTp.setColumnValue("COUNT",newValue);
            }
        }

    }


    private class Itr implements Iterator<Tuple>{
        Iterator it=newTpList.entrySet().iterator();
        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer<? super Tuple> action) {

        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Tuple next() {
            Map.Entry element = (Map.Entry)it.next();
            Tuple tp = (Tuple) element.getValue();
            return tp;
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
        return new Itr();
    }
}


/**
 *
 * SELECT COUNT(FIRSTNAME) FROM PLAYERS;
 *
 * SELECT COUNT(FIRSTNAME),FIRSTSEASON FROM PLAYERS;
 * 会取第一个FIRSTSEASON的值，并对应一个COUNT（*）
 *
 * SELECT COUNT(FIRSTNAME) FROM PLAYERS GROUP BY FIRSTSEASON;
 * 会正确显示count结果，但是不会有对应的FIRSTSEASON
 *
 * SELECT COUNT(FIRSTNAME),FIRSTSEASON FROM PLAYERS GROUP BY FIRSTSEASON;
 *
 *
 * 1.新的attribute是groupby的属性的唯一值，和Count
 * 2.新groupby的值出现的时候，count的值+1
 *
 *
 * 但是往count里传啥呢。。。应该是把tuple一个一个传进去
 *
 *
 *
 *
 * 如何将函数构造成iterator的样子，然后把函数中的某个collection return出去
 *
 *
 *
 *
 */


