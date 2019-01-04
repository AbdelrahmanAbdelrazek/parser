package parser;
import java.io.File;

public class SyntaxTree {
	
	private enum NodeType {EXP, STATEMENT};
	private static int nodeCounter = 0;
	private GraphViz graph;
	/**
	 * Constructs a Syntax Tree object
	 */
	public SyntaxTree() {
		/* To create a GraphViz object:
			 	* the location of the executable of GraphViz 'dot.exe' muse be specified
			 	* and also a location to store temp files ("." = the directory of the jar file)
		*/
		this.graph = new GraphViz("./GraphVizLite/dot.exe", ".");
		//start a new graph
		graph.addln(graph.start_graph());
		graph.addln("graph [ordering=\"out\"];");
		graph.addln("edge[arrowhead=\"none\"];");
	}
	
	/**
	 * adds a child to a parent node in the tree
	 * @param parentNode the number of the parent node
	 * @param childNode	the number of the child node that should be added as a child to the parent node
	 */
	public void addChild(int parentNode, int childNode){
		graph.addln("n" + parentNode + "->" +"n"+ childNode);
	}
	/**
	 * makes two nodes at the same vertical level
	 * @param node1 the number of the first node 
	 * @param node2	the number of the second node that should be leveled with the first node
	 */
	public void sameRank(int node1, int node2){
		graph.addln("{rank=same; n" + node1 + " n" + node2 + "}");
	}
	/**
	 * Create a new node in the tree and returns the number of that node
	 * @param label the string that should be written inside that node
	 * @param type	the type of the node (expression or statement) which will decide the shape of the node (circle or square)
	 * @return the number of the new node
	 */
	private int makeNode(String label, NodeType type){
		String shape = (type == NodeType.EXP)? "circle" : "box";
		String node = "n" + nodeCounter + " [label=\"" + label + "\", " + "shape="  + shape + "];";
		graph.addln(node);
		return nodeCounter++;
	}
	
	/**
	 * create a constant expression  node 
	 * @param val the value of the Constant that should be written inside the node
	 * @return the number of the new node
	 */
	public int makeConstNode(String val){
		return makeNode("Const\\n(" + val + ")" , NodeType.EXP);
	}
	
	/**
	 * create an identifier expression node 
	 * @param val the value of the identifier that should be written inside the node
	 * @return the number of the new node
	 */
	public int makeIDNode(String val){
		return makeNode("ID\\n(" + val + ")" , NodeType.EXP);
	}
	
	/**
	 * create an operator expression node 
	 * @param val the value of the operator sign that should be written inside the node
	 * @return the number of the new node
	 */
	public int makeOPNode(String val){
		return makeNode("OP\\n(" + val + ")" , NodeType.EXP);
	}
	
	/**
	 * create an if statement node 
	 * @return the number of the new node
	 */
	public int makeIFNode(){
		return makeNode("if" , NodeType.STATEMENT);
	}
	
	/**
	 * create a repeat statement node 
	 * @return the number of the new node
	 */
	public int makeRepeatNode(){
		return makeNode("repeat" , NodeType.STATEMENT);
	}
	
	/**
	 * create a write statement node 
	 * @return the number of the new node
	 */
	public int makeWriteNode(){
		return makeNode("write" , NodeType.STATEMENT);
	}
	
	/**
	 * create a read statement node 
	 * @param val the identifier that should be written inside the node
	 * @return the number of the new node
	 */
	public int makeReadNode(String val){
		return makeNode("read\\n(" + val + ")"  , NodeType.STATEMENT);
	}
	
	/**
	 * create an assign statement node 
	 * @param val the identifier that should be written inside the node
	 * @return the number of the new node
	 */
	public int makeAssignNode(String val){
		return makeNode("assign\\n(" + val + ")"  , NodeType.STATEMENT);
	}
        
        /**
         * Finalize drawing the syntax tree 
         * Must be called after syntax tree drawing
         */
        public void end(){
            graph.addln(graph.end_graph());
            System.out.println(graph.getDotSource());
        }
	
	/**
	 * Output the syntax tree into file 
	 * @param filePath the path of the output file
	 * @param type the type of the output file
	 * Supported types are:
	 	* dot
	 	* fig   // open with xfig
	 	* pdf
		* ps
		* svg    // open with inkscape
		* png
		* plain
                * gif
	 * 
	 */
	public void outputToFile(String filePath){
		graph.increaseDpi();   // 106 dpi
		String repesentationType= "dot";
		//		String repesentationType= "neato";
		//		String repesentationType= "fdp";
		//		String repesentationType= "sfdp";
		// 		String repesentationType= "twopi";
		// 		String repesentationType= "circo";
		
		//File out = new File("/tmp/out"+gv.getImageDpi()+"."+ type);   // Linux
		File out = new File(filePath);    // Windows
                String fileName = out.getName();
                String type = fileName.substring(fileName.indexOf('.') + 1);
		graph.writeGraphToFile( graph.getGraph(graph.getDotSource(), type, repesentationType), out );
	}
        
        /**
         * Returns the image of the syntax tree
         * @param type Type of the output image to be produced, e.g.: gif, dot, fig, pdf, ps, svg, png.
         * @return image of the syntax tree
         */
        public byte[] getImg(String type){
            graph.increaseDpi();   // 106 dpi
            return graph.getGraph(graph.getDotSource(), type, "dot");
        }
}
