package mxevaluator;

import org.mariuszgromada.math.mxparser.FunctionExtension;

import java.util.function.Function;

public abstract class TextEvaluatorFunction implements FunctionExtension, Cloneable {

    public abstract String getFunctionName();

    public abstract int getParametersNumber();

    public abstract void setParameterValue(final int parameterIndex, final double parameterValue);

    public abstract String getParameterName(final int parameterIndex);

    public abstract double calculate();

    protected Function<Double, String> resolver;

    public void setResolver(final Function<Double, String> valueResolver) {
        this.resolver = valueResolver;
    }

    protected String getParameterText(final Double parameterIndex) {
        return this.resolver.apply(parameterIndex);
    }

    public TextEvaluatorFunction clone() {
        try {
            final TextEvaluatorFunction cloned = (TextEvaluatorFunction) super.clone();
            cloned.resolver = this.resolver;
            return cloned;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
