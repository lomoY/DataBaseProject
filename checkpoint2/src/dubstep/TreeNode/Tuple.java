package dubstep.TreeNode;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;
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
    public List<Column> columnList = new ArrayList<>();

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
//        Iterator it = rawColumns.entrySet().iterator();
//        while(it.hasNext()){
//            Map.Entry pair = (Map.Entry)it.next();
//            String name = (String)pair.getKey();
//            Column cl = (Column)pair.getValue();
//            String columnName = cl.getColumnName();
//            String columnWholename= cl.getWholeColumnName();
//            String columnTb = cl.getTable().toString();
//            out.writeObject(name);
//            out.writeObject(columnName);
//            out.writeObject(columnTb);
//
//        }

//        for(ColumnDefinition def:coldefinitions){
//            String name = (String)def.getColumnName();
//            ColDataType type = def.getColDataType();
//            out.writeObject(name);
//            out.writeObject(type.toString());
//        }
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException{

        in.defaultReadObject();
//        for(int i=0;i<this.columnValues.size();i++) {
//            String name = (String) in.readObject();
//            String columnName = (String) in.readObject();
//            String clTb = (String)in.readObject();
//            Table tb = new Table(clTb);
//            Column cl = new Column();
//            cl.setColumnName(columnName);
//            cl.setTable(tb);
//            if (rawColumns == null) {
//                this.rawColumns = new HashMap<>();
//            }
//            this.rawColumns.put(name, cl);
//        }
//
//        for(int i=0;i<this.columnValues.size();i++) {
//            if(coldefinitions==null){
//                coldefinitions = new ArrayList<>();
//            }
//
//            ColumnDefinition def = new ColumnDefinition();
//            def.setColumnName((String)in.readObject());
//            String typeString = (String)in.readObject();
//            ColDataType cltype = new ColDataType();
//            cltype.setDataType(typeString);
//            def.setColDataType(cltype);
//            coldefinitions.add(def);
//
//        }

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
