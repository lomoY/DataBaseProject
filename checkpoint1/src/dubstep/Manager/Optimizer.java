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
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

//        this.oldTree=parseTree;
        this.oldTree=schemaGenerator(parseTree);

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


                // check if use IndexScan or not
                /**
                 *
                 * String tableName,
                 * String colName,
                 * PrimitiveValue lowerBound,
                 * PrimitiveValue upperBound,
                 * boolean equals,
                 * boolean softLowerBound,
                 * boolean softUpperBound
                 *
                 */


                IndexScan is = new IndexScan(tableName, rm.getColName(),rm.getLowerBound(),rm.getUpperBound(),rm.getEquals(),rm.getSoftLowerBound(),rm.getSoftUpperBound());
                Set<String> keySet=is.getIndexes();
//
//                if(rm.getColName()!=null&&keySet.contains(rm.getColName())){
//
//                    lftChild=is;
//
//                }else{
//                    if(rm.getTableName()==tn.getTableName()){
//                        SelectionNode sn= new SelectionNode(rm.getWhereExpression());
//                        sn.setLeftChildNode(tn);
//                        lftChild=sn;
//                    }else{
//                        lftChild=tn;
//                    }
//
//                }

                if(rm.getTableName()==tn.getTableName()){
                    SelectionNode sn= new SelectionNode(rm.getWhereExpression());
                    sn.setLeftChildNode(tn);
                    lftChild=sn;
                }else{
                    lftChild=tn;
                }

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

    /**
     *
     * schemaGenerator will accept parseTree as value
     * and generate the columnList in each TreeNode, here,
     * I treat columnList as the schema of a corresponding TreeNode
     *
     */

    private TreeNode schemaGenerator(TreeNode treeNode){

        if(!(treeNode.getLeftChildNode()instanceof TableNode)){

            if(treeNode instanceof JoinNode){

                TreeNode LhsNode = treeNode.getLeftChildNode();
                TreeNode RhsNode = treeNode.getRightChildNode();

//                Shallow Copy
                List<Column> temp = schemaGenerator(LhsNode).getLhsColumnList();
                List<Column> temp2 = schemaGenerator(RhsNode).getLhsColumnList();
                List<Column> temp3 = new ArrayList<>(temp);
                temp3.addAll(temp2);

                treeNode.setLhsColumnList(temp3);
                return treeNode;

            }
            else if(treeNode instanceof ProjectionNode){

//                treeNode.setLhsColumnList(treeNode.);
                //todo determine weather should i add column from child node to the columnList of treeNode.
                schemaGenerator(treeNode.getLeftChildNode());
                return treeNode;

            }
            else {

                treeNode.setLhsColumnList(schemaGenerator(treeNode.getLeftChildNode()).getLhsColumnList());
                return treeNode;

            }

        }else {

            treeNode.setLhsColumnList(treeNode.getLeftChildNode().getLhsColumnList());
            return treeNode;

        }
    }
}