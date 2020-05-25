package my.rules;

import org.mariuszgromada.math.mxparser.FunctionExtension;

import java.util.function.Function;

public class TextEvaluatorFunction implements FunctionExtension {

    private Double parameter1;
    private Double parameter2;
    private Function<Double, String> resolver;

    public TextEvaluatorFunction(Function<Double, String> valueResolver) {
        this(0, 0, valueResolver);
    }

    private TextEvaluatorFunction(double a, double b, Function<Double, String> valueResolver) {
        this.parameter1 = a;
        this.parameter2 = b;
        this.resolver = valueResolver;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public FunctionExtension clone() {
        return new TextEvaluatorFunction(this.parameter1, this.parameter2, this.resolver);
    }

    @Override
    public int getParametersNumber() {
        return 2;
    }

    @Override
    public void setParameterValue(final int parameterIndex, final double parameterValue) {
        if (parameterIndex == 0) {
            this.parameter1 = parameterValue;
        } else {
            this.parameter2 = parameterValue;
        }
    }

    @Override
    public String getParameterName(final int parameterIndex) {
        return "text" + parameterIndex;
    }

    @Override
    public double calculate() {
        final String text1 = getParameterText(this.parameter1);
        final String text2 = getParameterText(this.parameter2);

        return text1.compareTo(text2);
    }

    private String getParameterText(final Double parameterIndex) {
        return this.resolver.apply(parameterIndex);
    }
}
