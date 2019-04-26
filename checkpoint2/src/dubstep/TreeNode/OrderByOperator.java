package dubstep.TreeNode;

import dubstep.Manager.EvaluatorManager;
import net.sf.jsqlparser.expression.PrimitiveValue;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;

import javax.lang.model.type.PrimitiveType;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Lomo
 *
 *
 * Replacement Merge Sort
 *
 * Design:
 *
 *
 */

public class OrderByOperator extends TreeNode implements OrderByVisitor{

    public List<OrderByElement> odbEle;
    int workingSetSize = 3000;
    List<Tuple> workingSet = new ArrayList<Tuple>();
    int num = 0; //to record name of temp files
    List<Tuple> sortedQueue = new ArrayList<>();
    ArrayList<String> fileNameList = new ArrayList<String>();
    PrimitiveValue lastValue;


    public OrderByOperator(List<OrderByElement> orderByElements) {
        this.odbEle= orderByElements;

    }

    private class Itr implements Iterator<Tuple>{
        Iterator<Tuple> lfir;
        FileInputStream fis;
        ObjectInputStream ois;
        boolean hasResult = true;
        Tuple tp=null;

        public Itr() {
            lfir = OrderByOperator.this.leftChildNode.iterator();

            //Step 1 createBlock
            while(lfir.hasNext()){
                if(workingSetSize>0){
                    Tuple tp =lfir.next();

                    OrderByOperator.this.workingSet.add(tp);
                    workingSetSize--;
                }

                if(workingSetSize==0){
                    createBlock(workingSet);
                    workingSetSize=3000;
                    workingSet.clear();
                }
            }

            if(workingSetSize>0&&workingSetSize<3000){
                createBlock(workingSet);
                workingSetSize=3000;
                workingSet.clear();
            }

            //Step2 merge

            while(OrderByOperator.this.fileNameList.size()!=1){
                try{
                    for(int i =0;i<OrderByOperator.this.fileNameList.size();i=i+2){

                        String fileName = "temp"+num;
                        fileNameList.add(fileName);
                        File tempFile= new File(fileName);
                        num++;

                        FileOutputStream fos = new FileOutputStream(tempFile);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);

                        merge(OrderByOperator.this.fileNameList.get(0),OrderByOperator.this.fileNameList.get(1),oos);
                        File f1 = new File(OrderByOperator.this.fileNameList.get(0));
                        File f2 = new File(OrderByOperator.this.fileNameList.get(1));
                        f1.delete();
                        f2.delete();
                        OrderByOperator.this.fileNameList.remove(0);
                        OrderByOperator.this.fileNameList.remove(0);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            try{
                File result = new File(OrderByOperator.this.fileNameList.get(0));
                fis = new FileInputStream(result);
                ois = new ObjectInputStream(fis);

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void remove() {

        }

        @Override
        public void forEachRemaining(Consumer action) {

        }

        @Override
        public boolean hasNext() {
            try{
                if(hasResult==true){
                    tp=(Tuple) ois.readObject();
                    return true;
                 }else {
                    ois.close();
                    fis.close();
                    return false;
                }
            }catch (Exception e){
                hasResult=false;
                return false;
            }
        }

        @Override
        public Tuple next() {
            Tuple tp = this.tp;
            return tp;
        }
    }

    public void createBlock(List<Tuple> workingSet){

            workingSet.sort(new myComparator());

            String fileName = "temp"+num;
//            System.out.println(fileName);
            fileNameList.add(fileName);
            File tempFile= new File(fileName);
            num++;

            try{
                //write it out to disk
                //entire working set is big.
                FileOutputStream fos = new FileOutputStream(tempFile);
//                BufferedOutputStream bos = new BufferedOutputStream(fos,1);//size
                ObjectOutputStream oos = new ObjectOutputStream(fos);//can write out as object.
                for(Tuple tp:workingSet){
//                    oos.writeUnshared(tp);
                    oos.writeObject(tp);
                    oos.reset();
                }

                oos.close();
                fos.close();

//                FileInputStream fis = new FileInputStream(tempFile);
//                ObjectInputStream ois = new ObjectInputStream(fis);
//                Object tp = ois.readObject();
//                ArrayList<Tuple> abcde = (ArrayList)tp;
//                for(Tuple x:abcde){
//                    System.out.println(x);
//                }

            }catch (Exception e){}
    }

    public void merge(String file1,String file2,ObjectOutputStream oos){
        try{
            boolean hasNext1 = true;
            boolean hasNext2 = true;
            FileInputStream fis1=new FileInputStream(file1);
            FileInputStream fis2=new FileInputStream(file2);
            ObjectInputStream ois1= new ObjectInputStream(fis1);
            ObjectInputStream ois2=new ObjectInputStream(fis2);
            Tuple tp1=null;
            Tuple tp2=null;
            boolean readNext1 = true;
            boolean readNext2 = true;


            while(hasNext1){

                if(readNext1||tp1==null){
                    try{
                        tp1 = (Tuple)ois1.readObject();
                    }catch (EOFException e){
                            hasNext1=false;
                    }
                }

                while(hasNext2){
                    if(readNext2||tp2==null){
                        try{

                            tp2 = (Tuple)ois2.readObject();
                        }catch (EOFException e){
                            hasNext2=false;
                        }
                    }

                    if(hasNext1&&hasNext2){
                        EvaluatorManager em = new EvaluatorManager(odbEle.get(0).getExpression());
                        if(!em.eval(new GreaterThan(tp1.getColumnValue(odbEle.get(0).getExpression().toString()),tp2.getColumnValue(odbEle.get(0).getExpression().toString()))).toBool()){
                            oos.writeObject(tp1);
                            readNext1=true;
                            readNext2=false;
//                            System.out.println(tp1);
                        }else{
                            oos.writeObject(tp2);
//                            System.out.println(tp2);
                            readNext1=false;
                            readNext2=true;
                        }
                        break;
                    }
                    if(hasNext1==false&&hasNext2==true){
                        oos.writeObject(tp2);
//                        System.out.println(tp2);

                        readNext2=true;
                    }

                }
                if(hasNext2==false&&hasNext1==true){
                    oos.writeObject(tp1);
                    readNext1=true;
//                    System.out.println(tp1);

                }

            }

            ois1.close();
            ois2.close();

        }catch (Exception e){

        }

    }

    @Override
    public void forEach(Consumer<? super Tuple> action) {}

    @Override
    public Spliterator<Tuple> spliterator() {
        return null;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return new Itr();
    }

    @Override
    public void visit(OrderByElement orderByElement) {

    }

    private class myComparator implements Comparator<Tuple>{

        @Override
        public int compare(Tuple t1, Tuple t2) {

            int result = 0;
            for(int i =0;i<odbEle.size();i++){

                PrimitiveValue v1 = t1.getColumnValue(odbEle.get(i).getExpression().toString());
                PrimitiveValue v2 = t2.getColumnValue(odbEle.get(i).getExpression().toString());

                try{
                    //date and
                    Long v1long = v1.toLong();
                    Long v2long = v2.toLong();
                    result = v1long.compareTo(v2long);

                    if(result==0){
                        //do nothing, read next odbEle
                    }else{

                        return v1long.compareTo(v2long);

                    }

                    /**
                     * 0 :  v1 == v2
                     * -1:  v1<v2
                     * 1 :  v1>v2
                     */
                }catch (Exception e){}
            }
            return 0;
        }


    }

}
