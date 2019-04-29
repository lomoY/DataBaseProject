package dubstep.Manager;

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

        TreeNode lfhNode = treeNode.getLeftChildNode();
        if(treeNode.getLeftChildNode()!=null){

            if(treeNode.getLeftChildNode() instanceof SelectionNode){ //WhereNode
//          Enter Selection Push Down
                treeNode.setLeftChildNode(SelectionPushDown(treeNode.getLeftChildNode()));

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

    private TreeNode SelectionPushDown (TreeNode selectionNode) {

        if (selectionNode.getLeftChildNode() instanceof FromItemNode) {

            FromItemNode fin = (FromItemNode) selectionNode.getLeftChildNode();

            if (fin.getLeftChildNode() instanceof ProjectionTypeNode) {

                ProjectionTypeNode ptn = (ProjectionTypeNode) fin.getLeftChildNode();

                if (ptn.getLeftChildNode() instanceof ProjectionNode) {

                    ProjectionNode pn = (ProjectionNode) ptn.getLeftChildNode();

                    if (pn.getLeftChildNode() instanceof JoinNode) {

                        JoinNode jn = (JoinNode) pn.getLeftChildNode();

                        FromItemNode lfhFromItem = (FromItemNode) jn.getLeftChildNode();
                        FromItemNode rhsFromItem = (FromItemNode) jn.getRightChildNode();

                        SelectionNode lfhSN = (SelectionNode) selectionNode;
                        SelectionNode rhsSN = (SelectionNode) selectionNode;

                        lfhSN.setLeftChildNode(lfhFromItem);
                        rhsSN.setRightChildNode(rhsFromItem);

                        jn.setLeftChildNode(lfhSN);
                        jn.setRightChildNode(rhsSN);
                        return ptn;
                    }
                    return selectionNode;
                }
                return selectionNode;
            }
            return selectionNode;
        }
        return selectionNode;

    }
}
