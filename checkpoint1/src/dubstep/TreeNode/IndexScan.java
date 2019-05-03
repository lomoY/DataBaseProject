package dubstep.TreeNode;


import dubstep.Manager.TableManager;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.Index;

import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.function.Consumer;


public class IndexScan extends TreeNode {

    String tableName;
    String aliasValue;
    File TableFile;
    Table TableObj;
    String colName;
    PrimitiveValue lowerBound;
    PrimitiveValue upperBound;
    boolean equalsBound= false;
    boolean softLowerBound = false;
    boolean softUpperBound = false;
    TreeMap<PrimitiveValue, ArrayList<Long>> index;
    List<ColumnDefinition> columnDefinitions;

    public IndexScan(String tableName, String colName, PrimitiveValue lowerBound, PrimitiveValue upperBound, boolean equals, boolean softLowerBound, boolean softUpperBound) {

        this.tableName= tableName;
        this.colName= colName;
        this.lowerBound= lowerBound;
        this.upperBound= upperBound;
        this.softLowerBound=softLowerBound;
        this.softUpperBound = softUpperBound;
        this.equalsBound= equals;
        IndexNode id = TableManager.getIndex(tableName);
        TreeMap<String, TreeMap<PrimitiveValue, ArrayList<Long>>> indexCol=id.indexes;
        this.index= indexCol.get(colName);
        this.TableFile= id.TableFile;

    }

    private class Itr implements Iterator<Tuple> {
        Iterator<Tuple> lfItr;
//        Tuple lftuple;
        String lftuple;
        BufferedReader br;
        RandomAccessFile raf;

        public Itr() {

            Schema schema = TableManager.getSchema(IndexScan.this.tableName);

            columnDefinitions = schema.getColumnDefinitions();


            try
            {
                raf=  new RandomAccessFile(IndexScan.this.TableFile, "rw");

                for(Map.Entry<PrimitiveValue, ArrayList<Long>> node: IndexScan.this.index.entrySet())
                {
                    //equal condition like age=10
                    boolean getrow = false;
                    if (IndexScan.this.equalsBound) {
                        if (node.getKey() == lowerBound) {
                            getrow=true;
                        }
                    }
                    //age<10 or age<=10
                    else if(IndexScan.this.lowerBound== null)
                    {
                        if(IndexScan.this.softUpperBound)
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                if(node.getKey().toString().compareToIgnoreCase(upperBound.toString())==-1 ||node.getKey().toString().compareToIgnoreCase(upperBound.toString())==0)
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()<=upperBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()<=upperBound.toDouble()){getrow=true;}
                            }
                        }
                        else //tight bound
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                if(node.getKey().toString().compareToIgnoreCase(upperBound.toString())==-1)
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()<upperBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()<upperBound.toDouble()){getrow=true;}
                            }
                        }
                    }
                    // age > 10 or age>=10
                    else if(IndexScan.this.upperBound== null)
                    {
                        if(IndexScan.this.softLowerBound)
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                if(node.getKey().toString().compareToIgnoreCase(lowerBound.toString())==1 ||node.getKey().toString().compareToIgnoreCase(lowerBound.toString())==0)
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()>=lowerBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()>=lowerBound.toDouble()){getrow=true;}
                            }
                        }
                        else //tight bound
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                if(node.getKey().toString().compareToIgnoreCase(lowerBound.toString())==1)
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()>lowerBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()>lowerBound.toDouble()){getrow=true;}
                            }
                        }
                    }
                    // age<10 and age > 5 or age <=10 and age >=5
                    else
                    {
                        // <= and >=
                        if(IndexScan.this.softLowerBound && IndexScan.this.softUpperBound )
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                int string_com_lower= node.getKey().toString().compareToIgnoreCase(lowerBound.toString());
                                int string_com_upper= node.getKey().toString().compareToIgnoreCase(upperBound.toString());
                                if((string_com_lower==1 || string_com_lower ==0 )&& (string_com_upper==-1 || string_com_upper ==0) )
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()>=lowerBound.toLong() && node.getKey().toLong()<=upperBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()>=lowerBound.toDouble() && node.getKey().toDouble()<=upperBound.toDouble()){getrow=true;}
                            }
                        }
                        // <= and >
                        else if (IndexScan.this.softLowerBound && IndexScan.this.softUpperBound==false )
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                int string_com_lower= node.getKey().toString().compareToIgnoreCase(lowerBound.toString());
                                int string_com_upper= node.getKey().toString().compareToIgnoreCase(upperBound.toString());
                                if((string_com_lower==1 || string_com_lower ==0 )&& (string_com_upper==-1) )
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()>=lowerBound.toLong() && node.getKey().toLong()<upperBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()>=lowerBound.toDouble() && node.getKey().toDouble()<upperBound.toDouble()){getrow=true;}
                            }
                        }
                        else if(IndexScan.this.softLowerBound==false && IndexScan.this.softUpperBound )
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                int string_com_lower= node.getKey().toString().compareToIgnoreCase(lowerBound.toString());
                                int string_com_upper= node.getKey().toString().compareToIgnoreCase(upperBound.toString());
                                if((string_com_lower==1)&& (string_com_upper==-1 || string_com_upper ==0) )
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()>lowerBound.toLong() && node.getKey().toLong()<=upperBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()>lowerBound.toDouble() && node.getKey().toDouble()<=upperBound.toDouble()){getrow=true;}
                            }
                        }
                        else if (IndexScan.this.softLowerBound ==false && IndexScan.this.softUpperBound ==false )
                        {
                            if(node.getKey()instanceof StringValue ||node.getKey()instanceof DateValue )
                            {
                                int string_com_lower= node.getKey().toString().compareToIgnoreCase(lowerBound.toString());
                                int string_com_upper= node.getKey().toString().compareToIgnoreCase(upperBound.toString());
                                if((string_com_lower==1)&& (string_com_upper==-1) )
                                {
                                    getrow=true;
                                }
                            }
                            else if(node.getKey() instanceof LongValue)
                            {
                                if(node.getKey().toLong()>lowerBound.toLong() && node.getKey().toLong()<upperBound.toLong()){getrow=true;}
                            }
                            else if (node.getKey() instanceof DoubleValue)
                            {
                                if(node.getKey().toDouble()>lowerBound.toDouble() && node.getKey().toDouble()<upperBound.toDouble()){getrow=true;}
                            }
                        }
                    }
                    if (getrow==true)
                    {
                        ArrayList<Long> rowPos = node.getValue();
                        for(Long i : rowPos){
                            String row= returnRow(i);
                            Tuple returntp= new Tuple(row);
    //                        return returntp;
                        }
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (PrimitiveValue.InvalidPrimitive e){e.printStackTrace();}
        }

        public String returnRow(Long i) {

//            Tuple returntp= new Tuple();
            String row="";

            try {
                raf.seek(i);
                row= raf.readLine();
                return row;
                //Zhenyu PLEASE LOOK INTO THIS!!
                //convert this string to tuple and return;
//                returntp= new Tuple(row);
//                return returntp;
                }
                catch (IOException e) {}
            return row;
        }

        @Override
        public Tuple next() {

            try {

                Tuple tp = new Tuple(columnDefinitions);
                String[] columnValues = lftuple.split("\\|");

                for(int i =0;i<columnValues.length;i++){

                    Table tb = IndexScan.this.TableObj;
                    if(IndexScan.this.aliasValue!=null){
                        tb.setName(aliasValue);
                    }

                    Column column = new Column(IndexScan.this.TableObj,columnDefinitions.get(i).getColumnName().toString());
                    String colDataType = columnDefinitions.get(i).getColDataType().toString();
                    switch (colDataType) {
                        case "int":
                            tp.setColumn(column, new LongValue(Long.parseLong(columnValues[i])));
                            break;
                        case "decimal":
                            tp.setColumn(column, new DoubleValue(Double.parseDouble(columnValues[i])));
                            break;
                        case "date":
                            tp.setColumn(column, new DateValue(columnValues[i]));
                            break;
                        case "varchar":
                        case "char":
                        case "string":
                            tp.setColumn(column, new StringValue(columnValues[i]));
                            break;
                        default:
                            tp.setColumn(column, new NullValue());
                    }
                }
                return tp;

            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void remove() {}

        @Override
        public void forEachRemaining(Consumer action) {}

        @Override
        public boolean hasNext() {
            if (lfItr.hasNext()) {
                return true;
            } else {

                return false;
            }
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
        return new IndexScan.Itr();
    }
}