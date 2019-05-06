package dubstep.TreeNode;

import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode implements Iterable<Tuple>{
    TreeNode leftChildNode;
    TreeNode rightChildNode;
    Schema lftSchema;
    Schema rightSchema;
    List<Column> LhsColumnList = new ArrayList<>();
    List<Column> RhsColumnList = new ArrayList<>();



    public TreeNode getLeftChildNode() {
        return leftChildNode;
    }

    public TreeNode getRightChildNode() {
        return rightChildNode;
    }
    public List<Column> getLhsColumnList(){return LhsColumnList;}

    public void setLeftChildNode(TreeNode leftChildNode) {
        this.leftChildNode = leftChildNode;
//        this.LhsColumnList = leftChildNode.getLhsColumnList();
    }

    public void setRightChildNode(TreeNode rightChildNode) {

        this.rightChildNode = rightChildNode;
//        this.RhsColumnList = rightChildNode.getLhsColumnList();
    }

    public Schema getSchema(){
        return this.lftSchema;
    }

    public void setLhsColumnList(List<Column> _LhsColumnList){
        this.LhsColumnList = _LhsColumnList;
    }

    public void setRhsColumnList(List<Column> _RhsColumnList){
        this.RhsColumnList=_RhsColumnList;
    }
}