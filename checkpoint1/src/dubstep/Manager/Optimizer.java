package dubstep.Manager;
import net.sf.jsqlparser.expression.Expression;

/**
 *
 * @author Lomo
 *
 * todo
 * 1. confirm the implementation of cast the ra
 * 2. how to handle comple query, should i concern all the query case?
 * I think I should use some feature.
 *
 */

import dubstep.TreeNode.*;

/**
 *
 * Pattern
 *
 * Project ---> Where -----> Join ----> Table1, Table2
 *
 * Project ---> Join ------> Where ----> Table1,
 *                           Where ----> Table2
 */

public class Optimizer{

    TreeNode oldTree;

    public Optimizer(TreeNode parseTree) {

        this.oldTree=parseTree;

    }

    public void optimize(TreeNode treeNode){

        TreeNode lfhNode = treeNode.getLeftChildNode();

        if(treeNode.getLeftChildNode()!=null){

            if(treeNode.getLeftChildNode() instanceof SelectionNode){ //WhereNode
//          Enter Selection Push Down
                treeNode.setLeftChildNode(SelectionPushDown((SelectionNode) treeNode.getLeftChildNode()));

            }else {

                optimize(lfhNode);

            }
        }
    }


    /**
     *
     *
     * @param selectionNode
     *
     * selectionNode--
     * ---left: FromItemNode
     *            ------ProjectionTypeNode
     *                  ------ ProjectionNode
     *                           -----JoinNode
     *                                  -------- FromItemNode
     *                                  -------- FromItemNode
     */


    private TreeNode SelectionPushDown (SelectionNode selectionNode) {

        TreeNode lftChild = selectionNode.getLeftChildNode();
        TreeNode rhsChild = selectionNode.getRightChildNode();

        if(lftChild!=null){

            if(!(lftChild instanceof TableNode)){

                if(lftChild instanceof JoinNode){

                    TreeNode childOfLhsChild = lftChild.getLeftChildNode();
                    TreeNode childOfRhsChild = lftChild.getRightChildNode();

                    selectionNode.setLeftChildNode(childOfLhsChild);
                    lftChild.setLeftChildNode(SelectionPushDown(selectionNode));

                    selectionNode.setLeftChildNode(childOfRhsChild);
                    lftChild.setRightChildNode(SelectionPushDown(selectionNode));

                }else{

                    TreeNode childOflftChild = lftChild.getLeftChildNode();
                    selectionNode.setLeftChildNode(childOflftChild);
                    lftChild.setLeftChildNode(SelectionPushDown(selectionNode));

                    return lftChild;
                }


            }else{
                //index scan
                Expression whereCondition = selectionNode.getWhereCondition();

                TableNode tn=(TableNode)lftChild;
                String tableName = tn.getTableName();
                RelationManager rm = new RelationManager(whereCondition,tableName);
                IndexScan is = new IndexScan(tableName, rm.getColName(),rm.getLowerBound(),rm.getUpperBound(),false);
                lftChild=is;

                return lftChild;

            }
        }

        if(rhsChild!=null){

            if(!(lftChild instanceof TableNode)){

                TreeNode childOflftChild = lftChild.getLeftChildNode();
                selectionNode.setLeftChildNode(childOflftChild);
                lftChild.setLeftChildNode(selectionNode);
                SelectionPushDown(selectionNode);

            }

            return rhsChild;

        }

        return lftChild;
    }


}