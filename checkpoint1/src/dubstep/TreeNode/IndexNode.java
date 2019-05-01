package dubstep.TreeNode;

import dubstep.Manager.EvaluatorManager;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.PrimitiveType;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import net.sf.jsqlparser.statement.create.table.Index;

/**
 * @author Nhrkr
 */

public class IndexNode {

    TreeMap<String, TreeMap<PrimitiveValue, ArrayList<Long>>> indexes = new TreeMap<>();
    File TableFile;
    String TableName;

    public IndexNode(CreateTable createTable) {

        this.TableName = createTable.getTable().getName();
//          this.TableFile = Paths.get("data",TableName+".csv").toFile();
//        this.TableFile = Paths.get("G:/UB/Spring'19/DB/cse562-master/DataBaseProject_1/checkpoint1/test/NBA_Examples", "PLAYERS.csv").toFile();
        this.TableFile=Paths.get("data",TableName+".csv").toFile();
        this.indexes = getIndex(createTable);

    }

    private TreeMap<String, TreeMap<PrimitiveValue, ArrayList<Long>>> getIndex(CreateTable createTable) {
        TreeMap<String, TreeMap<PrimitiveValue, ArrayList<Long>>> indexes = new TreeMap<>();
        BufferedReader br;
        RandomAccessFile raf;
        Map<String, Integer> colPos = new HashMap<>();
        Map<String, ColDataType> colDef= new HashMap<>();
        List<Index> indices = createTable.getIndexes();
        List<String> indexedColumns = new ArrayList<>();


        if (indices != null) {
            for (Index i : indices) {
                for (String colname : i.getColumnsNames()) {
                    indexedColumns.add(colname);
                }
            }
        }
//        System.out.println("Indexed Columns " + indexedColumns);

        //          Assigning col positions
        int position = 0;
        for (ColumnDefinition c : createTable.getColumnDefinitions()) {
            colPos.put(c.getColumnName(), position);
            colDef.put(c.getColumnName(), c.getColDataType());
            position++;
        }

        try {
            br = new BufferedReader(new FileReader(IndexNode.this.TableFile));
            raf=  new RandomAccessFile(IndexNode.this.TableFile, "rw");
            PrimitiveValue tempString;
            String[] rows;
//          index creation
            int pos;
            PrimitiveValue key_val= null;
            for (String c : indexedColumns) {
                TreeMap<PrimitiveValue, ArrayList<Long>> temp = new TreeMap<>();
                while (raf.readLine() != null) {

                    long rowIndex = raf.getFilePointer();
                    rows = br.readLine().split("\\|");
                    pos = colPos.get(c);
//                    PrimitiveValue key_val= new StringValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("string"))
                        key_val= new StringValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("double"))
                        key_val= new DoubleValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("int"))
                        key_val= new LongValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("date"))
                        key_val= new TimeValue(rows[pos]);

                    tempString = key_val;
                    System.out.println(tempString);
                    if (temp.containsKey(tempString)) {
                        ArrayList<Long> rowsList = temp.get(tempString);
                        rowsList.add(rowIndex);
                        System.out.println(rowsList);
                    } else {
                        ArrayList<Long> tempVal = new ArrayList<>();
                        tempVal.add(rowIndex);
                        temp.put(tempString, tempVal);

                    }
//                    rowNum++;
                }
                indexes.put(c.toUpperCase(), temp);
                temp = null;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e)
        {e.printStackTrace();}
        return indexes;
    }

}





//
//
//    private class Itr implements Iterator<Tuple> {
//        public Itr() {
//
//        }
//        @Override
//        public Tuple next() {
//            Tuple tp = new Tuple();
//            return tp;
//        }
//        @Override
//        public boolean hasNext() {
//            return true;
//        }
//        @Override
//        public void remove() {
//        }
//        @Override
//        public void forEachRemaining(Consumer action) {}
//
//    }
//}