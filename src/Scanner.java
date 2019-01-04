package parser;
import java.io.*;

public class Scanner {

	private RandomAccessFile inputFile;
	
	//Represents the states in DFA
	public enum State {
	    START, INCOMMENT, INID, INNUM, INASSIGN, DONE, SYNTAX_ERROR
	}
	
	/**
	 * Constructs a Scanner to scan the file located in filePath
	 * @param filePath The path of the file containing the TINY language program
	 * @throws FileNotFoundException 
	 */
	public Scanner(String filePath) throws FileNotFoundException {
			this.inputFile =  new RandomAccessFile(filePath,"r");
	}
	
	

	
	/**
	 * Returns the token the cursor of the file is pointing to
	 * @param inputFile	File from which to extract the next token
	 * @return Returns the current Token the cursor of inputFile is pointing to.
	 * @throws IOException
	 */
	public Token getCurrentToken() {
		try{
			long pos = inputFile.getFilePointer();
			Token temp = getNextToken();
			inputFile.seek(pos);
			return temp;
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Advance the cursor to point to the next token
	 * @param inputFile File from which to extract the next token
	 * @throws IOException
	 */
	public void advanceInput(){
		try {
			getNextToken();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Returns the next token from the inputStreaam 
	 * @param inputFile 	File from which to extract the next token
	 * @return The next token 
	 * @throws IOException
	 */
	private Token getNextToken() throws IOException{
		State currentState = State.START;
		
		Token token = new Token();
		long markPos = 0;
		while(currentState != State.DONE && currentState != State.SYNTAX_ERROR){
			char ch = (char)inputFile.read();
//			if(inputFile.getFilePointer() == inputFile.length())
//				System.out.println("END OF FILE");
			switch(currentState){
			case START:
				if(Character.isWhitespace(ch)){
					currentState = State.START;
				}else if(ch == '{'){
					currentState = State.INCOMMENT;
				}else if(Character.isLetter(ch)){
					currentState = State.INID;
					token.setValue(token.getValue() + ch);
					//mark this position so that if the next character is not a letter, i can read it again 
					markPos = inputFile.getFilePointer();	
				}else if(Character.isDigit(ch)){
					currentState = State.INNUM;
					token.setValue(token.getValue() + ch);
					//mark this position so that if the next character is not a letter, i can read it again 
					markPos = inputFile.getFilePointer();		
				}else if(ch == ':'){
					currentState = State.INASSIGN;
					token.setValue(token.getValue() + ch);
				}else if("+-*/=<>();".indexOf(ch) != -1){	//if character is one of the symbols in "+-*/=<();"
					currentState= State.DONE;
					token.setValue(ch + "");
					token.setType(getSymbolTokenType(ch));
				}else{
					currentState= State.DONE;
					//END OF FILE
					token.setType(Token.TokenType.END_OF_FILE);
				}
				break;
			case INCOMMENT:
				if(ch == '}'){
					currentState= State.START;
				}
				break;
			case INID:
				if(Character.isLetter(ch)){
					token.setValue(token.getValue() + ch);
					markPos = inputFile.getFilePointer();	 //mark this position
				}else{
					currentState= State.DONE;
					//resets the buffer so that the next time the buffer is read it reads 
					//this character again ex: ahmed: the reset enables ":" to be read again
					//the next time inputFile.read() is called 
					inputFile.seek(markPos);
					token.setType(getIdentifierTokenType(token.getValue()));	//to see if the identifier is one of the reserved words and return the correct TokenType
				}
				break;
			case INNUM:
				if(Character.isDigit(ch)){
					token.setValue(token.getValue() + ch);
					markPos = inputFile.getFilePointer();		//mark this position
				}else{
					currentState= State.DONE;
					inputFile.seek(markPos);
					token.setType(Token.TokenType.NUMBER);
				}
				break;
			case INASSIGN:
				token.setValue(token.getValue() + ch);
				if(ch == '='){
					token.setType(Token.TokenType.ASSIGN);
					currentState = State.DONE;
				}else{
					currentState = State.SYNTAX_ERROR;
				}
				break;
			}
		}
		
		if(currentState == State.DONE)
			return token;
		else
			return new Token();
	}

	/**
	 * Returns the correct TokenType based on the value of the identifier.
	 * if the identifier is one of the reserved words like (if, then, repeat .. etc) the token type will
	 * be (IF,THEN,REPEAT .. etc).
	 * if the identifier is not one of the reserved words the type will be IDENTIFIER.
	 * @param identifier	the identifier whose TokenType we want to determine
	 * @return	The TokenType of the identifier
	 */
	private Token.TokenType getIdentifierTokenType(String identifier) {
		//IF, THEN, ELSE, END, REPEAT, UNTIL, READ, WRITE,
		return 	identifier.equals("if")? 		Token.TokenType.IF :
				identifier.equals("then") ? 		Token.TokenType.THEN :
				identifier.equals("else") ? 		Token.TokenType.ELSE :
				identifier.equals("end") ? 		Token.TokenType.END :
				identifier.equals("repeat") ?	Token.TokenType.REPEAT :
				identifier.equals("until") ? 	Token.TokenType.UNTIL :
				identifier.equals("read") ? 		Token.TokenType.READ :
				identifier.equals("write") ? 	Token.TokenType.WRITE :	Token.TokenType.IDENTIFER;
	}

	/**
	 * Returns the token type of the symbols + - * / = < ( ) ;
	 * @param symbol The symbol whose token type is unknown
	 * @return The TokenType of the symbol
	 */
	private Token.TokenType getSymbolTokenType(char symbol) {
		return 	symbol == '+'?	Token.TokenType.PLUS :
				symbol == '-'?	Token.TokenType.MINUS :
				symbol == '*'?	Token.TokenType.TIMES :
				symbol == '/'?	Token.TokenType.DIVIDE :
				symbol == '='?	Token.TokenType.EQUAL :
				symbol == '<'?	Token.TokenType.LESS_THAN :
				symbol == '>'?	Token.TokenType.GREATER_THAN :
				symbol == '('?	Token.TokenType.LEFT_PARENTHESIS :
				symbol == ')'?	Token.TokenType.RIGHT_PARENTHESIS : Token.TokenType.SEMI_COLON ;
	}
	
	public static void main(String[] args) throws IOException{
		PrintWriter out = new PrintWriter("output.txt");
		out.print("");
		Scanner scanner = new Scanner("input.txt");
		while(scanner.inputFile.getFilePointer() <= scanner.inputFile.length()){
			Token t = scanner.getCurrentToken();
			scanner.advanceInput();
			if(t.getType() == Token.TokenType.END_OF_FILE)	break;
			if (t.getType() == null || t.getValue() == ""){
				out.println("Syntax Error");
				break; //TODO: handle syntax errors better
			}else{
				out.println(t);
			}
		}
		out.close();
	}
}
