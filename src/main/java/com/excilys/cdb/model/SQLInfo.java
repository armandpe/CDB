package main.java.com.excilys.cdb.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLInfo {
	String name();
	boolean primaryKey() default false;
	boolean foreignKey() default false;
}
