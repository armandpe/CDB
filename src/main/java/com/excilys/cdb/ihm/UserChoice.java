package main.java.com.excilys.cdb.ihm;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * @author Armand Pette
 * Use this interface to expose methods to user
 * Annotated methods must return a boolean and musn't have arguments.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface UserChoice {
	String name();
}
