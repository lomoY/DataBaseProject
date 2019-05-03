package dubstep.TreeNode;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.PrimitiveType;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lomo
 * Initialize Tuple in TableNode, and return it to the parent node
 */

public class Tuple implements Serializable {

    public Map<String,PrimitiveValue> columnValues = new HashMap<>();//only for print out
   transient public List<Column> columnList = new ArrayList<>();

    public Tuple(String row){

    }
    public Tuple(){ }

    public Tuple(List<ColumnDefinition> columnDefinitions){}

    public PrimitiveValue getColumnValue(String _colName) {

        String key="";

        for(Column column:this.columnList){

            if(_colName.contains(".")){
                if(_colName.equals(column.getWholeColumnName())){
                    key = _colName;
                }
            }else{

                String colName =column.getWholeColumnName();

                Pattern regex = Pattern.compile("\\.(\\S+)");
                Matcher match = regex.matcher(colName);
                if (match.find()){
                    colName = match.group(1);
                }
                if(_colName.equals(colName)){
                    key=column.getWholeColumnName();
                }
            }

        }

        return columnValues.get(key);
    }

    public void setColumnValue(String columnName,PrimitiveValue value) {

        columnValues.put(columnName,value);
    }

    public void setColumn(Column column, PrimitiveValue columnValue) {

        this.columnValues.put(column.getWholeColumnName(),columnValue);
        this.columnList.add(column);
    }

    public void setAll(Map<String,String> alias, Map<String,PrimitiveValue> _columnValues, List<ColumnDefinition> _coldefinitions, List<Column> _columnList){

        Map<String,PrimitiveValue> tempValList = new HashMap<>();

        Iterator _valItr = _columnValues.entrySet().iterator();


        while(_valItr.hasNext()){

            Map.Entry _pair =(Map.Entry)_valItr.next();
            String _key = (String) _pair.getKey();
            String _keyAlias = alias.get(_key);
            boolean isFullName = false;
            boolean isMatch = false;
            if(_key.contains(".")){
                isFullName=true;
            }

            Iterator valItr = columnValues.entrySet().iterator();

            while(valItr.hasNext()){

                Map.Entry pair = (Map.Entry) valItr.next();
                String key =(String) pair.getKey();

                if(isFullName){//which means: ID not Players.ID

                    if(_key.equals(key)){

                        if(_keyAlias!=null){
                            int i = key.indexOf(".");
                            String tbName = key.substring(0,i)+".";
                            key=tbName+_keyAlias;
                        }
                        tempValList.put(key,(PrimitiveValue)_pair.getValue());
                        isMatch=true;
                        break;
                    }

                }else{

                    String colName =key;

                    Pattern regex = Pattern.compile("\\.(\\S+)");
                    Matcher match = regex.matcher(colName);
                    if (match.find()){
                        colName = match.group(1);
                    }

                    if(_key.equals(colName)){

                        if(_keyAlias!=null){
                            int i = key.indexOf(".");
                            String tbName = key.substring(0,i)+".";
                            key=tbName+_keyAlias;
                        }

                        tempValList.put(key,(PrimitiveValue)_pair.getValue());
                        isMatch=true;
                        break;
                    }
                }
            }

            if(!isMatch){
                if(_keyAlias!=null){
                    String tbName = this.columnList.get(0).getTable().getName();
                    _key=tbName+"."+_keyAlias;
                }
                tempValList.put(_key,(PrimitiveValue)_pair.getValue());
            }

        }

        this.columnValues=tempValList;


//        Update ColumnList
        List<Column> tempColList = new ArrayList<>();

        for(Column _column:_columnList){

            String _keyAlias = alias.get(_column.getWholeColumnName());
            boolean isMatch = false;
            for(Column column:this.columnList){

                if(_column.getTable().getName()==null){//which means: ID not Players.ID

                    if(_column.getColumnName().equals(column.getColumnName())){

                        if(_keyAlias!=null){
                            column.setColumnName(_keyAlias);
                        }

                        tempColList.add(column);
                        isMatch=true;
                        break;
                    }
                }else{

                    if(_column.getWholeColumnName().equals(column.getWholeColumnName())){

                        if(_keyAlias!=null){
                            column.setColumnName(_keyAlias);
                        }

                        tempColList.add(column);
                        isMatch=true;
                        break;
                    }
                }
            }

            if(!isMatch){
                if(_keyAlias!=null){
                    String tbName=columnList.get(0).getTable().getName();
                    Table tb = new Table();
                    tb.setName(tbName);
                    _column.setColumnName(_keyAlias);
                    _column.setTable(tb);
                }
                tempColList.add(_column);
            }
        }

        this.columnList = tempColList;
    }

    /**
     * Customer Serialization
     * @param out
     * @throws IOException
     */

    private void writeObject(ObjectOutputStream out) throws IOException{

        out.defaultWriteObject();
        Iterator it = this.columnValues.entrySet().iterator();

        while(it.hasNext()){

            Map.Entry pair = (Map.Entry)it.next();
            String colWholeName = (String)pair.getKey();
            PrimitiveValue val = (PrimitiveValue) pair.getValue();
            out.writeObject(colWholeName);
            out.writeObject(val);

        }

        for(Column col:columnList){

            String name = col.getColumnName();
            String tableName = col.getTable().getName();
            out.writeObject(name);
            out.writeObject(tableName);

        }
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException{

        in.defaultReadObject();
        for(int i=0;i<this.columnValues.size();i++) {

            String  colWholeName= (String) in.readObject();
            PrimitiveValue val = (PrimitiveValue)in.readObject();
            this.columnValues.put(colWholeName,val);

        }

        this.columnList = new ArrayList<>();// todo why this.columnList = null?
        for(int i=0;i<this.columnValues.size();i++) {

            String colName = (String )in.readObject();
            String tbName = (String )in.readObject();
            Column col = new Column();
            Table tb = new Table();
            col.setColumnName(colName);
            tb.setName(tbName);
            col.setTable(tb);
            this.columnList.add(col);

        }
    }

    @Override
    public String toString() {

        String result="";

        for (int i = 0; i < this.columnList.size(); i++) {

            try {

                String wholeColumnName = columnList.get(i).getWholeColumnName();
                PrimitiveValue columnValue = this.columnValues.get(wholeColumnName);

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

            if (i != this.columnList.size() - 1){
                result += "|";
            }
        }

        return result;
    }
}
