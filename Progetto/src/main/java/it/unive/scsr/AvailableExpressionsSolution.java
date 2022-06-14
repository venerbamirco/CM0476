package it.unive.scsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteForwardDataflowDomain;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;

public class AvailableExpressionsSolution
		implements
		DataflowElement<DefiniteForwardDataflowDomain<AvailableExpressionsSolution>, AvailableExpressionsSolution> {

	private final ValueExpression expression;

	public AvailableExpressionsSolution() {
		this(null);
	}

	private AvailableExpressionsSolution(ValueExpression expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return getIdentifierOperands(expression);
	}

	private static Collection<Identifier> getIdentifierOperands(ValueExpression expression) {
		Collection<Identifier> result = new HashSet<>();

		if (expression == null)
			return result;

		if (expression instanceof Identifier)
			result.add((Identifier) expression);

		if (expression instanceof UnaryExpression)
			result.addAll(getIdentifierOperands((ValueExpression) ((UnaryExpression) expression).getExpression()));

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;
			result.addAll(getIdentifierOperands((ValueExpression) binary.getLeft()));
			result.addAll(getIdentifierOperands((ValueExpression) binary.getRight()));
		}

		if (expression instanceof TernaryExpression) {
			TernaryExpression ternary = (TernaryExpression) expression;
			result.addAll(getIdentifierOperands((ValueExpression) ternary.getLeft()));
			result.addAll(getIdentifierOperands((ValueExpression) ternary.getMiddle()));
			result.addAll(getIdentifierOperands((ValueExpression) ternary.getRight()));
		}

		return result;
	}

	@Override
	public Collection<AvailableExpressionsSolution> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteForwardDataflowDomain<AvailableExpressionsSolution> domain) {
		Collection<AvailableExpressionsSolution> result = new HashSet<>();
		AvailableExpressionsSolution ae = new AvailableExpressionsSolution(expression);
		if (!ae.getInvolvedIdentifiers().contains(id) && filter(expression))
			result.add(ae);
		return result;
	}

	@Override
	public Collection<AvailableExpressionsSolution> gen(ValueExpression expression, ProgramPoint pp,
			DefiniteForwardDataflowDomain<AvailableExpressionsSolution> domain) {
		Collection<AvailableExpressionsSolution> result = new HashSet<>();
		AvailableExpressionsSolution ae = new AvailableExpressionsSolution(expression);
		if (filter(expression))
			result.add(ae);
		return result;
	}

	private static boolean filter(ValueExpression expression) {
		if (expression instanceof Identifier)
			return false;
		if (expression instanceof Constant)
			return false;
		if (expression instanceof Skip)
			return false;
		if (expression instanceof PushAny)
			return false;
		return true;
	}

	@Override
	public Collection<AvailableExpressionsSolution> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteForwardDataflowDomain<AvailableExpressionsSolution> domain) {
		Collection<AvailableExpressionsSolution> result = new HashSet<>();

		for (AvailableExpressionsSolution ae : domain.getDataflowElements()) {
			Collection<Identifier> ids = getIdentifierOperands(ae.expression);

			if (ids.contains(id))
				result.add(ae);
		}

		return result;
	}

	@Override
	public Collection<AvailableExpressionsSolution> kill(ValueExpression expression, ProgramPoint pp,
			DefiniteForwardDataflowDomain<AvailableExpressionsSolution> domain) {
		return Collections.emptyList();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AvailableExpressionsSolution other = (AvailableExpressionsSolution) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	@Override
	public DomainRepresentation representation() {
		return new StringRepresentation(expression);
	}

	@Override
	public AvailableExpressionsSolution pushScope(ScopeToken scope) throws SemanticException {
		return new AvailableExpressionsSolution((ValueExpression) expression.pushScope(scope));
	}

	@Override
	public AvailableExpressionsSolution popScope(ScopeToken scope) throws SemanticException {
		return new AvailableExpressionsSolution((ValueExpression) expression.popScope(scope));
	}
}
