package parser;
import java.io.FileNotFoundException;

public class Parser {
	private Scanner scanner;
	private SyntaxTree tree;
	/**
	 * Constructs a Parser to Parse the file located in inputFilePath
	 * @param inputFilePath the path of the file containing the program written in TINY language
	 */
	public Parser(String inputFilePath) {
		try {
			//create scanner object to scan the file located in inputfilePath
			this.scanner = new Scanner(inputFilePath);
			this.tree = new SyntaxTree();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
		}
	}
	
	
	private int stmt_sequence() throws SyntaxErrorException{
		int temp = statement();
		int res = temp;
		Token token = scanner.getCurrentToken();
		while(token.getType() == Token.TokenType.SEMI_COLON){
			match(Token.TokenType.SEMI_COLON);
			int newTemp = statement(); //
			tree.addChild(temp, newTemp);
			tree.sameRank(temp, newTemp);
			temp = newTemp;
			token = scanner.getCurrentToken();
		}
		return res;
	}
	
	private int statement() throws SyntaxErrorException{
		Token stmt = scanner.getCurrentToken();
		switch (stmt.getType()){
		case IF:
			return if_stmt();
		case REPEAT:
			return repeat_stmt();
		case IDENTIFER:
			return assign_stmt();
		case READ:
			return read_stmt();
		case WRITE:
			return write_stmt();
		default:
			throw(new SyntaxErrorException("Syntax error near token \"" + scanner.getCurrentToken().toString()+ "\""));
		}
	}
	
	private int if_stmt() throws SyntaxErrorException {
		match(Token.TokenType.IF);
		int ifNode = tree.makeIFNode();
		tree.addChild(ifNode, exp());
		match(Token.TokenType.THEN);
		tree.addChild(ifNode, stmt_sequence());
		Token token = scanner.getCurrentToken();
		if (token.getType() == Token.TokenType.ELSE){//
			match(Token.TokenType.ELSE);
			tree.addChild(ifNode, stmt_sequence());
		}
		match(Token.TokenType.END);
		return ifNode;
	}

	private int repeat_stmt() throws SyntaxErrorException {
		match(Token.TokenType.REPEAT);
		int repeatNode = tree.makeRepeatNode();
		int bodyNode = stmt_sequence();
		match(Token.TokenType.UNTIL);
		int condNode = exp();
		tree.addChild(repeatNode, condNode );
		tree.addChild(repeatNode, bodyNode);
		return repeatNode;
	}

	private int assign_stmt() throws SyntaxErrorException{
		Token identifier = scanner.getCurrentToken();
		match(Token.TokenType.IDENTIFER);
		match(Token.TokenType.ASSIGN);
		int assignNode = tree.makeAssignNode(identifier.getValue());
		tree.addChild(assignNode, exp());
		return assignNode;
	}
	
	private int read_stmt() throws SyntaxErrorException{
		match(Token.TokenType.READ);
		Token identifier = scanner.getCurrentToken();
		match(Token.TokenType.IDENTIFER);
		return tree.makeReadNode(identifier.getValue());
	}
	
	private int write_stmt() throws SyntaxErrorException{
		match(Token.TokenType.WRITE);
		int writeNode = tree.makeWriteNode();
		tree.addChild(writeNode, exp());
		return writeNode;
	}
	
	private int exp() throws SyntaxErrorException{
		int temp = simple_exp();
		Token currentToken = scanner.getCurrentToken();
		while(currentToken.getType() == Token.TokenType.LESS_THAN ||
				currentToken.getType() == Token.TokenType.EQUAL ||
				currentToken.getType() == Token.TokenType.GREATER_THAN){
			int opNode = comparisonOp();
			tree.addChild(opNode, temp);
			tree.addChild(opNode, simple_exp());
			temp = opNode;
			currentToken = scanner.getCurrentToken();
		}
		return temp;
	}
	
	private int comparisonOp() throws SyntaxErrorException{
		Token token = scanner.getCurrentToken();
		switch(token.getType()){
		case LESS_THAN:
			match(Token.TokenType.LESS_THAN);
			return tree.makeOPNode("<");
		case EQUAL:
			match(Token.TokenType.EQUAL);
			return tree.makeOPNode("=");
		case GREATER_THAN:
			match(Token.TokenType.GREATER_THAN);
			return tree.makeOPNode(">");
		default:
			throw(new SyntaxErrorException("Syntax error near token \"" + scanner.getCurrentToken().toString()+ "\""));
		}
	} 
	
	private int simple_exp() throws SyntaxErrorException{
		int temp = term();
		Token currentToken = scanner.getCurrentToken();
		while(currentToken.getType() == Token.TokenType.PLUS ||
				currentToken.getType() == Token.TokenType.MINUS){
			int opNode = addOp();
			tree.addChild(opNode, temp);
			tree.addChild(opNode, term());
			temp = opNode;
			currentToken = scanner.getCurrentToken();
		}
		return temp;
	}
	
	private int addOp() throws SyntaxErrorException{
		Token token = scanner.getCurrentToken();
		switch(token.getType()){
		case PLUS:
			match(Token.TokenType.PLUS);
			return tree.makeOPNode("+");
		case MINUS:
			match(Token.TokenType.MINUS);
			return tree.makeOPNode("-");
		default:
			throw(new SyntaxErrorException("Syntax error near token \"" + scanner.getCurrentToken().toString()+ "\""));
		}
	} 

	
	private int term() throws SyntaxErrorException{
		int temp = factor();
		Token currentToken = scanner.getCurrentToken();
		while(currentToken.getType() == Token.TokenType.DIVIDE ||
				currentToken.getType() == Token.TokenType.TIMES){
			int opNode = mulOp();
			tree.addChild(opNode, temp);
			tree.addChild(opNode, factor());
			temp = opNode;
			currentToken = scanner.getCurrentToken();
		}
		return temp;
	}
	
	private int mulOp() throws SyntaxErrorException{
		Token token = scanner.getCurrentToken();
		 
		switch(token.getType()){
		case DIVIDE:
			match(Token.TokenType.DIVIDE);
			return tree.makeOPNode("/");
		case TIMES:
			match(Token.TokenType.TIMES);
			return tree.makeOPNode("*");
		default:
			throw(new SyntaxErrorException("Syntax error near token \"" + scanner.getCurrentToken().toString()+ "\""));
		}
	}
	
	private int factor() throws SyntaxErrorException{
		Token currentToken = scanner.getCurrentToken();
		int temp;
		switch(currentToken.getType()){
		case LEFT_PARENTHESIS:
			match(Token.TokenType.LEFT_PARENTHESIS);
			temp = exp();
			match(Token.TokenType.RIGHT_PARENTHESIS);
			break;
		case NUMBER:
			temp = tree.makeConstNode(currentToken.getValue());
			match(Token.TokenType.NUMBER);
			break;
		case IDENTIFER:
			temp = tree.makeIDNode(currentToken.getValue());
			match(Token.TokenType.IDENTIFER);
			break;
		default:
			throw(new SyntaxErrorException("Syntax error near token \"" + scanner.getCurrentToken().toString()+ "\""));
		}
		return temp;
	}
	
	
	private void match(Token.TokenType type) throws SyntaxErrorException{
		Token token = scanner.getCurrentToken();
		if(token.getType() == type)
			scanner.advanceInput();
		else
			throw(new SyntaxErrorException("Syntax error. Expected " + type + " instead got token \"" + scanner.getCurrentToken().toString()+ "\""));
	}
        
        /**
         * Start parsing the file until the end of file
         * @param type
         * @throws SyntaxErrorException 
         */
	public void start(String type) throws SyntaxErrorException
	{
            stmt_sequence();
            tree.end();
	}
        
        public SyntaxTree getTree(){
            return tree;
        }
}
