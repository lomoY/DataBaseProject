package dubstep.Aggregator;

import dubstep.TreeNode.Tuple;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Lomo Yang
 */

public class GroupByIterator implements Iterator<Tuple> {
    Iterator<Tuple> itr;
    Function aggrFn;
    ExpressionList expressionList;
    Count count;
    Iterator aggreItr;
    List<SelectExpressionItem> groupByAttrs;
    /**
     * SELECT COUNT(FIRSTNAME),FIRSTSEASON FROM PLAYERS GROUP BY FIRSTSEASON;
     * 这里有两个问题：
     *      1. 需要统计FIRSTNAME在同一个FIRSTSEASON中的：属性，数量
     *      2. 产生的结果需要以一连串的tuple的形式返回出去
     *
     *      这里有很多事情要做
     *
     * @param projectAttrs
     * @param itr
     * @param groupByColumns
     */

    public GroupByIterator(List<SelectExpressionItem> projectAttrs, List<Column> groupByColumns,Iterator<Tuple> itr) {

        this.itr=itr;
//        groupby的东西
        groupByAttrs = projectAttrs;

//
        for(SelectExpressionItem item:projectAttrs){
            if(item.getExpression() instanceof Function){
                this.aggrFn=(Function) item.getExpression();
                String aggrfnName = this.aggrFn.getName();//gBfn
                this.expressionList=this.aggrFn.getParameters();//firstname

                if(aggrfnName.equals("COUNT")){
                    count= new Count(this.expressionList,groupByColumns);
                }
            }
        }

        while(this.itr.hasNext()) {
            Tuple oldTp = itr.next();
            this.count.accumulate(oldTp);
        }
        aggreItr=this.count.iterator();
    }

    @Override
    public boolean hasNext() {
        if(aggreItr.hasNext()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Tuple next() {
        /**
         * 读一个tuple，查看firstname里的值
         * 如果firstname是第一次出现，比如alex，那就增加一个alex的counter
         * 如果是第二次出现，就在原来的基础上+1
         */
        Tuple tp =(Tuple) aggreItr.next();
        return tp;
    }

    @Override
    public void remove() {

    }

    @Override
    public void forEachRemaining(Consumer<? super Tuple> action) {

    }

}
