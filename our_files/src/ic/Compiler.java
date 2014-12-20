package ic;

import ic.ast.*;
import ic.parser.*;
import ic.semanticCheck.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java_cup.runtime.Symbol;

public class Compiler {
    public static void main(String[] args)
    {
    	StringBuilder sb = new StringBuilder();
    	parser pp;
    	LibParser lp;
    	Symbol result;
    	ASTNode programNode = null, libraryProgramNode = null;
    	String LibraryFile;
    	String fullPath;
    	String progFileName, libFileName;
    	int index;
    	try {
    		
    		if (args.length > 1) { // Library file is also supplied
    			if (!args[1].substring(0,2).equals("-L")) {
    				System.out.println("\n ERROR: Library file must be supplied with preceding -L ");
    				return;
    			}
    			LibraryFile = args[1].substring(2);
                lp = new LibParser(new Lexer(new FileReader(LibraryFile)));
                result = lp.parse();
                libraryProgramNode = (ASTNode) result.value;
                fullPath = args[1];
        		index = fullPath.lastIndexOf(File.separator);
        		libFileName = fullPath.substring(index + 1);
        		System.out.println("Parsed " + libFileName + " successfully!");
    		}
    		
    		fullPath = args[0];
    		index = fullPath.lastIndexOf(File.separator);
    		progFileName = fullPath.substring(index + 1);
    		
    		
    		pp = new parser(new Lexer(new FileReader(args[0])));
    		result = pp.parse();
    		programNode = (ASTNode) result.value;
    		System.out.println("Parsed " + progFileName + " successfully!");
    		
    		// Add the Library AST to the list of class declarations
    		if (libraryProgramNode != null) { 
    			((Program) programNode).getClasses().add(0, ((Program) libraryProgramNode).getClasses().get(0));
    		}
    		    					
    		// Build the symbol table
            SymbolTableBuilder stb = new SymbolTableBuilder(progFileName);
            programNode.accept(stb);
			
            // Run semantic checks
			SemanticChecker sck = new SemanticChecker();
			programNode.accept(sck);
			
            // Build the type table builder
            TypeTabelBuilder ttb = new TypeTabelBuilder(progFileName); 
     		programNode.accept(ttb);
     		
            // Print the symbol table
            System.out.println();
            printSymbolTable(stb.getRootScope());

            // Print the Type table
            System.out.println();
     		System.out.println(ttb);
			
			    		
    	} catch (ParserException | SemanticException | LexicalError e) {
    		System.out.println(e.getMessage());
    		//System.exit(1);
    	} catch (IOException e) {
    		System.err.printf("IO Error:\n%s\n", e.getMessage());
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    
    }
    
    public static void printSymbolTable(FrameScope n) {
        if (n == null)
            return;
        
        System.out.print(n);
        for (FrameScope child : n.getChildren()) {
            System.out.println();
            printSymbolTable(child);
        }
    }
    
}
