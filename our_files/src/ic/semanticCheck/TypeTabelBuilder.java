package ic.semanticCheck;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.type.ArrayType;

import ic.DataTypes;
import ic.LiteralTypes;
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
import ic.ast.Type;
import ic.ast.UnaryOp;
import ic.ast.UserType;
import ic.ast.VariableLocation;
import ic.ast.VirtualCall;
import ic.ast.VirtualMethod;
import ic.ast.Visitor;
import ic.ast.While;
import ic.semanticCheck.ScopeNode.ScopeType;

public class TypeTabelBuilder implements Visitor{
	
	private HashMap<Integer, Type> primitive;
	private HashMap<Integer, Type> arrayType;
	private HashMap<Integer, ICClass> classes;
	private HashMap<Integer, Method> methods;
	private int id=1;
	String name;
	
	public TypeTabelBuilder()
	{
		primitive = new LinkedHashMap<Integer, Type>();
		arrayType = new LinkedHashMap<Integer, Type>();
		classes = new LinkedHashMap<Integer, ICClass>();
		methods = new LinkedHashMap<Integer, Method>();
	}
	public TypeTabelBuilder(String name)
	{
		primitive = new LinkedHashMap<Integer, Type>();
		arrayType = new LinkedHashMap<Integer, Type>();
		classes = new LinkedHashMap<Integer, ICClass>();
		methods = new LinkedHashMap<Integer, Method>();
		this.name = name;
	}
	
    @Override
    public Object visit(Program program) {
        for (ICClass icClass : program.getClasses()) {
            icClass.accept(this);
        }
        return null;
    }
    
    @Override
    public Object visit(ICClass icClass) {
    	
        for (Field field : icClass.getFields()) {
            field.accept(this);
        }
        
        for (Method method : icClass.getMethods()) {
            method.accept(this);        
        }
    	if(!classes.containsValue(icClass))
    		classes.put(id++, icClass);
    	
        return null;
    }
    
    @Override
    public Object visit(Field field) {
    	addParm(field.getType());
        return null;
    }
    
    private void visitMethod(Method method) {

    	addParm(method.getType());
        for (Formal formal : method.getFormals()) 
            formal.accept(this);
        
        for (Statement statement : method.getStatements())
            statement.accept(this);
        
    	if(!containsMethod(method))
    		methods.put(id++,method);
    }
    
    @Override
    public Object visit(VirtualMethod method) {
        visitMethod(method);
        return null;
    }

    @Override
    public Object visit(StaticMethod method) {
        visitMethod(method);
        return null;
    }
    @Override
    public Object visit(LibraryMethod method) {
        visitMethod(method);
        return null;
    }
	@Override
	public Object visit(Formal formal) {
		addParm(formal.getType());
		return null;
	}
	@Override
	public Object visit(PrimitiveType type) {
		addParm(type);
		return null;
	}
	@Override
	public Object visit(UserType type) {
		addParm(type);
		return null;
	}
	@Override
	public Object visit(Assignment assignment) {
        assignment.getVariable().accept(this);
        assignment.getAssignment().accept(this);
		return null;
	}
	@Override
	public Object visit(CallStatement callStatement) {
		callStatement.getCall().accept(this);
		return null;
	}
	@Override
	public Object visit(Return returnStatement) {
        if (returnStatement.hasValue())
            returnStatement.getValue().accept(this);
		return null;
	}
	@Override
	public Object visit(If ifStatement) {
        ifStatement.getCondition().accept(this);
        ifStatement.getOperation().accept(this);
        if (ifStatement.hasElse())
            ifStatement.getElseOperation().accept(this);
		return null;
	}
	@Override
	public Object visit(While whileStatement) {
        whileStatement.getCondition().accept(this);
        whileStatement.getOperation().accept(this);     
		return null;
	}
	@Override
	public Object visit(Break breakStatement) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(Continue continueStatement) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(StatementsBlock statementsBlock) {
        for (Statement statement : statementsBlock.getStatements())
            statement.accept(this);
		return null;
	}
	@Override
	public Object visit(LocalVariable localVariable) {
        localVariable.getType().accept(this);
        if (localVariable.getInitValue() != null)
            localVariable.getInitValue().accept(this);
        addParm(localVariable.getType());
		return null;
	}
	@Override
	public Object visit(VariableLocation location) {
    	if (location.getLocation() == null)
            return null;
    	 else
            location.getLocation().accept(this);
         return null;
	}
	@Override
	public Object visit(ArrayLocation location) {
        location.getArray().accept(this);
        location.getIndex().accept(this);        
		return null;
	}
	@Override
	public Object visit(StaticCall call) {
        for (Expression argument : call.getArguments())
            argument.accept(this);    
		return null;
	}
	@Override
	public Object visit(VirtualCall call) {
        if (call.isExternal()) 
            call.getLocation().accept(this);
        for (Expression argument : call.getArguments())
            argument.accept(this);
		return null;
	}
	@Override
	public Object visit(This thisExpression) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(NewClass newClass) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object visit(NewArray newArray) {
		addParm(newArray.getType());
        newArray.getType().accept(this);
        newArray.getSize().accept(this);
		return null;
	}
	@Override
	public Object visit(Length length) {
		length.getArray().accept(this);
		return null;
	}
	@Override
	public Object visit(MathBinaryOp binaryOp) {
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this);
		return null;
	}
	@Override
	public Object visit(LogicalBinaryOp binaryOp) {
        binaryOp.getFirstOperand().accept(this);
        binaryOp.getSecondOperand().accept(this);  
		return null;
	}
	@Override
	public Object visit(MathUnaryOp unaryOp) {
		unaryOp.getOperand().accept(this);
		return null;
	}
	@Override
	public Object visit(LogicalUnaryOp unaryOp) {
		unaryOp.getOperand().accept(this);
		return null;
	}
	@Override
	public Object visit(Literal literal) {
		
		LiteralTypes literalType = literal.getType();
		PrimitiveType ret = null;
		switch(literalType.getDescription()) {
			case ("Boolean literal") : ret = new PrimitiveType(literal.getLine(), DataTypes.BOOLEAN);
				break;
			case ("Integer literal") : ret = new PrimitiveType(literal.getLine(), DataTypes.INT);
				break;
			case ("String literal") : ret = new PrimitiveType(literal.getLine(), DataTypes.STRING);
				break;
			case ("Literal") : ret = new PrimitiveType(literal.getLine(), DataTypes.NULL);
				break;	
		}
		addParm(ret);
		return null;
	}
	@Override
	public Object visit(ExpressionBlock expressionBlock) {
		// TODO Auto-generated method stub
		return null;
	}
	private void addParm(Type type) {
		if(type.getDimension() == 0  && type instanceof PrimitiveType){
			if(!containsPrimitive(type))
				primitive.put(id++,type);
		}
		if(!containsArrayType(type)){
			for(int i=0; i<type.getDimension(); i++){
				UserType tmp = new UserType(type.getLine(), type.getName());
				for(int j=0; j<=i; j++)
					tmp.incrementDimension();
				arrayType.put((id++),tmp);
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Type Table: "+name+"\n");

		// primitive
		for (Map.Entry<Integer, Type> entry : primitive.entrySet()) {
			sb.append("    "+entry.getKey()+": "+"Primitive type: ");
			sb.append(formatType(entry.getValue()));
			sb.append("\n");
		}
		
		// Classes
		for (Map.Entry<Integer, ICClass> entry : classes.entrySet()) {
			sb.append("    "+entry.getKey()+": "+"Class: ");
			sb.append(entry.getValue().getName());
			if(entry.getValue().getSuperClassName() != null){
				sb.append(", Superclass ID: " + getSuperClassId(entry.getValue()));
			}
			sb.append("\n");
		}
		
		// arrayType
		for (Map.Entry<Integer, Type> entry : arrayType.entrySet()) {
			sb.append("    "+entry.getKey()+": "+"Array type: ");
			sb.append(formatType(entry.getValue()));
			sb.append("\n");
		}

		// Methods
		for (Map.Entry<Integer, Method> entry : methods.entrySet()) {
			sb.append("    "+entry.getKey()+": "+"Method type: ");
			sb.append("{");
			sb.append(formatSig(entry.getValue()));
			sb.append("}");
			sb.append("\n");
		}

		return sb.toString();
	}
	
	private int getSuperClassId(ICClass icClass){
		for (Map.Entry<Integer, ICClass> entry : classes.entrySet()) {
			if(icClass.getSuperClassName().equals(entry.getValue().getName()))
				return entry.getKey();
		}
		return 0;
	}
	
	private boolean typesEqual(Type t1, Type t2) {
		if (t1.getDimension() != t2.getDimension())
			return false;

		if (!t1.getName().equals(t2.getName()))
			return false;

		return true;
	}

	private boolean methodSigEqual(Method m1, Method m2) {
		List<Formal> f1 = m1.getFormals();
		List<Formal> f2 = m2.getFormals();

		if (f1.size() != f2.size())
			return false;

		if (!typesEqual(m1.getType(), m2.getType()))
			return false;

		for (int i = 0; i < f1.size(); i++) {
			if (!typesEqual(f1.get(i).getType(), f2.get(i).getType()))
				return false;
		}

		return true;
	}
	
	private boolean containsPrimitive(Type type) {
		for (Map.Entry<Integer, Type> entry : primitive.entrySet()) {
			if(typesEqual(type,entry.getValue()))
				return true;
		}
		return false;
	}
	private boolean containsArrayType(Type type) {
		for (Map.Entry<Integer, Type> entry : arrayType.entrySet()) {
			if(typesEqual(type,entry.getValue()))
				return true;
		}
		return false;
	}
	private boolean containsMethod(Method method) {
		for (Map.Entry<Integer, Method> entry : methods.entrySet()) {
			if(methodSigEqual(method,entry.getValue()))
				return true;
		}
		return false;
	}
	
	
	private String formatType(Type t) {
		StringBuilder sb = new StringBuilder();

		sb.append(t.getName());
		for (int i = 0; i < t.getDimension(); i++)
			sb.append("[]");

		return sb.toString();
	}

	private String formatSig(Method m) {
		StringBuilder sb = new StringBuilder();

		String delim = "";
		for (Formal f : m.getFormals()) {
			sb.append(delim);
			sb.append(formatType(f.getType()));
			delim = ", ";
		}

		sb.append(" -> ");
		sb.append(formatType(m.getType()));

		return sb.toString();
	}

}
