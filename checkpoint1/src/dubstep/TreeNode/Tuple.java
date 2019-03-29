package dubstep.TreeNode;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lomo
 * Tuple的初始化是在TableNode里面完成的
 * 我们需要Return出去的tuple
 */

public class Tuple {
    Map<String,Column> rawColumns = new HashMap<>();//用来存储原本模式的column
    Map<String,PrimitiveValue> columnValues = new HashMap<>();//only for print out
    List<ColumnDefinition> coldefinitions = new ArrayList<>();

    public Tuple(){

    }

    public Tuple(List<ColumnDefinition> columnDefinitions){
        for(ColumnDefinition columnDefinition:columnDefinitions){
            coldefinitions.add(columnDefinition);
        }

    }

    public Column getRawColumn(String columnName){
        return rawColumns.get(columnName);
    }


    public PrimitiveValue getColumnValue(String columnName) {
        return columnValues.get(columnName);
    }


    public ColumnDefinition getColdefinition(String columnName) {
            ColumnDefinition targetColumn = new ColumnDefinition();
            for(ColumnDefinition columnDefinition:this.coldefinitions){
                if(columnDefinition.getColumnName().equals(columnName)){
                    targetColumn=columnDefinition;
                    break;
                }
            }
        return targetColumn;
    }

    public void setColumnValue(String columnName,PrimitiveValue value) {

        columnValues.put(columnName,value);
    }

    public void setColumn(Column column, PrimitiveValue columnValue) {
        this.rawColumns.put(column.getColumnName(),column);
        this.columnValues.put(column.getColumnName(),columnValue);
    }

    public void setColdefinition(List<ColumnDefinition> _coldefinitions){
        this.coldefinitions=_coldefinitions;
    }

    /**
     * Update Schema。 是在tuple里update呢，还是在TableNode里update
     * @param selectItems
     * arg List<SelectExpressionItem>
     */

    public void upDateColumn(List<SelectExpressionItem> selectItems){

        Map<String,Column> tempColmns = new HashMap<>();
        Map<String,PrimitiveValue> tempColumnValues = new HashMap<>();
        List tempColdefinitions = new ArrayList();
        String attName;
        String newName;
        for(SelectExpressionItem selectItem:selectItems){


            if(selectItem.getAlias()==null){
                attName = selectItem.getExpression().toString();//p1.firstname 无法实现getcolumnname

                Pattern regex = Pattern.compile("\\.(\\S+)");
                Matcher match = regex.matcher(selectItem.getExpression().toString());
                if (match.find()){
                    attName = match.group(1);
                }

                tempColmns.put(attName,this.rawColumns.get(attName));
                tempColumnValues.put(attName,this.columnValues.get(attName));//这一步把value也一起更新了
                tempColdefinitions.add(this.getColdefinition(attName));

            }else{

                if(selectItem.getAlias()!=this.getColdefinition(selectItem.getAlias()).getColumnName()){//first time

                    attName = selectItem.getExpression().toString();
                    newName = selectItem.getAlias();

                }else{
                    attName =selectItem.getAlias();
                    newName = selectItem.getAlias();
                }

                Column tempColumn = this.rawColumns.get(attName);
                tempColumn.setColumnName(newName);

                tempColmns.put(newName,tempColumn);
                tempColumnValues.put(newName,this.columnValues.get(attName));
                ColumnDefinition tempDefinition = this.getColdefinition(attName);
                tempDefinition.setColumnName(newName);
                tempColdefinitions.add(tempDefinition);

            }
        }

        this.rawColumns=tempColmns;
        this.columnValues=tempColumnValues;
        this.coldefinitions=tempColdefinitions;
    }

    public void rename(List<String> rename){

    }

    @Override
    public String toString() {

        String result="";

        for (int i = 0; i < this.coldefinitions.size(); i++) {

            try {
                String columnName = coldefinitions.get(i).getColumnName();
                PrimitiveValue columnValue = this.columnValues.get(columnName);

                if (columnValue == null) {
                    // Do nothing
                } else if (columnValue instanceof LongValue) {
                    result += Long.toString(columnValue.toLong());
                } else if (columnValue instanceof DoubleValue) {
                    result += Double.toString(columnValue.toDouble());
                } else if (columnValue instanceof StringValue) {
                    String stringValue = columnValue.toString();
                    result += stringValue.substring(1, stringValue.length() - 1);
                } else {
                    result += columnValue.toString();
                }
            }catch (PrimitiveValue.InvalidPrimitive e){
                e.printStackTrace();
            }

            if (i != coldefinitions.size() - 1){
                result += "|";
            }
        }

        return result;
    }
}
