package ic.semanticCheck;


import ic.ast.Field;
import ic.ast.Formal;
import ic.ast.ICClass;
import ic.ast.LibraryMethod;
import ic.ast.LocalVariable;
import ic.ast.Method;
import ic.ast.StaticMethod;
import ic.ast.Type;
import ic.ast.VirtualMethod;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScopeNode {
	
	// Global scope params
	private HashMap<String, ICClass> classes;

	// Class scope params
	private HashMap<String, Method> methods;
	private HashMap<String, Field> fields;

	// Method scope params
	private HashMap<String, Type> parameters;

	// Statement block scope params
	private HashMap<String, Type> localVars;

	// Scope params
	private ScopeType scopeType;
	private String scopeName;

	private ScopeNode parent;
	private List<ScopeNode> children;
	

	public static enum ScopeType {
		Global("Global Symbol Table"), Class("Class Symbol Table"), Method(
				"Method Symbol Table"), StatementBlock(
				"Statement Block Symbol Table");

		private final String name;

		private ScopeType(String s) {
			name = s;
		}

		public String toString() {
			return name;
		}
	}

	public ScopeNode(ScopeType type, String name, ScopeNode parent) {
		scopeType = type;
		scopeName = name;

		classes = new LinkedHashMap<String, ICClass>();
		methods = new LinkedHashMap<String, Method>();
		fields = new LinkedHashMap<String, Field>();
		parameters = new LinkedHashMap<String, Type>();
		localVars = new LinkedHashMap<String, Type>();

		this.parent = parent;
		children = new LinkedList<ScopeNode>();
	}

	public ScopeType getType() {
		return scopeType;
	}

	public ScopeNode getParent() {
		return parent;
	}

	public String getName() {
		return scopeName;
	}

	public List<ScopeNode> getChildren() {
		return children;
	}

	public ICClass getClass(String name) {
		return classes.get(name);
	}

	public Method getMethod(String name) {
		return methods.get(name);
	}

	public Field getField(String name) {
		return fields.get(name);
	}

	public Type getParameter(String name) {
		return parameters.get(name);
	}

	public Type getLocalVar(String name) {
		return localVars.get(name);
	}

	// Look up an identifier up the scope hierarchy
	public Object retrieveIdentifier(String name) {
		Object ret = null;
		ScopeNode node = this;

		while (node != null) {
			if ((ret = node.getLocalVar(name)) != null)
				break;

			if ((ret = node.getParameter(name)) != null)
				break;

			if ((ret = node.getField(name)) != null)
				break;

			if ((ret = node.getMethod(name)) != null)
				break;

			if ((ret = node.getClass(name)) != null)
				break;

			node = node.getParent();
		}

		return ret;
	}

	// Lookup the name of a parameter up to the nearest Method scope
	public Type lookupParameter(String name) {
		ScopeNode node = this;

		// find the enclosing Method scope
		while (node != null && node.getType() != ScopeType.Method)
			node = node.getParent();

		if (node == null)
			return null;

		return node.getParameter(name);
	}

	public void addClass(ICClass icClass) {
		if (getClass(icClass.getName()) != null)
			throw new SemanticException(icClass, "Id " + icClass.getName()
					+ " already defined in current scope");

		classes.put(icClass.getName(), icClass);
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

	public void addMethod(Method m) {
		if (getMethod(m.getName()) != null)
			throw new SemanticException(m, "Id " + m.getName()
					+ " already defined in current scope");

		Object ret;
		if ((ret = retrieveIdentifier(m.getName())) != null) {
			if (ret instanceof Method
					&& !methodSigEqual((Method) ret, m)) {
				throw new SemanticException(m, "method '" + m.getName()
						+ "' overloads a different method with the same name");
			} else if (ret instanceof Field) {
				throw new SemanticException(m, "Method " + m.getName()
						+ " is shadowing a field with the same name");
			}
		}

		methods.put(m.getName(), m);
	}

	public void addField(Field f) {
		if (getField(f.getName()) != null)
			throw new SemanticException(f, "Id " + f.getName()
					+ " already defined in current scope");

		Object ret;
		if ((ret = retrieveIdentifier(f.getName())) != null) {
			if (ret instanceof Field)
				throw new SemanticException(f, "Field " + f.getName()
						+ " is shadowing a field with the same name");
			else if (ret instanceof Method)
				throw new SemanticException(f, "Field " + f.getName()
						+ " is shadowing a method with the same name");
		}

		fields.put(f.getName(), f);
	}

	public void addParameter(Formal f) {
		if (getParameter(f.getName()) != null)
			throw new SemanticException(f, "Id " + f.getName()
					+ " already defined in current scope");

		parameters.put(f.getName(), f.getType());
	}

	public void addLocalVar(LocalVariable lv) {
		if (lookupParameter(lv.getName()) != null)
			throw new SemanticException(lv, "Local variable " + lv.getName()
					+ " is shadowing a parameter");

		if (getLocalVar(lv.getName()) != null)
			throw new SemanticException(lv, "Id " + lv.getName()
					+ " already defined in current scope");

		localVars.put(lv.getName(), lv.getType());
	}

	// Add a child scope node
	public ScopeNode addScope(ScopeType type, String name, ScopeNode parent) {
		ScopeNode scope = new ScopeNode(type, name, parent);
		children.add(scope);
		return scope;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(scopeType);
		if (getName() != null)
			sb.append(": " + getName());

		if (getParent() != null && parent.getType() != ScopeType.Global)
			sb.append("  (parent = " + parent.getName() + ")");

		sb.append("\n");

		// Classes
		for (String className : classes.keySet()) {
			sb.append("    Class:  ");
			sb.append(className);
			sb.append("\n");
		}

		// Fields
		for (Map.Entry<String, Field> entry : fields.entrySet()) {
			sb.append("    Field:  ");
			sb.append(entry.getKey());
			sb.append(" : ");
			sb.append(formatType(entry.getValue().getType()));
			sb.append("\n");
		}

		// Methods
		for (Map.Entry<String, Method> entry : methods.entrySet()) {
			if (entry.getValue() instanceof StaticMethod
					|| entry.getValue() instanceof LibraryMethod)
				sb.append("    Static method:  ");
			else if (entry.getValue() instanceof VirtualMethod)
				sb.append("    Virtual method:  ");

			sb.append(entry.getKey());
			sb.append(" : ");
			sb.append(formatSig(entry.getValue()));
			sb.append("\n");
		}

		// Parameters
		for (Map.Entry<String, Type> entry : parameters.entrySet()) {
			sb.append("    Parameter:  ");
			sb.append(entry.getKey());
			sb.append(" : ");
			sb.append(formatType(entry.getValue()));
			sb.append("\n");
		}

		// Local variables
		for (Map.Entry<String, Type> entry : localVars.entrySet()) {
			sb.append("    Local variable:  ");
			sb.append(entry.getKey());
			sb.append(" : ");
			sb.append(formatType(entry.getValue()));
			sb.append("\n");
		}

		return sb.toString();
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
