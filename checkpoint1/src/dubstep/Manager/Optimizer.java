package dubstep.Manager;

/**
 *
 * @author Lomo
 *
 */

import dubstep.TreeNode.JoinNode;
import dubstep.TreeNode.SelectionNode;
import dubstep.TreeNode.TreeNode;

/**
 *
 * Pattern
 *
 * Project ---> Where -----> Join ----> Table1, Table2
 *
 * Project ---> Join ------> Where ----> Table1,
 *                           Where ----> Table2
 */

public class Optimizer {
    TreeNode oldTree;
    TreeNode whereNode;
    TreeNode lhsFromItemNode;
    TreeNode rhsFromItemNode;

    boolean pattern1 = false;

    public Optimizer(TreeNode parseTree) {
        this.oldTree=parseTree;
    }

    public void optimize(TreeNode treeNode){

        if(treeNode.getLeftChildNode()!=null){

            if(treeNode.getLeftChildNode() instanceof SelectionNode){ //WhereNode

            }else if(treeNode instanceof SelectionNode && treeNode.getLeftChildNode() instanceof JoinNode){ // JoinNode
                treeNode.getLeftChildNode();
            }

        }

    }
}
