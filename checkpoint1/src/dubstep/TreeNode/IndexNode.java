package dubstep.TreeNode;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.*;

import net.sf.jsqlparser.statement.create.table.Index;
/**
 * @author Nhrkr
 */

public class IndexNode {
    static TreeMap<String,TreeMap<String,ArrayList<Integer>>> indexes=new TreeMap<>();
    public IndexNode(CreateTable createTable, String tableName) {
        {
            File scanFile;
            Scanner scan;
            ArrayList<String[]> data = new ArrayList<>();
            Map<String, Integer> colPos=new HashMap<>();
//          scanFile = new File("test/" + tableName + ".csv");
            scanFile= new File ("G:/UB/Spring'19/DB/cse562-master/DataBaseProject_1/checkpoint1/test/NBA_Examples/PLAYERS.csv");
            try
            {
                List<Index> indices= createTable.getIndexes();
                List<String> indexedColumns = new ArrayList<>();
                Iterator<String[]> recordIter;
                String row ;

                scan = new Scanner(scanFile);
                scan.useDelimiter("|");
                //read table into var data.
                while (scan.hasNextLine() && ((row = scan.nextLine()) != null))
                    data.add(row.split("\\|"));
                scan.close();


                //make index on primary key first
                if(indices!=null) {
                    for (Index i : indices) {
                        if (i.getType().toString().equalsIgnoreCase("PRIMARY KEY")) {
                            for (String colname : i.getColumnsNames()) {
                                indexedColumns.add(colname);
                            }
                        }
                    }
                }
                System.out.println("Indexed Columns "+ indexedColumns);
                indices = null;

                //other indcies, also set the column position
                Integer position=0;
                for(ColumnDefinition c: createTable.getColumnDefinitions() ) {
                    colPos.put(c.getColumnName(),position);
                    position++;
                    if (c.getColumnSpecStrings() != null)
                        indexedColumns.add(c.getColumnName());
                }

                System.out.println("Indexed Columns "+ indexedColumns);

                String tempString;
                String[] rows;
                Map<String, Map> colNames=new HashMap<>();
//
//          index creation
            int pos;
            for(String c: indexedColumns) {
//                for (int i = 0; i < indexedColumns.size(); i++) {
                    TreeMap<String, ArrayList<Integer>> temp=new TreeMap<>();
                    recordIter = data.iterator();
                    int rowNum = 0;
                    while(recordIter.hasNext())
                    {
                        rows = recordIter.next();
                        pos= colPos.get(c);
                        tempString = rows[pos];
                        System.out.println(tempString);
                        if(temp.containsKey(tempString))
                        {
                            ArrayList<Integer> rowsList=temp.get(tempString);
                            rowsList.add(rowNum);
                            System.out.println(rowsList);
                        }
                        else
                        {
                            ArrayList<Integer> tempVal = new ArrayList<>();
                            tempVal.add(rowNum);
                            temp.put(tempString.toUpperCase(), tempVal);

                        }
                        rowNum++;
                    }
                    this.indexes.put(c.toUpperCase(), temp);
                    temp=null;
                }
            }
            catch(FileNotFoundException e)
            {
                System.out.println("Table not found");
            }

        }
    }
}