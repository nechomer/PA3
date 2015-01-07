package ic.semanticCheck;

public class SemanticException extends Error  {
    private String msg;
    
    public SemanticException(String message) {
        msg = message;
    }
    
    public SemanticException(ic.ast.ASTNode node, String message) {
        msg = node.getLine() + ": semantic error; " + message;
    }
    
    @Override
    public String getMessage() {
        return msg;        
    }
}
