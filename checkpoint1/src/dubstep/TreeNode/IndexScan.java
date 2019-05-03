package dubstep.TreeNode;


import dubstep.Manager.TableManager;
import net.sf.jsqlparser.expression.PrimitiveValue;

import java.util.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.function.Consumer;


public class IndexScan extends TreeNode {
    String tableName;
    String colName;
    PrimitiveValue lowerBound;
    PrimitiveValue upperBound;
    File TableFile;
    TreeMap<PrimitiveValue, ArrayList<Long>> index;

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
        Tuple lftuple;
        BufferedReader br;
        RandomAccessFile raf;
        public Itr() {
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

        public Tuple returnRow(Long i)
        {
            Tuple returntp= new Tuple();
            try
            {
                raf.seek(i);
                String row= raf.readLine();
                //Zhenyu PLEASE LOOK INTO THIS!!
                //convert this string to tuple and return;
                returntp= new Tuple(row);
                return returntp;
            }

//            catch (FileNotFoundException e) {}
            catch (IOException e) {}//here is only one try, but why two catch?
            return returntp;
        }
        @Override
        public Tuple next() {
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