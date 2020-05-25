import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.List;

public class TestMain {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Test
    public void testingRulesWithValues() {

        evaluateExpr("age > 40 || age < 30");
    }

    @Test
    public void testingRulesWithText() {

        evaluateExpr("compare(name, 'Bob') > 0");

        evaluateExpr("( compare(name, 'Bob') > 0 ) || ( compare(country, 'China') > 0 )");
    }

    @Test(expected = RuntimeException.class)
    public void testingRulesWithFailure1() {

        evaluateExpr("compare(name, 200) > 0");
    }

    @Test(expected = RuntimeException.class)
    public void testingRulesWithFailure2() {

        evaluateExpr("compare(200, 'Alice') > 0");
    }

    @Test(expected = RuntimeException.class)
    public void testingRulesWithFailure3() {

        evaluateExpr("compare('Bob', 200) > 0");
    }

    private void evaluateExpr(final String ruleExpression) {

        show("Evaluating expression: %s: ", ruleExpression);

        final ExampleBeamEvaluator evaluator = new ExampleBeamEvaluator(ruleExpression);

        final List<ExampleBean> itemsToTest = ExampleBean.getTestItems();

        for (final ExampleBean item : itemsToTest) {

            final boolean ok = evaluator.evaluate(item);

            show("  * Evaluated '%s => %s", item, ok);
        }
    }

    private void show(String formatted, Object... vars) {

        final String msg = String.format(formatted, vars);
        System.out.println(msg);
//        assertEquals(msg, systemOutRule.getLog());
    }
}
