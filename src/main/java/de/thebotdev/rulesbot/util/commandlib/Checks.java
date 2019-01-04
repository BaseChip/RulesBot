package de.thebotdev.rulesbot.util.commandlib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Checks {
    Check[] value() default {};
}
