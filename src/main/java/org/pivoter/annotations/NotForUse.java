package org.pivoter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS) // Or RetentionPolicy.RUNTIME if needed
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NotForUse {
    String reason() default "This method should not be used.";
}
