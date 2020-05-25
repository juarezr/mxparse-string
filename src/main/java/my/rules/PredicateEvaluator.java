package my.rules;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluates when a expression returns true or flase
 *
 * @param <T> Source Class for obtaining 'variables' for expressions
 */
public abstract class PredicateEvaluator<T> {

    // (?:([\w]+)\(([^\)]+)\))
    private final static String functionRegexp = "(?:([\\w]+)\\(([^\\)]+)\\))";
    private final static Pattern functionPattern = Pattern.compile(functionRegexp);

    // \s*([^,\s]+)\s*
    private final static String paramRegexp = "\\s*([^,\\s]+)\\s*";
    private final static Pattern paramPattern = Pattern.compile(paramRegexp);

    // ^\s*([a-zA-Z][a-zA-Z_]*)\s*$
    private final static String variableRegexp = "^([a-zA-Z][a-zA-Z0-9_]*)$";
    private final static Pattern variablePattern = Pattern.compile(variableRegexp);

    // ^(["'])(?:(?=(\\?))\2.)*?\1$
    private final static String stringRegexp = "^([\"'])(?:(?=(\\\\?))\\2.)*?\\1$";
    private final static Pattern stringPattern = Pattern.compile(stringRegexp);

    // (["'])(?:(?=(\\?))\2.)*?\1
    private final static String quotesRegexp = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
    private final static Pattern quotesPattern = Pattern.compile(quotesRegexp);

    private final Expression parser;
    private final TextEvaluatorFunction comparator;

    private final List<String> consts;
    private final List<String> values;

    /**
     * List all numeric variables supported by this Evaluator
     */
    protected abstract String[] getNumericVariables();

    /**
     * List all text variables supported by this Evaluator
     */
    protected abstract String[] getTextVariables();

    /**
     * Lazily get the value for a numeric/text variable while evaluating the rule for the source class
     *
     * @param sourceBeam   the instance from where to get the numeric/text value for variableName
     * @param variableName extract the value for this variable
     * @return the value corresponding to the variableName in the sourceBeam
     */
    protected abstract Object getValueFrom(final T sourceBeam, final String variableName);

    /**
     * Creates a evaluator for one rule for testing multiple instances of the evaluated source class
     *
     * @param ruleExpression An MxParser expression containing the variables provided by the source class
     * @throws RuntimeException Fails when detecting errors in the expression provided
     */
    public PredicateEvaluator(final String ruleExpression) throws RuntimeException {

        this.consts = new ArrayList<>();
        this.values = new ArrayList<>();

        this.checkExpression(ruleExpression);
        final String parsed = this.parseQuotedText(ruleExpression);
        this.comparator = new TextEvaluatorFunction(this::getParameterText);

        this.parser = new Expression(parsed);
        this.parser.addFunctions(new Function("compare", this.comparator));

        defineRuleVariables(ruleExpression);
    }

    private void checkExpression(final String ruleExpression) throws RuntimeException {
        final Matcher matched = functionPattern.matcher(ruleExpression);

        while (matched.find()) {
            final String fexpr = matched.group(); // full match
            final String fname = matched.group(1);
            final String fpars = matched.group(2);
            final Matcher params = paramPattern.matcher(fpars);
            while (params.find()) {
                final String parameter = params.group(1);
                final Matcher validVar = variablePattern.matcher(parameter);
                if (!validVar.matches()) {
                    final Matcher validStr = stringPattern.matcher(parameter);
                    if (!validStr.matches()) {
                        final String msg = String.format("Parameter `%s` for function `%s` must be a variable or a string: %s",
                                parameter, fname, fexpr);
                        throw new RuntimeException(msg);
                    }
                }
            }
        }
    }

    private String parseQuotedText(final String ruleExpression) {

        StringBuffer parsed = new StringBuffer(); // StringBuilder for Java > 8
        final Matcher matched = quotesPattern.matcher(ruleExpression);

        while (matched.find()) {
            final String literal = matched.group();
            final int len = literal.length();
            final String unquoted = len < 3 ? "" : literal.substring(1, len - 1);

            final int literalIndex = this.addConst(unquoted);
            final String replacement = Integer.toString(literalIndex);

            matched.appendReplacement(parsed, replacement);
        }
        matched.appendTail(parsed);
        return parsed.toString();
    }

    private void defineRuleVariables(final String ruleExpression) {

        final String[] missingVars = this.parser.getMissingUserDefinedArguments();

        if (missingVars.length > 0) {
            final Set<String> numeric = new HashSet<>(Arrays.asList(this.getNumericVariables()));
            final Set<String> textual = new HashSet<>(Arrays.asList(this.getTextVariables()));

            final StringBuilder unknown = new StringBuilder();
            int notFound = 0;

            for (final String missing : missingVars) {
                final boolean found = numeric.contains(missing) || textual.contains(missing);
                if (found) {
                    this.parser.defineArgument(missing, 0);
                } else {
                    unknown.append(notFound < 1 ? missing : " ".concat(missing));
                    notFound += 1;
                }
            }
            if (notFound > 0) {
                final String msg = notFound > 1
                        ? String.format("Found %d unknown variables with names '%s' while parsing expression '%s'", notFound, unknown.toString(), ruleExpression)
                        : String.format("Found a unknown variable named '%s' while parsing expression '%s'", unknown.toString(), ruleExpression);
                throw new RuntimeException(msg);
            }
        }
    }

    public boolean evaluate(final T sourceBeam) throws RuntimeException {

        this.clearValues();

        final int argNumber = this.parser.getArgumentsNumber();
        for (int i = 0; i < argNumber; i++) {
            final Argument arg = this.parser.getArgument(i);

            final String argName = arg.getArgumentName();

            final Object argValue = this.getValueFrom(sourceBeam, argName);

            if (argValue instanceof Double) {
                arg.setArgumentValue((Double) argValue);
            } else {
                final String beamValue = argValue == null ? "null" : argValue.toString();
                final int valueIndex = this.addValue(beamValue);
                arg.setArgumentValue(valueIndex);
            }
        }
        final double success = this.parser.calculate();
        return success != 0;
    }


    public int addConst(final String literal) {
        final int valueIndex = consts.size() - 1;
        this.consts.add(literal);
        return valueIndex;
    }

    public int addValue(final String literal) {
        final int valueIndex = values.size();
        this.values.add(literal);
        return valueIndex;
    }

    public void clearValues() {
        this.values.clear();
    }

    private String getParameterText(final Double parameterIndex) {
        final int valueIndex = parameterIndex.intValue();
        if (valueIndex >= 0) {
            return this.values.get(valueIndex);
        } else {
            final int literalIndex = (valueIndex * -1) - 1;
            return this.consts.get(literalIndex);
        }
    }
}
