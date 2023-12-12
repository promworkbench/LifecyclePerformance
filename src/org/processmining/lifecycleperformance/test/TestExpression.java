package org.processmining.lifecycleperformance.test;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class TestExpression {

	public static void main(String[] args) {
		//@formatter:off
		String expression = "x(a)+y";		
		
		ExpressionBuilder builder = new ExpressionBuilder(expression);		
		final double z = 2d;
		Function x = new Function("x", 1) {			
		    @Override
			public double apply(double... args) {
				return z*args[0];
			}
		};
		builder.function(x);
		builder.variable("a");
		builder.variable("y");
				
		Expression e = builder.build();
		e.setVariable("a", 2);
		e.setVariable("y", 2);
		//@formatter:on
		double result = e.evaluate();
		System.out.println(result);
	}

}
