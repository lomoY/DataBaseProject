package dubstep.TreeNode;


import dubstep.Manager.TableManager;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.Index;

import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.function.Consumer;



public class IndexScan extends TreeNode {

    String tableName;
    String aliasValue;
    File TableFile;
    Table tableObj;
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
        this.tableObj = TableManager.getTable(this.tableName).getTableObj();
        this.colName= colName;
        this.lowerBound= lowerBound;
        this.upperBound= upperBound;
        this.softLowerBound=softLowerBound;
        this.softUpperBound = softUpperBound;
        this.equalsBound= equals;
        IndexNode id = TableManager.getIndex(tableName);
        TreeMap<String, TreeMap<PrimitiveValue, ArrayList<Long>>> indexCol=id.indexes;
        this.index= indexCol.get(colName);
//        System.out.println(isIndex(this.index));
        this.TableFile= id.TableFile;

    }

    public boolean isIndex(TreeMap<PrimitiveValue, ArrayList<Long>> index)
    {
        if (index ==null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public Set<String> getIndexes(TreeMap<String,TreeMap<PrimitiveValue, ArrayList<Long>>> tableindex){
        IndexNode id = TableManager.getIndex(IndexScan.this.tableName);
        TreeMap<String, TreeMap<PrimitiveValue, ArrayList<Long>>> indexCol=id.indexes;
        return indexCol.keySet();
    }

    private class Itr implements Iterator<Tuple> {
        Iterator<Tuple> lfItr;
        //        Tuple lftuple;
        String lftuple;
        BufferedReader br;
        RandomAccessFile raf;
        Iterator rowPosItr;

        public Itr() {

            Schema schema = TableManager.getSchema(IndexScan.this.tableName);

            columnDefinitions = schema.getColumnDefinitions();
            SortedMap <PrimitiveValue, ArrayList<Long>> sortedMap = new TreeMap<>();
            //>
            if(upperBound==null && !softLowerBound && lowerBound!=null)
            {
                sortedMap= IndexScan.this.index.tailMap(lowerBound);
            }
            //>=
            else if (upperBound== null && softLowerBound && lowerBound!=null)
            {
                sortedMap= IndexScan.this.index.tailMap(lowerBound, true);
            }
            //<
            else if (lowerBound== null && !softUpperBound && upperBound!=null)
            {
                sortedMap= IndexScan.this.index.headMap(upperBound);
            }
            else if (lowerBound == null && softUpperBound && upperBound!=null)
            {
                sortedMap= IndexScan.this.index.headMap(upperBound, true);
            }
            else  if (softUpperBound && softLowerBound && upperBound !=null && lowerBound != null)
            {
                sortedMap= IndexScan.this.index.subMap(lowerBound, true, upperBound, true);
            }
            else if (softUpperBound && !softLowerBound && upperBound !=null && lowerBound != null)
            {
                sortedMap= IndexScan.this.index.subMap(lowerBound, false, upperBound, true);
            }
            else if (!softUpperBound && softLowerBound && upperBound !=null && lowerBound != null)
            {
                sortedMap= IndexScan.this.index.subMap(lowerBound, true, upperBound, false);
            }
            else if (!softUpperBound && !softLowerBound && upperBound !=null && lowerBound != null)
            {
                sortedMap= IndexScan.this.index.subMap(lowerBound, false, upperBound, false);
            }
            else if(equalsBound)
            {
                sortedMap= IndexScan.this.index.subMap(lowerBound, true, lowerBound, true);
            }
            System.out.println( sortedMap.entrySet());
            try
            {
                raf=  new RandomAccessFile(IndexScan.this.TableFile, "r");
                for(Map.Entry<PrimitiveValue, ArrayList<Long>> node: sortedMap.entrySet())
                {
                    ArrayList<Long> rowPos = node.getValue();
                    this.rowPosItr=rowPos.iterator();
                    for(Long i : rowPos){
                        String row= returnRow(i);
                        Tuple returntp= new Tuple(row);
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        public String returnRow(Long i) {

//            Tuple returntp= new Tuple();
            String row="";

            try {
                raf.seek(i);
                row= raf.readLine();
                return row;
            }
            catch (IOException e) {}
            return row;
        }

        @Override
        public Tuple next() {

            String row= returnRow((Long)rowPosItr.next());
            try {
                Tuple tp = new Tuple(columnDefinitions);
                String[] columnValues = row.split("\\|");

                for(int i =0;i<columnValues.length;i++){

                    Table tb = IndexScan.this.tableObj;
                    if(IndexScan.this.aliasValue!=null){
                        tb.setName(aliasValue);
                    }

                    Column column = new Column(IndexScan.this.tableObj,columnDefinitions.get(i).getColumnName().toString());
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

            if(rowPosItr!=null&&rowPosItr.hasNext()){
                return true;
            }else {
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
        return new Itr();
    }
}
