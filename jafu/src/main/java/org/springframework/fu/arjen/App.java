package org.springframework.fu.arjen;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerInitializer;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.JafuApplication;

/**
 * @author Arjen Poutsma
 */
public class App implements ApplicationContextInitializer<GenericApplicationContext> {

	private final Consumer<AppDsl> consumer;

	protected App(Consumer<AppDsl> consumer) {
		this.consumer = consumer;
	}

	public ConfigurableApplicationContext run(String[] args) {
		SpringApplication app = new SpringApplication(JafuApplication.class) {
			@Override
			protected void load(ApplicationContext context, Object[] sources) {
				// We don't want the annotation bean definition reader
			}
		};
		app.setApplicationContextClass(ServletWebServerApplicationContext.class);

		app.addInitializers(this);
		System.setProperty("spring.backgroundpreinitializer.ignore", "true");
		return app.run(args);
	}


	public static App application(Consumer<AppDsl> consumer) {
		return new App(consumer);
	}

	@Override
	public void initialize(GenericApplicationContext applicationContext) {
		new ServletWebServerInitializer(new ServerProperties(), new WebMvcProperties(), new ResourceProperties()).initialize(applicationContext);
		this.consumer.accept(new AppDsl(applicationContext));
	}
}
