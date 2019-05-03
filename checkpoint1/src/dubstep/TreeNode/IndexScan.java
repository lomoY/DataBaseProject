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
    TreeMap<PrimitiveValue, ArrayList<Long>> index;
    List<ColumnDefinition> columnDefinitions;

    public IndexScan(String tableName, String colName, PrimitiveValue lowerBound, PrimitiveValue upperBound, boolean equals) {

        this.tableName= tableName;
        this.colName= colName;
        this.lowerBound= lowerBound;
        this.upperBound= upperBound;
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
            }
            catch (IOException e){
                e.printStackTrace();
            }
            for(Map.Entry<PrimitiveValue, ArrayList<Long>> node: IndexScan.this.index.entrySet())
            {
                //condition for key-- if key < lowerbound,
                PrimitiveValue key= node.getKey();
                ArrayList<Long> rowPos = node.getValue();
                for(Long i : rowPos)
                {
                    lftuple= returnRow(i);
                }

            }
        }

        public String returnRow(Long i) {

//            Tuple returntp= new Tuple();
            String row="";

            try {

                raf.seek(i);
                row= raf.readLine();
                //Zhenyu PLEASE LOOK INTO THIS!!
                //convert this string to tuple and return;
//                returntp= new Tuple(row);
//                return returntp;
                return row;
                }
                catch (IOException e) {}

//            return returntp;
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