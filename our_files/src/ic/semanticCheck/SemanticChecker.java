package ic.semanticCheck;

import java.util.Stack;

import ic.DataTypes;
import ic.LiteralTypes;
import ic.ast.*;
import ic.semanticCheck.ScopeNode.ScopeType;

public class SemanticChecker implements Visitor {

	// TODO comment on fields
	private boolean static_scope, hasReturn, isLibrary;
	private Stack<Boolean> loop, cond_block;
	private Type currMethodType;

	public SemanticChecker() {
		loop = new Stack<>();
		cond_block = new Stack<>();
		this.isLibrary = false;
	}

	/**
	 * @param program
	 * @return
	 */
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

	/**
	 * @param icClass
	 * @return
	 */
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

	/**
	 * @param field
	 * @return
	 */
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

	/**
	 * @param method
	 * @return
	 */
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

	/**
	 * @param method
	 * @return
	 */
	@Override
	public Object visit(StaticMethod method) {
		this.static_scope = true;
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

	/**
	 * This function is intended for library method checking - Verifies all parameters 
	 * in the method declaration are predefined or primitive. Since library methods return a
	 * primitive/void, the return type isn't checks since it was verified by the parser.
	 * 
	 * @param method - A library method to be checked
	 * @return - null
	 */
	@Override
	public Object visit(LibraryMethod method) {
		if (!this.isLibrary) {
			throw new SemanticException(method,
					" Library methods should be defined only in Library class");
		}
		this.static_scope = true;
		this.currMethodType = method.getType();
		//TODO: Check if necessary!!!
//		for (Statement s : method.getStatements()) {
//			s.accept(this);
//		}
		checkParams(method);
		return null;
	}

	/**
	 * @param formal
	 * @return
	 */
	@Override
	public Object visit(Formal formal) {
		return formal.getType();
	}

	/**
	 * @param type
	 * @return
	 */
	@Override
	public Object visit(PrimitiveType type) {
		return type;
	}

	/**
	 * @param type
	 * @return
	 */
	@Override
	public Object visit(UserType type) {
		return type;
	}

	/**
	 * @param assignment
	 * @return
	 */
	@Override
	public Object visit(Assignment assignment) {
		Type a = (Type) assignment.getVariable().accept(this);
		Type b = (Type) assignment.getAssignment().accept(this);

		checkAssignment(a, b, assignment);

		return a;
	}

	/**
	 * @param callStatement
	 * @return
	 */
	@Override
	public Object visit(CallStatement callStatement) {
		return callStatement.getCall().accept(this);

	}

	/**
	 * @param returnStatement
	 * @return
	 */
	@Override
	public Object visit(Return returnStatement) {
		// TODO compare return type and method type
		if (returnStatement.hasValue()) {
			Type t = (Type) returnStatement.getValue().accept(this);
			if (this.currMethodType.getName().equals(t.getName())) {
				if (this.cond_block.empty() || this.cond_block.peek().booleanValue())
					this.hasReturn = true;
				return this.currMethodType;
			} else
				throw new SemanticException(returnStatement,
						"Return statement is not of type "
								+ this.currMethodType.getName());
		} else {
			if (this.currMethodType.getName().equals("void")) {
				if (this.cond_block.empty() || this.cond_block.peek().booleanValue())
					this.hasReturn = true;
				return this.currMethodType;
			} else
				throw new SemanticException(returnStatement,
						"Return statement is not of type "
								+ this.currMethodType.getName());

		}
	}

	/**
	 * @param ifStatement
	 * @return null
	 */
	@Override
	public Object visit(If ifStatement) {
		// TODO check that every branch has return!
		Type cond = (Type) ifStatement.getCondition().accept(this);
		if (!cond.getName().equals("boolean")) {
			throw new SemanticException(ifStatement,
					"Non boolean condition for if statement");
		}
		//In case there are many if statments in a raw, we need to know which if scope is handled
		this.cond_block.push(true);
		ifStatement.getOperation().accept(this);
		this.cond_block.pop();
		if (ifStatement.hasElse()) {
			ifStatement.getElseOperation().accept(this);
		}
		return null;
	}

	/**
	 * @param whileStatement
	 * @return
	 */
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

	/**
	 * @param breakStatement
	 * @return
	 */
	@Override
	public Object visit(Break breakStatement) {

		if (this.loop.empty() || !this.loop.peek().booleanValue())
			throw new SemanticException(breakStatement,
					"Use of 'break' statement outside of loop not allowed");
		return null;
	}

	/**
	 * @param continueStatement
	 * @return
	 */
	@Override
	public Object visit(Continue continueStatement) {

		if (this.loop.empty() || !this.loop.peek().booleanValue())
			throw new SemanticException(continueStatement,
					"Use of 'continue' statement outside of loop not allowed");
		return null;
	}

	/**
	 * @param statementsBlock
	 * @return
	 */
	@Override
	public Object visit(StatementsBlock statementsBlock) {

		for (Statement s : statementsBlock.getStatements()) {
			s.accept(this);
		}
		return null;
	}

	/**
	 * @param localVariable
	 * @return
	 */
	@Override
	public Object visit(LocalVariable localVariable) {

		if (localVariable.hasInitValue()) {
			Type init = (Type) localVariable.getInitValue().accept(this);
			if (init == null) {
				return null;
			}
			checkAssignment(localVariable.getType(), init, localVariable);
		}
		return localVariable.getType();
	}

	/**
	 * @param location
	 * @return
	 */
	@Override
	public Object visit(VariableLocation location) {
		//The variable reference is of the type <var>, it's not a class field
		if (location.getLocation() == null){
			Object variable = location.scope.lookupId(location.getName());
			if (variable == null) {
				throw new SemanticException(location, location.getName()
						+ " not found in symbol table");
			} else if (variable instanceof Field) {
				//If the variable is defined as a field in a static scope - We get an error
				if (static_scope == true)
					throw new SemanticException(location,
							"Use of field inside static method is not allowed");
				return ((Field) variable).getType();
			}

			return (Type) variable;
			
		} else {
			//The variable is a class, so it is looked up
			Type ctype = (Type) location.getLocation().accept(this);
			ICClass c = (ICClass) location.scope.lookupId(ctype
					.getName());
			//Look up the field defention in the class
			Field field = c.scope.getField(location.getName());
			if (field == null) {
				throw new SemanticException(location, location.getName()
						+ " doesn't exist in " + c.getName());
			}
			return field.getType();
		}
	}

	/**
	 * @param location
	 * @return
	 */
	@Override
	public Object visit(ArrayLocation location) {

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

	/**
	 * @param call
	 * @return the method call return type if successfull
	 */
	@Override
	public Object visit(StaticCall call) {

		Object c = call.scope.lookupId(call.getClassName());
		//Static calls have to be in a form of "<class>."
		if (!(c instanceof ICClass)) {
			throw new SemanticException(call, call.getClassName()
					+ " class doesn't exist");
		}
		//The method called has to be predefined in the class, so it looks for it
		Method method = ((ICClass) c).scope.getMethod(call.getName());
		if (method == null) {
			throw new SemanticException(call, "Method " + call.getName()
					+ " doesn't exist");
		}
		//The static method has to contain the same number of parameters as the call
		if (call.getArguments().size() != method.getFormals().size()) {
			throw new SemanticException(call,
					"Invalid number of arguments for "
							+ ((ICClass) c).getName() + "."
							+ call.getName());
		}
		//The method called has to be static as well..
		if (method instanceof VirtualMethod)
			throw new SemanticException(call, " called method isn't static");
		
		//Iterate over the method calling arguments, 
		//compare the method arguments predefined accordingly
		for (int i = 0; i < call.getArguments().size(); i++) {
			Type t = (Type) call.getArguments().get(i).accept(this);
			Type formal = method.getFormals().get(i).getType();
			if (!t.getName().equals(formal.getName())) {
				//formals aren't of the same type, it can still be valid
				//if the called argument is a sub class of the defined argument 
				if (formal instanceof UserType && t instanceof UserType) {
					ICClass classA = (ICClass) call.scope.lookupId(t
							.getName());
					ICClass classB = (ICClass) call.scope.lookupId(formal
							.getName());
					if (!isSubClass(classB.scope, classA.scope)) {
						throw new SemanticException(call, "Method "
								+ ((ICClass) c).getName() + "."
								+ call.getName()
								+ " is not applicable for the arguments given");
					}
				//In case this is not a call of an inherited argument,
				//It may have been a null reference call for a user-defined type or string
				} else if (((formal instanceof UserType) || formal.getName().equals("string")) &&
						!t.getName().equals("void")) {
					throw new SemanticException(call, "Method "
								+ ((ICClass) c).getName() + "."
								+ call.getName()
								+ " is not applicable for the arguments given");
				}
			}
		}
		return method.getType();
	}

	/**
	 * @param call
	 * @return the method return type if successful
	 */
	@Override
	public Object visit(VirtualCall call) {
		Object m = null;
		String class_name = null;
		//The calling is not of the format "<exp>."
		if (call.getLocation() == null) {
			//Look for the method definition in its class
			m = call.scope.lookupId(call.getName());
			//Look for the name of the class the method is defined in
			class_name = lookupClassScopeName(call.scope);
			if (m == null || !(m instanceof Method)) {
				throw new SemanticException(call, call.getName()
						+ " not found in symbol table");
			}
			//can't call a virtual method in a static scope
			if (this.static_scope == true && m instanceof VirtualMethod) {

				throw new SemanticException(call,
						" Calling a local virtual method from inside a static method is not allowed");
			}

		} else {
			//Classify the class ID of the called method (before the ".")
			Type class_type = (Type) call.getLocation().accept(this);
			//Primitive type isn't a class - Can't call a method with "." 
			if (class_type instanceof PrimitiveType) {
				throw new SemanticException(call,
						" Primitive type has no methods");
			}
			//Look for the class's method in the class's table
			Object c = call.scope.lookupId(class_type.getName());
			m = ((ICClass) c).scope.lookupId(call.getName());
			//Get the instance's class name
			class_name = ((ICClass) c).getName();
			if (m == null || !(m instanceof Method)) {
				throw new SemanticException(call, "Method " + class_name + "."
						+ call.getName() + " not found in type table");
			}

		}
		//Checks the calling and the definition have the same amount of arguments
		if (call.getArguments().size() != ((Method) m).getFormals().size()) {
			throw new SemanticException(call,
					"Invalid number of arguments for method "
							+ call.getName());
		}
		
		//Iterates over the arguments - Compares each of them typewise: See static call doc
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
								+ class_name + "." + call.getName()
								+ " is not applicable for the arguments given");
					}
				} else if (((formal instanceof UserType) || formal.getName().equals("string")) &&
						!t.getName().equals("void")) {
					throw new SemanticException(call, "Method " + class_name
							+ "." + call.getName()
							+ " is not applicable for the arguments given");
				}
			}
		}
		return ((Method) m).getType();
	}

	/**
	 * @param thisExpression
	 * @return the type 'this' refers to
	 */
	@Override
	public Object visit(This thisExpression) {
		//check if 'this' was used inside a static scope
		if (this.static_scope == true) {
			throw new SemanticException(thisExpression,
					" Use of 'this' expression inside static method is not allowed");
		}
		return new UserType(thisExpression.getLine(),
				lookupClassScopeName(thisExpression.scope));
	}

	/**
	 * @param newClass
	 * @return the type of the new 'User Type' object
	 */
	@Override
	public Object visit(NewClass newClass) {

		Object c = newClass.scope.lookupId(newClass.getName());
		//check if class was declared before
		if (c == null) {
			throw new SemanticException(newClass, newClass.getName()
					+ " not found in symbol table");
		}
		return new UserType(newClass.getLine(), newClass.getName());
	}

	/**
	 * @param newArray
	 * @return the type of the new array
	 */
	@Override
	public Object visit(NewArray newArray) {

		Type sizeType = (Type) newArray.getSize().accept(this);
		//a new array should always be created with expression size of type int
		if (!sizeType.getName().equals("int")) {
			throw new SemanticException(newArray, " size should be int");
		}
		return newArray.getType();
	}

	/**
	 * @param length
	 * @return int primitive type
	 */
	@Override
	public Object visit(Length length) {

		length.getArray().accept(this);
		return new PrimitiveType(length.getLine(), DataTypes.INT);
	}

	/**
	 * @param literal
	 * @return the literal type, if literal is null returns void type.
	 */
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
			case ("Literal") : ret = new PrimitiveType(literal.getLine(), DataTypes.VOID);
				break;	
		}
		return ret;
	}

	/**
	 * @param unaryOp
	 * @return the operand Type if its int, otherwise throws an exception
	 */
	@Override
	public Object visit(MathUnaryOp unaryOp) {

		Type operandType = (Type) unaryOp.getOperand().accept(this);
		switch (operandType.getName()) {
		case "int":
			if (!unaryOp.getOperator().getDescription().equals("unary subtraction")) {
				throw new SemanticException(unaryOp, " type mismatch");
			}
			return operandType;
		default:
			throw new SemanticException(unaryOp, " type mismatch");
		}
	}
	
	/**
	 * @param unaryOp
	 * @return the operand Type if its boolean, otherwise throws an exception
	 */
	@Override
	public Object visit(LogicalUnaryOp unaryOp) {

		Type operandType = (Type) unaryOp.getOperand().accept(this);
		switch (operandType.getName()) {
		case "boolean":
			if (!unaryOp.getOperator().getDescription().equals("logical negation")) {
				throw new SemanticException(unaryOp, " type mismatch");
			}
			return operandType;
		default:
			throw new SemanticException(unaryOp, " type mismatch");
		}
	}

	/**
	 * @param binaryOp
	 * @return the type of the Mathematical expression which has to be int or string(if its addition).
	 */
	@Override
	public Object visit(MathBinaryOp binaryOp) {
		Type a = (Type) binaryOp.getFirstOperand().accept(this);
		Type b = (Type) binaryOp.getSecondOperand().accept(this);
		switch (binaryOp.getOperator().getDescription()) {
		case "addition":
			if (a.getName().equals("int") //check if both ints
					&& b.getName().equals("int")) {
				return a;
			} else if (a.getName().equals("string") //check if both strings
					&& b.getName().equals("string")) {
				return a;
			} else {
				throw new SemanticException(binaryOp, "Type mismatch: "
						+ a.getName() + " " + binaryOp.getOperator()
						+ " " + b.getName());
			}
		case "subtraction":
		case "multiplication":
		case "division":
		case "modulo":
			if (a.getName().equals("int")
					&& b.getName().equals("int")) {
				return a;
			} else {
				throw new SemanticException(binaryOp, "Type mismatch: "
						+ a.getName() + " " + binaryOp.getOperator()
						+ " " + b.getName());
			}
		}
		return null;
	}
	
	/**
	 * @param binaryOp
	 * @return the type of the Logical Binary expression which has to be boolean.
	 */
	public Object visit(LogicalBinaryOp binaryOp) {
		Type a = (Type) binaryOp.getFirstOperand().accept(this);
		Type b = (Type) binaryOp.getSecondOperand().accept(this);
		switch (binaryOp.getOperator().getDescription()) {
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
		case "equality":
		case "inequality":
			if (a.getName().equals(b.getName())) { //check if both the same
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);
			} else if ((a.getName().equals("void") && b instanceof UserType)
					|| (b.getName().equals("void") && a instanceof UserType)) { //check if one is null and the other is an UserType object
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);
			} else if (b instanceof UserType && a instanceof UserType) {
				ICClass classA = (ICClass) binaryOp.scope.lookupId(a
						.getName());
				ICClass classB = (ICClass) binaryOp.scope.lookupId(b
						.getName());
				if (!isSubClass(classA.scope, classB.scope)
						&& !isSubClass(classB.scope, classA.scope)) { //check if both UserType objects, if one is a subclass of the other
					throw new SemanticException(binaryOp, "Type mismatch: "
							+ a.getName() + " " + binaryOp.getOperator()
							+ " " + b.getName());
				}
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);
			} else if ((a.getName().equals("void") && b.getName().equals("string"))
					|| (b.getName().equals("void") && a.getName().equals("string"))) { //check if one is null and the other is a string
				return new PrimitiveType(binaryOp.getLine(), DataTypes.BOOLEAN);
			} else {
				throw new SemanticException(binaryOp, "Type mismatch: "
						+ a.getName() + " " + binaryOp.getOperator()
						+ " " + b.getName());
			}
		}
		return null;
	}
	
	/**
	 * @param expressionBlock
	 * @return the type of the expression within the block
	 */
	@Override
	public Object visit(ExpressionBlock expressionBlock) {
		
		return expressionBlock.getExpression().accept(this);
	}

	/**
	 * @param node
	 * @return class name
	 */
	private String lookupClassScopeName(ScopeNode node) {
		//search until you see scope of type class, return his name
		while (node.getType() != ScopeType.Class) {
			node = node.getParent();
		}
		return node.getName();

	}

	/**
	 * @param classA
	 * @param subClassA
	 * @return Whether subClassA is indeed a subclass of A
	 */
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

	/**
	 * @param method
	 */
	private void checkParams(Method method) {
		for (Formal f : method.getFormals()) {
			//If it is a primitive type - The checker knows the type and moves on
			if (f.getType() instanceof PrimitiveType) {
				continue;
			}
			//Not a primitive type - Checks whether the type has been declared in table
			Object c = method.scope.lookupId(f.getType().getName());
			if (c == null) {
				throw new SemanticException(method, f.getType()
						.getName() + " not found in type table");
			}
		}
	}

	/**
	 * Checks whether the assignment is legitimate in terms of type checking
	 * @param a
	 * @param b
	 * @param assignment
	 */
	private void checkAssignment(Type a, Type b, ASTNode assignment) {
		if (!a.getName().equals(b.getName())) {//If a and b are not of the same type
			if (a instanceof UserType && b instanceof UserType) {//Both are user-defined types, in case of an inherited class assignment
				ICClass classA = (ICClass) assignment.scope.lookupId(a
						.getName());
				ICClass classB = (ICClass) assignment.scope.lookupId(b
						.getName());
				if (isSubClass(classA.scope, classB.scope)) {
					return;//If a is a subclass of b - Checking is valid
				}
			} else if (a instanceof UserType && b instanceof PrimitiveType) {
				if (b.getName().equals("void")) {//b is null, so it can be assigned to a user-defined type
					return;
				}
			} else if (a instanceof PrimitiveType && a.getDimension() > 0) {
				if (b.getName().equals("void")) {//a is a primitive array - null can be assigned to it
					return;
				}
			} else if (a instanceof PrimitiveType && a.getName().equals("string")) {
				if (b.getName().equals("void")) {//a is a string - null can be assigned to it
					return;
				}
			}
			throw new SemanticException(assignment,
					"Invalid assignment of type " + b.getName()
							+ " to variable of type " + a.getName());
//TODO		} else if (a.getDimension() != b.getDimension()) {
//			//Same types, but if dimensions are different it's still an error 
//			throw new SemanticException(assignment,
//					"Invalid assignment of type " + b.getName() + " with " + a.getDimension() +" dimensions"
//							+ " to variable of type " + a.getName()+ " with " + b.getDimension() +" dimensions");
		}

	}


}
