package org.springframework.fu.arjen;

import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.fu.arjen.FooDsl.*;
import static org.springframework.fu.arjen.App.*;
/**
 * @author Arjen Poutsma
 */
class AppTests {

	public static void main(String[] args) {
		var application = application(app -> app
				.enable(foo(), dsl -> dsl
						.foo("foo")
						.bar()));

		ConfigurableApplicationContext context = application.run(args);
		System.out.println(context.getBean("bar"));

	}

}