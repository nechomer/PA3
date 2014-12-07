package ic;

import ic.ast.ASTNode;
import ic.ast.PrettyPrinter;
import ic.parser.Lexer;
import ic.parser.LexicalError;
import ic.parser.LibParser;
import ic.parser.ParserException;
import ic.parser.parser;

import java.io.FileReader;
import java.io.IOException;

import java_cup.runtime.Symbol;

public class Compiler {
    public static void main(String[] args)
    {
    	parser pp;
    	LibParser lp;
    	Symbol result;
    	ASTNode programNode, libraryProgramNode;
    	String LibraryFile;
    	try {
    		
    		pp = new parser(new Lexer(new FileReader(args[0])));
    		result = pp.parse();
    		programNode = (ASTNode) result.value;

    		if (programNode != null) 
    			System.out.println(programNode.accept(new PrettyPrinter(args[0])));

    		if (args.length > 1) { // Library file is also supplied
    			if (!args[1].substring(0,2).equals("-L")) {
    				System.out.println("\n ERROR: Library file must be supplied with preceding -L ");
    				return;
    			}
    			LibraryFile = args[1].substring(2);
              lp = new LibParser(new Lexer(new FileReader(LibraryFile)));
              result = lp.parse();
              libraryProgramNode = (ASTNode) result.value;
              
              if (libraryProgramNode != null) 
                  System.out.println(libraryProgramNode.accept(new PrettyPrinter(LibraryFile)));
    		}
    		
    	} catch (ParserException  | LexicalError e) {
    		System.out.println(e.getMessage());
    		System.exit(1);
    	} catch (IOException e) {
    		System.err.printf("IO Error:\n%s\n", e.getMessage());
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    
    }
}
