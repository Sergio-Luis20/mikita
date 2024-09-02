package mikita.command;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandEntry {

    // SubCommands
    String value() default "";
    boolean oneWordFinalString() default false;

}

