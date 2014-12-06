import ic.ast.Node;
import ic.ast.decl.DeclStaticMethod;
import ic.interp.*;
import ic.lexer.Lexer;
import ic.lexer.LexerException;
import ic.lexer.Token;
import ic.parser.Parser;
import ic.parser.ParserException;
import ic.sem.ScopeNode;
import ic.sem.SemanticChecker;
import ic.sem.SemanticException;
import ic.sem.SymbolTableBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @team TheCunningLinguists <velbaumm@mail.tau.ac.il>
 * 1. Stanislav Podolsky
 * 2. Artyom Lukianov
 * 3. Michael Velbaum
 */

public class Main {

    public static void main(String[] args) {
        List<Token> tokens = new ArrayList<Token>();
        List<Token> libtokens = new ArrayList<Token>();
        DeclStaticMethod method;

        try {
            // Lexical analysis
            Lexer.process(new FileReader(args[0]), tokens);
            if (args.length > 1 && args[1].startsWith("-L")){
                Lexer.process(new FileReader(args[1].substring(2)), libtokens);
            }
            
            // Syntax Analysis
            Node progAst = null, libAst = null;            
            if (args.length > 1 && args[1].startsWith("-L")) {
                libAst = Parser.processLibrary(libtokens);
            }
            progAst = Parser.processProgram(tokens, libAst);

            // Run interpreter
            if (args.length > 1 && !args[1].startsWith("-L")) {
                if (args.length > 1) {
                    String[] methodArgs = null;
                    if (args.length > 2) {
                        methodArgs = Arrays.copyOfRange(args, 2, args.length);
                    }
                    try {
                        method = (DeclStaticMethod) Interpreter.locateMethod(progAst, args[1], methodArgs);
                        Interpreter inter = new Interpreter(methodArgs);
                        method.accept(inter);
                    } catch (Interpreter.RuntimeError e) {
                        System.out.println(e.getMessage());
                        return;
                    } catch (Interpreter.ReturnSuccess e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                }
            } else {
                if (args.length > 2) {
                    String[] methodArgs = null;
                    if (args.length > 3) {
                        methodArgs = Arrays.copyOfRange(args, 3, args.length);
                    }
                    try {
                        method = (DeclStaticMethod) Interpreter.locateMethod(progAst, args[2], methodArgs);
                        Interpreter inter = new Interpreter(methodArgs);
                        method.accept(inter);
                    } catch (Interpreter.RuntimeError e) {
                        System.out.println(e.getMessage());
                        return;
                    } catch (Interpreter.ReturnSuccess e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                }
            }
            
            // Build the symbol table
            SymbolTableBuilder stb = new SymbolTableBuilder();
            progAst.accept(stb);

			// Run semantic checks
			SemanticChecker sck = new SemanticChecker();
			progAst.accept(sck);

            // Print the symbol table
            System.out.println();
            printSymbolTable(stb.getRootScope());
        } catch (LexerException | ParserException | SemanticException | IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }       
    }
    
    public static void printSymbolTable(ScopeNode n) {
        if (n == null)
            return;
        
        System.out.print(n);
        for (ScopeNode child : n.getChildren()) {
            System.out.println();
            printSymbolTable(child);
        }
    }
}