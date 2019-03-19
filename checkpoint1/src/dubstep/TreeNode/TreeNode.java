package dubstep.TreeNode;

public abstract class TreeNode implements Iterable<Tuple>{
    TreeNode leftChildNode;
    TreeNode rightChildNode;


    public TreeNode getLeftChildNode() {
        return leftChildNode;
    }

    public TreeNode getRightChildNode() {
        return rightChildNode;
    }


    public void setLeftChildNode(TreeNode leftChildNode) {
        this.leftChildNode = leftChildNode;
    }

    public void setRightChildNode(TreeNode rightChildNode) {
        this.rightChildNode = rightChildNode;
    }
}
