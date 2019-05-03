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
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

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
          this.TableFile = Paths.get("data",TableName+".csv").toFile();
//        this.TableFile = Paths.get("G:/UB/Spring'19/DB/cse562-master/DataBaseProject_1/checkpoint1/test/NBA_Examples", "PLAYERS.csv").toFile();
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


//        if (indices != null) {
//            for (Index i : indices) {
//                for (String colname : i.getColumnsNames()) {
//                    indexedColumns.add(colname);
//                }
//            }
//        }


        //          Assigning col positions
        int position = 0;
        for (ColumnDefinition c : createTable.getColumnDefinitions()) {
            colPos.put(c.getColumnName(), position);
            colDef.put(c.getColumnName(), c.getColDataType());
            indexedColumns.add(c.getColumnName());
            position++;
        }
//        System.out.println("Indexed Columns " + indexedColumns);
        try {
            br = new BufferedReader(new FileReader(IndexNode.this.TableFile));
            raf=  new RandomAccessFile(IndexNode.this.TableFile, "rw");
            PrimitiveValue tempString;
            String[] rows;
//          index creation
            int pos;
            PrimitiveValue key_val= null;
            for (String c : indexedColumns) {
                raf=  new RandomAccessFile(IndexNode.this.TableFile, "rw");
                TreeMap<PrimitiveValue, ArrayList<Long>> temp = new TreeMap<>(new KeyComparator());
                while (raf.readLine() != null) {
                    long rowIndex = raf.getFilePointer();
                    rows = raf.readLine().split("\\|");
                    pos = colPos.get(c);
//                    PrimitiveValue key_val= new StringValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("string"))
                        key_val= new StringValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("double"))
                        key_val= new DoubleValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("int"))
                        key_val= new LongValue(rows[pos]);
                    if(colDef.get(c).getDataType().equalsIgnoreCase("date"))
                        key_val= new DateValue(rows[pos]);

                    tempString = key_val;
//                    System.out.println(tempString);

                    if (temp.containsKey(tempString)) {
                        ArrayList<Long> rowsList = temp.get(tempString);
                        rowsList.add(rowIndex);
//                        System.out.println(rowsList);
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

    private class KeyComparator implements Comparator{
        public KeyComparator() {
        }

        @Override
        public Comparator reversed() {
            return null;
        }

        @Override
        public Comparator thenComparing(Comparator other) {
            return null;
        }

        @Override
        public Comparator thenComparing(Function keyExtractor, Comparator keyComparator) {
            return null;
        }

        @Override
        public Comparator thenComparing(Function keyExtractor) {
            return null;
        }

        @Override
        public Comparator thenComparingInt(ToIntFunction keyExtractor) {
            return null;
        }

        @Override
        public Comparator thenComparingLong(ToLongFunction keyExtractor) {
            return null;
        }

        @Override
        public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
            return null;
        }

        @Override
        public int compare(Object o1, Object o2) {
            if(o1 instanceof StringValue ||o2 instanceof StringValue || o1 instanceof DateValue )
            {
                if(o1.toString().equals(o2.toString())){
                    return 0;
                }else {
                    return -1;
                }
            }
            if(o1 instanceof LongValue)
            {
                if(((LongValue) o1).toLong() == ((LongValue) o2).toLong()){
                    return 0;
                }else {
                    return -1;
                }
            }
            if(o1 instanceof DoubleValue) {
                if (((DoubleValue) o1).toDouble() == ((DoubleValue) o2).toDouble()) {
                    return 0;
                } else {
                    return -1;
                }
            }
            return 0;
        }

    }

}