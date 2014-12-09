package ic.semanticCheck;

import ic.ast.ArrayLocation;
import ic.ast.Assignment;
import ic.ast.BinaryOp;
import ic.ast.Break;
import ic.ast.CallStatement;
import ic.ast.Continue;
import ic.ast.Expression;
import ic.ast.ExpressionBlock;
import ic.ast.Field;
import ic.ast.Formal;
import ic.ast.ICClass;
import ic.ast.If;
import ic.ast.Length;
import ic.ast.LibraryMethod;
import ic.ast.Literal;
import ic.ast.LocalVariable;
import ic.ast.LogicalBinaryOp;
import ic.ast.LogicalUnaryOp;
import ic.ast.MathBinaryOp;
import ic.ast.MathUnaryOp;
import ic.ast.Method;
import ic.ast.NewArray;
import ic.ast.NewClass;
import ic.ast.PrimitiveType;
import ic.ast.Program;
import ic.ast.Return;
import ic.ast.Statement;
import ic.ast.StatementsBlock;
import ic.ast.StaticCall;
import ic.ast.StaticMethod;
import ic.ast.This;
import ic.ast.UnaryOp;
import ic.ast.UserType;
import ic.ast.VariableLocation;
import ic.ast.VirtualCall;
import ic.ast.VirtualMethod;
import ic.ast.Visitor;
import ic.ast.While;
import ic.semanticCheck.ScopeNode.ScopeType;

public class SymbolTableBuilder implements Visitor {

    private final ScopeNode rootScope;
    private ScopeNode currScope;
    
    public SymbolTableBuilder() {
        rootScope = new ScopeNode(ScopeType.Global, null, null);
        currScope = rootScope;
    }
    
    public ScopeNode getRootScope() {
        return rootScope;
    }
    
    @Override
    public Object visit(Program program) {
        program.scope = currScope;
        
        for (ICClass icClass : program.getClasses()) {
            icClass.accept(this);
        }
        
        return null;
    }

    @Override
    public Object visit(ICClass icClass) {
        // Find the proper enclosing (parent) scope for a class
        ScopeNode parentScope = rootScope;
        if (icClass.hasSuperClass()) {
        	ICClass sup = rootScope.getClass(icClass.getSuperClassName()); 
            if (sup == null)
                throw new SemanticException(icClass, "Class " + icClass.getName() + " cannot extend "
                                      + icClass.getSuperClassName() + ", since it's not yet defined");
            parentScope = sup.scope;
        }
        
        // begin a new Class scope for the fields and methods of the class
        currScope = currScope.addScope(ScopeType.Class, icClass.getName(), parentScope);        
        icClass.scope = currScope;

        // Add any class to the class list of the global scope
        rootScope.addClass(icClass);        
        
        for (Field field : icClass.getFields()) {
            field.accept(this);
        }
        
        for (Method method : icClass.getMethods()) {
            method.accept(this);        
        }
        
        // return to the global scope
        currScope = rootScope;
        
        return null;
    }

    @Override
    public Object visit(Field field) {
        field.scope = currScope;
        
        currScope.addField(field);
        
        return null;
    }

    private void visitMethod(Method method, ScopeType type) {
        currScope.addMethod(method);
        
        // create a new scope for the method
        currScope = currScope.addScope(type, method.getName(), currScope);        
        method.scope = currScope;     
        
        for (Formal formal : method.getFormals()) 
            formal.accept(this);
        
        for (Statement statement : method.getStatements())
            statement.accept(this);        
        
        // return to parent scope
        currScope = currScope.getParent();        
    }
    
    @Override
    public Object visit(VirtualMethod method) {
        visitMethod(method, ScopeType.Method);
        return null;
    }

    @Override
    public Object visit(StaticMethod method) {
        visitMethod(method, ScopeType.Method);
        return null;
    }

    @Override
    public Object visit(LibraryMethod method) {
        visitMethod(method, ScopeType.Method);
        return null;
    }

    @Override
    public Object visit(Formal formal) {
        formal.scope = currScope;
        
        currScope.addParameter(formal);
                
        return null;
    }

    @Override
    public Object visit(PrimitiveType type) {
        type.scope = currScope;

        return null;
    }

    @Override
    public Object visit(UserType type) {
        type.scope = currScope;
        
        return null;
    }

    @Override
    public Object visit(Assignment assignment) {
        assignment.scope = currScope;
        
        assignment.getVariable().accept(this);
        assignment.getAssignment().accept(this);
        
        return null;
    }

    @Override
    public Object visit(CallStatement callStatement) {
        callStatement.scope = currScope;
        
        callStatement.getCall().accept(this);

        return null;
    }

    @Override
    public Object visit(Return returnStatement) {
        returnStatement.scope = currScope;
        if (returnStatement.hasValue())
            returnStatement.getValue().accept(this);
        
        return null;
    }

    @Override
    public Object visit(If ifStatement) {
        ifStatement.scope = currScope;

        ifStatement.getCondition().accept(this);
        ifStatement.getOperation().accept(this);
        if (ifStatement.hasElse())
            ifStatement.getElseOperation().accept(this);
        
        return null;
    }

    @Override
    public Object visit(While whileStatement) {
        whileStatement.scope = currScope;
        
        whileStatement.getCondition().accept(this);
        whileStatement.getOperation().accept(this);        
        
        return null;
    }

    @Override
    public Object visit(Break breakStatement) {
        breakStatement.scope = currScope;
        return null;
    }

    @Override
    public Object visit(Continue continueStatement) {
        continueStatement.scope = currScope;
        return null;
    }

    @Override
    public Object visit(StatementsBlock statementsBlock) {
        String parentName = currScope.getName();
        if (parentName.charAt(0) == '@')
            parentName = parentName.substring(parentName.lastIndexOf('@')+1);

        currScope = currScope.addScope(ScopeType.StatementBlock, "@"+parentName, currScope);
        statementsBlock.scope = currScope;
        
        for (Statement statement : statementsBlock.getStatements())
            statement.accept(this);

        currScope = currScope.getParent();
        return null;
    }

    @Override
    public Object visit(LocalVariable localVariable) {
        localVariable.scope = currScope;
        
        currScope.addLocalVar(localVariable);
        localVariable.getType().accept(this);
        if (localVariable.getInitValue() != null)
            localVariable.getInitValue().accept(this);
            
        return null;
    }

    @Override
    public Object visit(VariableLocation location) {
    	if (location.getLocation() == null) {
    		location.scope = currScope;
            return null;
    	} else {
    		location.scope = currScope;
            location.getLocation().accept(this);
            return null;
    	}
        
    }


    @Override
    public Object visit(ArrayLocation location) {
        location.scope = currScope;
        location.getArray().accept(this);
        location.getIndex().accept(this);        
        return null;
    }

    @Override
    public Object visit(StaticCall call) {
        call.scope = currScope;
        for (Expression argument : call.getArguments())
            argument.accept(this);        
        
        return null;
    }

    @Override
    public Object visit(VirtualCall call) {
        call.scope = currScope;
        
        if (call.isExternal()) 
            call.getLocation().accept(this);

        for (Expression argument : call.getArguments())
            argument.accept(this);        

        return null;
    }

    @Override
    public Object visit(This thisExpression) {
        thisExpression.scope = currScope;
        return null;
    }

    @Override
    public Object visit(NewClass newClass) {
        newClass.scope = currScope;
        return null;
    }

    @Override
    public Object visit(NewArray newArray) {
        newArray.scope = currScope;
        newArray.getType().accept(this);
        newArray.getSize().accept(this);
        return null;
    }

    @Override
    public Object visit(Length length) {
        length.scope = currScope;
        length.getArray().accept(this);
        return null;
    }

    @Override
    public Object visit(Literal literal) {
        literal.scope = currScope;

        return null;
    }

    @Override
    public Object visit(MathUnaryOp unaryOp) {
        unaryOp.scope = currScope;
        unaryOp.getOperand().accept(this);        
        return null;
    }
    
    public Object visit(LogicalUnaryOp unaryOp) {
        unaryOp.scope = currScope;
        unaryOp.getOperand().accept(this);        
        return null;
    }

    @Override
    public Object visit(MathBinaryOp binaryOp) {
        binaryOp.scope = currScope;
        
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this);        

        return null;
    }
    
    @Override
    public Object visit(LogicalBinaryOp binaryOp) {
        binaryOp.scope = currScope;
        
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this);        

        return null;
    }

	@Override
	public Object visit(ExpressionBlock expressionBlock) {
		// TODO Auto-generated method stub
		return null;
	}

}
