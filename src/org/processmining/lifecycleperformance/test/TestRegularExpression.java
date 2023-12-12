package org.processmining.lifecycleperformance.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegularExpression {

	public static void main(String[] args) {

		String pattern = "state_time_spent_min\\(\\s*(\\w*)\\s*\\)";
		String text = "(state_time_spent_min(started) + state_time_spent_min(completed))/2";

		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);

		while (m.find()) {

			System.out.println(m.group(0));
			System.out.println(m.group(1));

			String arguments = m.group(1);
			String[] split = arguments.split(",");

			for (String s : split) {
				System.out.println("\"" + s.trim() + "\"");
			}

			System.out.println();
		}

	}

}
