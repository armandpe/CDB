package main.java.com.excilys.cdb.service;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface ServiceMethod {
	boolean forUser() default true; //If false, you must manually specify the behavior of the function
	String fullName() default ""; //Specify if forUser is false, must be unique
	String name();
}
