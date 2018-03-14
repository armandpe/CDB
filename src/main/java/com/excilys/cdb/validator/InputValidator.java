package com.excilys.cdb.validator;

import java.util.List;
import java.util.function.Function;

public class InputValidator {

	public static <T, E extends Exception> void isCorrectString(String s, ExceptionFunction<String, T, E> parse, 
			boolean nullOk, boolean emptyOk, List<Function<T, Boolean>> tests) throws InvalidInputException, E {

		if (s == null) {
			if (!nullOk) {
				throw new InvalidInputException("- null value");
			}
		} else if (s.trim().equals("")) {
			if (!emptyOk) {
				throw new InvalidInputException("- empty value");
			}
		} else {

			T parsed = null;

			parsed = parse.apply(s);

			if (parsed == null) {
				if (!nullOk) {
					throw new InvalidInputException("- null value");
				}
			} else {

				for (Function<T, Boolean> test : tests) {
					if (!test.apply(parsed)) {
						throw new InvalidInputException("- incorrect value");
					}
				}

			}
		}
	}
}
