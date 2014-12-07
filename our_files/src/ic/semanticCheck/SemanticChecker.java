package ic.semanticCheck;

import java.util.Stack;

import ic.DataTypes;
import ic.ast.ASTNode;
import ic.ast.Field;
import ic.ast.Formal;
import ic.ast.ICClass;
import ic.ast.Program;
import ic.ast.Visitor;
import ic.ast.expr.ArrayLocation;
import ic.ast.expr.BinaryOp;
import ic.ast.expr.Length;
import ic.ast.expr.Literal;
import ic.ast.expr.NewArray;
import ic.ast.expr.NewClass;
import ic.ast.expr.StaticCall;
import ic.ast.expr.This;
import ic.ast.expr.UnaryOp;
import ic.ast.expr.VariableLocation;
import ic.ast.expr.VirtualCall;
import ic.ast.methods.LibraryMethod;
import ic.ast.methods.Method;
import ic.ast.methods.StaticMethod;
import ic.ast.methods.VirtualMethod;
import ic.ast.stmt.Assignment;
import ic.ast.stmt.Break;
import ic.ast.stmt.CallStatement;
import ic.ast.stmt.Continue;
import ic.ast.stmt.If;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Return;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StatementsBlock;
import ic.ast.stmt.While;
import ic.ast.types.PrimitiveType;
import ic.ast.types.Type;
import ic.ast.types.UserType;
import ic.semanticCheck.ScopeNode.ScopeType;

public class SemanticChecker implements Visitor {

	private boolean static_scope, hasReturn, isLibrary;
	private Stack<Boolean> loop, cond_block;
	private Type currMethodType;

	public SemanticChecker() {
		// TODO Auto-generated constructor stub
		loop = new Stack<>();
		cond_block = new Stack<>();
		this.isLibrary = false;
	}

	@Override
	public Object visit(Program program) {
		int main_cnt = 0;
		for (ICClass c : program.getClasses()) {
			if (c.getName().equals("Library")) {
				this.isLibrary = true;
			}
			c.accept(this);
			this.isLibrary = false;
			Method m = c.scope.getMethod("main");
			if (m == null) {
				continue;
			} else {
				if (main_cnt > 0) {
					throw new SemanticException(m,
							" Found more than one main in the file");
				}
				if (!m.getType().getName().equals("void")) {
					throw new SemanticException(m,
							" Main method should have 'void' return type");
				}
				Type args = m.scope.getParameter("args");
				if (args == null) {
					throw new SemanticException(m,
							" Argument for main method should be 'string[] args'");
				} else if (!args.getName().equals("string")) {
					throw new SemanticException(m,
							" Argument for main method should be 'string[] args'");
				} else if (args.getDimension() != 1) {
					throw new SemanticException(m,
							" Argument for main method should be 'string[] args'");
				}
				main_cnt++;
			}
		}
		return null;
	}

	@Override
	public Object visit(ICClass icClass) {
		for (Method m : icClass.getMethods()) {
			m.accept(this);
		}
		for (Field f : icClass.getFields()) {
			f.accept(this);
		}

		return null;
	}

	@Override
	public Object visit(Field field) {
		if (field.getType() instanceof PrimitiveType) {
			return null;
		}
		Object c = field.scope.lookupId(field.getType().getName());
		if (c == null) {
			throw new SemanticException(field, field.getType().getName()
					+ " not found in type table");
		}
		return null;
	}

	@Override
	public Object visit(VirtualMethod method) {
		this.static_scope = false;
		this.currMethodType = method.getType();
		this.hasReturn = false;
		if (method.getType().getName().equals("void")) {
			this.hasReturn = true;
		}
		for (Statement s : method.getStatements()) {
			s.accept(this);
		}
		checkParams(method);
		if (!this.hasReturn) {
			throw new SemanticException(method,
					" no return statement in non void method");
		}
		return null;
	}

	@Override
	public Object visit(StaticMethod method) {
		this.static_scope = true;
		this.currMethodType = method.getType();
		for (Statement s : method.getStatements()) {
			s.accept(this);
		}
		checkParams(method);
		return null;
	}

	@Override
	public Object visit(LibraryMethod method) {
		if (!this.isLibrary) {
			throw new SemanticException(method,
					" Library methods should be defined only in Library class");
		}
		this.static_scope = true;
		this.currMethodType = method.getType();
		for (Statement s : method.getStatements()) {
			s.accept(this);
		}
		checkParams(method);
		return null;
	}

	@Override
	public Object visit(Formal formal) {
		return formal.getType();
	}

	@Override
	public Object visit(PrimitiveType type) {
		return type;
	}

	@Override
	public Object visit(UserType type) {
		return type;
	}

	@Override
	public Object visit(Assignment assignment) {
		Type a = (Type) assignment.getVariable().accept(this);
		Type b = (Type) assignment.getAssignment().accept(this);

		checkAssignment(a, b, assignment);

		return a;
	}

	@Override
	public Object visit(CallStatement callStatement) {
		return callStatement.getCall().accept(this);

	}

	@Override
	public Object visit(Return returnStatement) {
		// TODO compare return type and method type
		if (returnStatement.hasValue()) {
			Type t = (Type) returnStatement.getValue().accept(this);
			if (this.currMethodType.getName().equals(t.getName())) {
				if (this.loop.empty() || !this.loop.peek().booleanValue())
					this.hasReturn = true;
				return this.currMethodType;
			} else
				throw new SemanticException(returnStatement,
						"Return statement is not of type "
								+ this.currMethodType.getName());
		} else {
			if (this.currMethodType.getName().equals("void")) {
				if (this.loop.empty() || !this.loop.peek().booleanValue())
					this.hasReturn = true;
				return this.currMethodType;
			} else
				throw new SemanticException(returnStatement,
						"Return statement is not of type "
								+ this.currMethodType.getName());

		}
	}

	@Override
	public Object visit(If ifStatement) {
		// TODO check that every branch has return!
		Type cond = (Type) ifStatement.getCondition().accept(this);
		if (!cond.getName().equals("boolean")) {
			throw new SemanticException(ifStatement,
					"Non boolean condition for if statement");
		}
		this.cond_block.push(true);
		ifStatement.getOperation().accept(this);
		this.cond_block.pop();
		if (ifStatement.hasElse()) {
			ifStatement.getElseOperation().accept(this);
		}
		return null;
	}

	@Override
	public Object visit(While whileStatement) {
		Type cond = (Type) whileStatement.getCondition().accept(this);
		if (cond == null) {
			return null;
		}
		if (!cond.getName().equals("boolean")) {
			throw new SemanticException(whileStatement,
					"Non boolean condition for while statement");
		}
		this.loop.push(true);
		if (whileStatement.getOperation() != null) {
			whileStatement.getOperation().accept(this);
		}
		this.loop.pop();
		return null;
	}

	@Override
	public Object visit(Break breakStatement) {
		// TODO Auto-generated method stub
		if (this.loop.empty() || !this.loop.peek().booleanValue())
			throw new SemanticException(breakStatement,
					"Use of 'break' statement outside of loop not allowed");
		return null;
	}

	@Override
	public Object visit(Continue continueStatement) {
		// TODO Auto-generated method stub
		if (this.loop.empty() || !this.loop.peek().booleanValue())
			throw new SemanticException(continueStatement,
					"Use of 'continue' statement outside of loop not allowed");
		return null;
	}

	@Override
	public Object visit(StatementsBlock statementsBlock) {
		// TODO Auto-generated method stub
		for (Statement s : statementsBlock.getStatements()) {
			s.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		// TODO Auto-generated method stub
		if (localVariable.isInitialized()) {
			Type init = (Type) localVariable.getInitialValue().accept(this);
			if (init == null) {
				return null;
			}
			checkAssignment(localVariable.getType(), init, localVariable);
		}
		return localVariable.getType();
	}

	@Override
	public Object visit(VariableLocation location) {
		// TODO Auto-generated method stub
		Object variable = location.scope.lookupId(location.getName());
		if (variable == null) {
			throw new SemanticException(location, location.getName()
					+ " not found in symbol table");
		} else if (variable instanceof Field) {
			if (static_scope == true)
				throw new SemanticException(location,
						"Use of field inside static method is not allowed");
			return ((Field) variable).getType();
		}

		return (Type) variable;
	}

	@Override
	public Object visit(Field location) {
		// TODO Auto-generated method stub
		Type ctype = (Type) location.getObject().accept(this);
		DeclClass c = (DeclClass) location.scope.lookupId(ctype
				.getName());
		DeclField field = c.scope.getField(location.getField());
		if (field == null) {
			throw new SemanticException(location, location.getField()
					+ " doesn't exist in " + c.getName());
		}
		return field.getType();
	}

	@Override
	public Object visit(ArrayLocation location) {
		// TODO Auto-generated method stub
		Object type = location.getIndex().accept(this);
		if (type instanceof String) {
			throw new SemanticException(location, " index should be integer");
		} else if (type instanceof PrimitiveType) {
			PrimitiveType ptype = (PrimitiveType) type;
			if (!ptype.getName().equals("int")) {
				throw new SemanticException(location,
						" index should be integer");
			}
		}
		return location.getArray().accept(this);
	}

	@Override
	public Object visit(StaticCall call) {

		Object c = call.scope.lookupId(call.getClassName());
		if (!(c instanceof ICClass)) {
			throw new SemanticException(call, call.getClassName()
					+ " class doesn't exist");
		}
		Method method = ((ICClass) c).scope.getMethod(call.getMethod());
		if (method == null) {
			throw new SemanticException(call, "Method " + call.getMethod()
					+ " doesn't exist");
		}
		if (call.getArguments().size() != method.getFormals().size()) {
			throw new SemanticException(call,
					"Invalid number of arguments for "
							+ ((ICClass) c).getName() + "."
							+ call.getMethod());
		}
		if (method instanceof VirtualMethod)
			throw new SemanticException(call, " called method isn't static");
		for (int i = 0; i < call.getArguments().size(); i++) {
			Type t = (Type) call.getArguments().get(i).accept(this);
			Type formal = method.getFormals().get(i).getType();
			if (!t.getName().equals(formal.getName())) {
				if (formal instanceof UserType && t instanceof UserType) {
					ICClass classA = (ICClass) call.scope.lookupId(t
							.getName());
					ICClass classB = (ICClass) call.scope.lookupId(formal
							.getName());
					if (!isSubClass(classB.scope, classA.scope)) {
						throw new SemanticException(call, "Method "
								+ ((ICClass) c).getName() + "."
								+ call.getMethod()
								+ " is not applicable for the arguments given");
					}
				} else {

					throw new SemanticException(call, "Method "
							+ ((ICClass) c).getName() + "."
							+ call.getMethod()
							+ " is not applicable for the arguments given");
				}
			}
		}
		return method.getType();
	}

	@Override
	public Object visit(VirtualCall call) {
		Object m = null;
		String class_name = null;
		if (call.getObject() == null) {
			m = call.scope.lookupId(call.getMethod());
			class_name = lookupClassScopeName(call.scope);
			if (m == null || !(m instanceof Method)) {
				throw new SemanticException(call, call.getMethod()
						+ " not found in symbol table");
			}
			if (this.static_scope == true && m instanceof VirtualMethod) {

				throw new SemanticException(call,
						" Calling a local virtual method from inside a static method is not allowed");
			}

		} else {
			Type class_type = (Type) call.getObject().accept(this);
			if (class_type instanceof PrimitiveType) {
				throw new SemanticException(call,
						" Primitive type has no methods");
			}
			Object c = call.scope.lookupId(class_type.getName());
			m = ((ICClass) c).scope.lookupId(call.getMethod());
			class_name = ((ICClass) c).getName();
			if (m == null || !(m instanceof Method)) {
				throw new SemanticException(call, "Method " + class_name + "."
						+ call.getMethod() + " not found in type table");
			}

		}
		if (call.getArguments().size() != ((Method) m).getFormals().size()) {
			throw new SemanticException(call,
					"Invalid number of arguments for method "
							+ call.getMethod());
		}

		for (int i = 0; i < call.getArguments().size(); i++) {
			Type t = (Type) call.getArguments().get(i).accept(this);
			Type formal = ((Method) m).getFormals().get(i).getType();

			if (!formal.getName().equals(t.getName())) {
				if (formal instanceof UserType && t instanceof UserType) {
					ICClass classA = (ICClass) call.scope.lookupId(t
							.getName());
					ICClass classB = (ICClass) call.scope.lookupId(formal
							.getName());
					if (!isSubClass(classB.scope, classA.scope)) {
						throw new SemanticException(call, "Method "
								+ class_name + "." + call.getMethod()
								+ " is not applicable for the arguments given");
					}
				} else {
					throw new SemanticException(call, "Method " + class_name
							+ "." + call.getMethod()
							+ " is not applicable for the arguments given");
				}
			}
		}
		return ((Method) m).getType();
	}

	@Override
	public Object visit(This thisExpression) {
		if (this.static_scope == true) {
			throw new SemanticException(thisExpression,
					" Use of 'this' expression inside static method is not allowed");
		}
		return new UserType(thisExpression.getLine(),
				lookupClassScopeName(thisExpression.scope));
	}

	@Override
	public Object visit(NewClass newClass) {
		// TODO Auto-generated method stub
		Object c = newClass.scope.lookupId(newClass.getName());
		if (c == null) {
			throw new SemanticException(newClass, newClass.getName()
					+ " not found in symbol table");
		}
		return new ClassType(newClass.getLine(), newClass.getName());
	}

	@Override
	public Object visit(NewArray newArray) {
		// TODO Auto-generated method stub
		Type size = (Type) newArray.getSize().accept(this);
		if (!size.getName().equals("int")) {
			throw new SemanticException(newArray, " size should be int");
		}
		return newArray.getType();
	}

	@Override
	public Object visit(Length length) {
		// TODO Auto-generated method stub
		length.getArray().accept(this);
		return new PrimitiveType(length.getLine(), DataType.INT);
	}

	@Override
	public Object visit(Literal literal) {
		// TODO Auto-generated method stub
		return new PrimitiveType(literal.getLine(), literal.getType());
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		// TODO Auto-generated method stub
		Type operand = (Type) unaryOp.getOperand().accept(this);
		switch (operand.getName()) {
		case "int":
			if (!unaryOp.getOperator().getDescription().equals("negate")) {
				throw new SemanticException(unaryOp, " type mismatch");
			}
			return operand;
		case "boolean":
			if (!unaryOp.getOperator().getDescription().equals("logical not")) {
				throw new SemanticException(unaryOp, " type mismatch");
			}
			return operand;
		default:
			throw new SemanticException(unaryOp, " type mismatch");
		}
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		Type a = (Type) binaryOp.getFirstOperand().accept(this);
		Type b = (Type) binaryOp.getSecondOperand().accept(this);
		switch (binaryOp.getOperator().getDescription()) {
		case "add":
			if (a.getName().equals("int")
					&& b.getName().equals("int")) {
				return a;
			} else if (a.getName().equals("string")
					&& b.getName().equals("string")) {
				return a;
			} else {
				throw new SemanticException(binaryOp, "Type mismatch: "
						+ a.getName() + " " + binaryOp.getOperator()
						+ " " + b.getName());
			}
		case "subtract":
		case "multiply":
		case "divide":
		case "modulo":
			if (a.getName().equals("int")
					&& b.getName().equals("int")) {
				return a;
			} else {
				throw new SemanticException(binaryOp, "Type mismatch: "
						+ a.getName() + " " + binaryOp.getOperator()
						+ " " + b.getName());
			}
		case "logical and":
		case "logical or":
			if (a.getName().equals("boolean")
					&& b.getName().equals("boolean")) {
				return a;
			} else {
				throw new SemanticException(binaryOp, "Type mismatch: "
						+ a.getName() + " " + binaryOp.getOperator()
						+ " " + b.getName());
			}
		case "less than":
		case "less than or equal to":
		case "greater than":
		case "greater than or equal to":
			if (a.getName().equals("int")
					&& b.getName().equals("int")) {
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);

			} else {
				throw new SemanticException(binaryOp,
						"Invalid logical binary op (" + binaryOp.getOperator()
								+ ") on non-integer expression");
			}
		case "equals":
		case "not equals":
			if (a.getName().equals(b.getName())) {
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);
			} else if ((a.getName().equals("void") && b instanceof UserType)
					|| (b.getName().equals("void") && a instanceof UserType)) {
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);
			} else if (b instanceof UserType && a instanceof UserType) {
				ICClass classA = (ICClass) binaryOp.scope.lookupId(a
						.getName());
				ICClass classB = (ICClass) binaryOp.scope.lookupId(b
						.getName());
				if (!isSubClass(classA.scope, classB.scope)
						&& !isSubClass(classB.scope, classA.scope)) {
					throw new SemanticException(binaryOp, "Type mismatch: "
							+ a.getName() + " " + binaryOp.getOperator()
							+ " " + b.getName());
				}
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);
			} else {
				throw new SemanticException(binaryOp, "Type mismatch: "
						+ a.getName() + " " + binaryOp.getOperator()
						+ " " + b.getName());
			}
		}
		return null;
	}

	private String lookupClassScopeName(ScopeNode node) {
		while (node.getType() != ScopeType.Class) {
			node = node.getParent();
		}
		return node.getName();

	}

	private boolean isSubClass(ScopeNode classA, ScopeNode subClassA) {
		if (classA.getName().equals(subClassA.getName()))
			return true;
		while (true) {
			ScopeNode eClass = subClassA.getParent();
			if (eClass.getType() == ScopeType.Global) {
				break;
			}
			if (eClass.getName().equals(classA.getName())) {
				return true;
			}
		}
		return false;
	}

	private void checkParams(Method method) {
		for (Formal f : method.getFormals()) {
			if (f.getType() instanceof PrimitiveType) {
				continue;
			}
			Object c = method.scope.lookupId(f.getType().getName());
			if (c == null) {
				throw new SemanticException(method, f.getType()
						.getName() + " not found in type table");
			}
		}
	}

	private void checkAssignment(Type a, Type b, ASTNode assignment) {
		if (!a.getName().equals(b.getName())) {
			if (a instanceof UserType && b instanceof UserType) {
				ICClass classA = (ICClass) assignment.scope.lookupId(a
						.getName());
				ICClass classB = (ICClass) assignment.scope.lookupId(b
						.getName());
				if (isSubClass(classA.scope, classB.scope)) {
					return;
				}
			} else if (a instanceof UserType && b instanceof PrimitiveType) {
				if (b.getName().equals("void")) {
					return;
				}
			} else if (a instanceof PrimitiveType && a.getDimension() > 0) {
				if (b.getName().equals("void")) {
					return;
				}
			}
			throw new SemanticException(assignment,
					"Invalid assignment of type " + b.getName()
							+ " to variable of type " + a.getName());
		}

	}

}
