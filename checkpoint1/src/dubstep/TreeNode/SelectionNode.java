package dubstep.TreeNode;

import dubstep.Manager.EvaluatorManager;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.PrimitiveValue.InvalidPrimitive;

import java.nio.file.DirectoryStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SelectionNode  extends TreeNode {

    private  Expression whereCondition;

    public SelectionNode(Expression whereExpression) {
        this.whereCondition = whereExpression;
    }

    private class Itr implements Iterator<Tuple>{
        DirectoryStream.Filter<Tuple> filter;
        Iterator<Tuple> lfir;
        Tuple tp;
        EvaluatorManager evaluatorManager=new EvaluatorManager(SelectionNode.this.whereCondition);
        boolean hasnext =true;


        public Itr() {
            lfir = SelectionNode.this.leftChildNode.iterator();
        }

        @Override
        public boolean hasNext() {
//            I assign true as the initial value of notEnd because I assume the tuple is match the condition
//            if it doesn't match, then the value of notEnd will be changed to false in the while loop
            boolean notEnd=true;
            if(lfir.hasNext()){
                try{
                    this.tp =lfir.next();
                    while(evaluatorManager.evaluateAttr(tp).toBool() == false){
                        if(lfir.hasNext()){
                            this.tp=lfir.next();
                        }else{
                            notEnd=false;
                            break;
                        }
                    }
                }catch (InvalidPrimitive e){
                    e.printStackTrace();
                }
            }else {
                notEnd= false;
            }
            return notEnd;
        }

        @Override
        public Tuple next() {
            return this.tp;
        }

        @Override
        public void remove() {}

        @Override
        public void forEachRemaining(Consumer<? super Tuple> action) { }
    }


//    Iterable
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
