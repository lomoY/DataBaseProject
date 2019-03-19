package dubstep.TreeNode;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.*;

/**
 * @author Lomo
 *
 * 我们需要Return出去的tuple
 */

public class Tuple {
    Map<String,Column> rawColumns = new HashMap<>();//用来存储原本模式的column
    Map<String,PrimitiveValue> columnValues = new HashMap<>();//only for print out
//    Map<String,ColumnDefinition> coldefinitions=new HashMap<>();
    List<ColumnDefinition> coldefinitions = new ArrayList<>();

    public Tuple(){

    }
    public Tuple(List<ColumnDefinition> columnDefinitions){
        for(ColumnDefinition columnDefinition:columnDefinitions){
//            coldefinitions.put(columnDefinition.getColumnName(),columnDefinition);
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


    public void upDateColumn(List<SelectItem> selectItems){

        Map<String,Column> tempColmns = new HashMap<>();
        Map<String,PrimitiveValue> tempColumnValues = new HashMap<>();
//        Map<String,ColumnDefinition> tempColdefinitions=new TreeMap<>();
        List tempColdefinitions = new ArrayList();

        for(SelectItem selectItem:selectItems){
            String attname = selectItem.toString();
            tempColmns.put(attname,this.rawColumns.get(attname));
            tempColumnValues.put(attname,this.columnValues.get(attname));
//            tempColdefinitions.put(attname,this.coldefinitions.get(attname));
            tempColdefinitions.add(this.getColdefinition(attname));
        }

        this.rawColumns=tempColmns;
        this.columnValues=tempColumnValues;
        this.coldefinitions=tempColdefinitions;
    }

    public void setColumn(Column column, PrimitiveValue columnValue) {
        this.rawColumns.put(column.getColumnName(),column);
        this.columnValues.put(column.getColumnName(),columnValue);
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
