import mxevaluator.CompareFunction;
import mxevaluator.PositionFunction;
import mxevaluator.PredicateEvaluator;
import mxevaluator.TextEvaluatorFunction;

public final class ExampleBeamEvaluator extends PredicateEvaluator<ExampleBean> {

    private static final String[] VAR_NUMS = {"age", "size"};

    private static final String[] VAR_TEXT = {"name", "country"};

    private static final TextEvaluatorFunction[] FUNCS = {new CompareFunction(), new PositionFunction()};

    public ExampleBeamEvaluator(final String ruleExpression) throws RuntimeException {
        super(ruleExpression, FUNCS);
    }

    @Override
    protected String[] getNumericVariables() {
        return VAR_NUMS;
    }

    @Override
    protected String[] getTextVariables() {
        return VAR_TEXT;
    }

    @Override
    protected Object getValueFrom(final ExampleBean sourceBean, final String variableName) throws IllegalStateException {
        switch (variableName.toLowerCase()) {
            case "age":
                return getVariableForAge(sourceBean);
            case "size":
                return getVariableForSize(sourceBean);
            case "name":
                return getVariableForName(sourceBean);
            case "country":
                return getVariableForCountry(sourceBean);
            default:
                throw new IllegalStateException("Unexpected variable: " + variableName);
        }
    }

    private Double getVariableForAge(final ExampleBean sourceBean) {
        return sourceBean.age + 0.0;
    }

    private Double getVariableForSize(final ExampleBean sourceBean) {
        return sourceBean.name.length() + 0.0;
    }

    private String getVariableForName(final ExampleBean sourceBean) {
        return sourceBean.name;
    }

    private String getVariableForCountry(final ExampleBean sourceBean) {
        return sourceBean.country;
    }
}
