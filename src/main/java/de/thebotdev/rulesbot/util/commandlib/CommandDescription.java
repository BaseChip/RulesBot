package de.thebotdev.rulesbot.util.commandlib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandDescription {
    String name();

    String[] triggers();

    String longDescription();

    String description();

    boolean hidden() default false;

    String[] usage();
}
