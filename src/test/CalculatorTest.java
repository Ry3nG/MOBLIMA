package test;

import main.Calculator;
import org.junit.*;

/**
 * A simple test class for the Calculator class
 * Helps to illustrate how unit test works.
 */
public class CalculatorTest {
    Calculator calculator;
    
    @Before
    public void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void testAdd() {
        int result = calculator.add(1, 2);
        Assert.assertEquals(3, result);
    }
    

}
