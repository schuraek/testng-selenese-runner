package de.kosch.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SeleneseMethodInfo {

    /**
     * supported: relative, absolute path for example: test1.html,
     * 
     * 
     * @return
     */
    String[] selenesePath();

    String configPath() default StringUtils.EMPTY;
    

    String[] configArgs() default {};

    /**
     * optional
     * 
     * @return
     */
    String driver() default "FIREFOX";

    /**
     * set to true for tests contains other assertions in test 
     * don't forget to call continue
     * @return
     */
    boolean preconditionMode() default false;

    /**
     * all selenese parents paths
     * @return
     */
    String[] chainedParents() default {};
}
