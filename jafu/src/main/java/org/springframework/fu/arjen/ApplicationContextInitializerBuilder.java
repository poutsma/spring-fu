package org.springframework.fu.arjen;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author Arjen Poutsma
 */
public interface ApplicationContextInitializerBuilder {

	ApplicationContextInitializer<GenericApplicationContext> build();

}
