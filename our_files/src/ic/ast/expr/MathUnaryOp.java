package ic.ast.expr;

import ic.UnaryOps;
import ic.ast.Visitor;

/**
 * Mathematical unary operation AST node.
 * 
 * @author Tovi Almozlino
 */
public class MathUnaryOp extends UnaryOp {

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a new mathematical unary operation node.
	 * 
	 * @param operator
	 *            The operator.
	 * @param operand
	 *            The operand.
	 */
	public MathUnaryOp(UnaryOps operator, Expression operand) {
		super(operator, operand);
	}

}
