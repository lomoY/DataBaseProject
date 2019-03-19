package dubstep.TreeNode;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author Lomo
 * TableNode Contains:
 * 1. TableFile <---- The path to the table file
 * 2. columnDefinition <------ The column's schema of the table
 * 3.
 */
public class TableNode extends TreeNode implements FromItemVisitor,IntoTableVisitor {
    String TableName;
    Table TableObj;
    List<ColumnDefinition> columnDefinitions;
    File TableFile;
    BufferedReader br;

    public TableNode(FromItem fromTable) {
        fromTable.accept(this);
    }

    public TableNode(CreateTable createTable) {

        this.TableName=createTable.getTable().getName();
        this.TableObj=createTable.getTable();
        this.columnDefinitions=createTable.getColumnDefinitions();
        this.TableFile = Paths.get("/Users/Lomo/Desktop/Courses/DataBaseSystem/SourceCode/checkpoint1_1/test/NBA_Examples/PLAYERS.csv").toFile();

    }

    /**
     * Iterator
     *
     * @return
     */

    private class Itr implements Iterator<Tuple> {
        BufferedReader br;
        File TableFile=TableNode.this.TableFile;
        Table TableObj =TableNode.this.TableObj;
        List<ColumnDefinition> columnDefinitions=TableNode.this.columnDefinitions;

        public Itr() {
            try {
                br = new BufferedReader(new FileReader(TableFile));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        /**
         *
         * @return tuple accroding to the condition in schema(columndifinitions)
         *
         */

        @Override
        public Tuple next() {
            try {
                Tuple tp = new Tuple(columnDefinitions);
                String[] columnValues = this.br.readLine().split("\\|");

                for(int i =0;i<columnValues.length;i++){
                    Column column = new Column(TableObj,columnDefinitions.get(i).getColumnName().toString());
                    String colDataType = columnDefinitions.get(i).getColDataType().toString();
                    switch (colDataType) {
                        case "int":
                            tp.setColumn(column, new LongValue(Long.parseLong(columnValues[i])));
                            break;
                        case "decimal":
                            tp.setColumn(column, new DoubleValue(Double.parseDouble(columnValues[i])));
                            break;
                        case "date":
                            tp.setColumn(column, new StringValue("'" + columnValues[i] + "'"));
                            break;
                        case "varchar":
                        case "char":
                        case "string":
                            tp.setColumn(column, new StringValue("'" + columnValues[i] + "'"));
                            break;
                        default:
                            tp.setColumn(column, new NullValue());
                    }
                }
                return tp;

            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public boolean hasNext() {
            try{
                if(br==null){
                    return false;
                }else if(br.ready()){
                    return true;
                }
                else {
                    br.close();
                    return false;
                }
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }


        @Override
        public void remove() {
        }
        @Override
        public void forEachRemaining(Consumer action) {}

    }


    /**
     * FromItemVisitor
     * @param table
     */

    @Override
    public void visit(Table table) {
//        this.TableObj = TableManager.getTable(this.TableName);
    }

    @Override
    public void visit(SubSelect subSelect) {

    }

    @Override
    public void visit(SubJoin subJoin) {

    }

//
    @Override
    public Iterator<Tuple> iterator() {
        return new Itr();
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }
}
