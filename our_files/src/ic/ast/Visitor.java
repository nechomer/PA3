package ic.ast;

import ic.ast.expr.ArrayLocation;
import ic.ast.expr.ExpressionBlock;
import ic.ast.expr.Length;
import ic.ast.expr.Literal;
import ic.ast.expr.LogicalBinaryOp;
import ic.ast.expr.LogicalUnaryOp;
import ic.ast.expr.MathBinaryOp;
import ic.ast.expr.MathUnaryOp;
import ic.ast.expr.NewArray;
import ic.ast.expr.NewClass;
import ic.ast.expr.StaticCall;
import ic.ast.expr.This;
import ic.ast.expr.VariableLocation;
import ic.ast.expr.VirtualCall;
import ic.ast.methods.LibraryMethod;
import ic.ast.methods.StaticMethod;
import ic.ast.methods.VirtualMethod;
import ic.ast.stmt.Assignment;
import ic.ast.stmt.Break;
import ic.ast.stmt.CallStatement;
import ic.ast.stmt.Continue;
import ic.ast.stmt.If;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Return;
import ic.ast.stmt.StatementsBlock;
import ic.ast.stmt.While;
import ic.ast.types.PrimitiveType;
import ic.ast.types.UserType;

/**
 * AST visitor interface. Declares methods for visiting each type of AST node.
 * 
 * @author Tovi Almozlino
 */
public interface Visitor {

	public Object visit(Program program);

	public Object visit(ICClass icClass);

	public Object visit(Field field);

	public Object visit(VirtualMethod method);

	public Object visit(StaticMethod method);

	public Object visit(LibraryMethod method);

	public Object visit(Formal formal);

	public Object visit(PrimitiveType type);

	public Object visit(UserType type);

	public Object visit(Assignment assignment);

	public Object visit(CallStatement callStatement);

	public Object visit(Return returnStatement);

	public Object visit(If ifStatement);

	public Object visit(While whileStatement);

	public Object visit(Break breakStatement);

	public Object visit(Continue continueStatement);

	public Object visit(StatementsBlock statementsBlock);

	public Object visit(LocalVariable localVariable);

	public Object visit(VariableLocation location);

	public Object visit(ArrayLocation location);

	public Object visit(StaticCall call);

	public Object visit(VirtualCall call);

	public Object visit(This thisExpression);

	public Object visit(NewClass newClass);

	public Object visit(NewArray newArray);

	public Object visit(Length length);

	public Object visit(MathBinaryOp binaryOp);

	public Object visit(LogicalBinaryOp binaryOp);

	public Object visit(MathUnaryOp unaryOp);

	public Object visit(LogicalUnaryOp unaryOp);

	public Object visit(Literal literal);

	public Object visit(ExpressionBlock expressionBlock);

}
