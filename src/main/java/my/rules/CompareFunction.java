package my.rules;

public class CompareFunction extends TextEvaluatorFunction implements Cloneable {

    private Double parameter1;
    private Double parameter2;

    public CompareFunction() {
        this.parameter1 = 0.0;
        this.parameter2 = 0.0;
    }

    @Override
    public String getFunctionName() {
        return "compare";
    }

    @Override
    public int getParametersNumber() {
        return 2;
    }

    public CompareFunction clone() {
        final CompareFunction cloned = (CompareFunction) super.clone();
        cloned.parameter1 = this.parameter1;
        cloned.parameter2 = this.parameter2;
        return cloned;
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
        final String text1 = this.getParameterText(this.parameter1);
        final String text2 = this.getParameterText(this.parameter2);

        return text1.compareTo(text2);
    }
}
