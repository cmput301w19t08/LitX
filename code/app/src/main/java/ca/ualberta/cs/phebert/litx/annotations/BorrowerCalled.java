package ca.ualberta.cs.phebert.litx.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.CONSTRUCTOR})
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface BorrowerCalled {
}
