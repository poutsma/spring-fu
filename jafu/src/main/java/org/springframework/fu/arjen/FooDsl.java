package org.springframework.fu.arjen;

import java.util.function.Function;

import org.springframework.context.support.GenericApplicationContext;

/**
 * @author Arjen Poutsma
 */
public class FooDsl extends DefaultDsl {

	public FooDsl(GenericApplicationContext context) {
		super(context);
	}

	public FooDsl foo(String bar) {
		context().registerBean("foo", Foo.class, () -> new Foo(bar));
		return this;
	}

	public FooDsl bar() {
		context().registerBean("bar", Bar.class, () -> new Bar(ref(Foo.class)));
		return this;
	}

	public static Function<GenericApplicationContext, FooDsl> foo() {
		return FooDsl::new;
	}


	public static class Foo {

		private final String s;

		public Foo(String s) {
			this.s = s;
		}

		@Override
		public String toString() {
			return s;
		}
	}


	public static class Bar {
		private final Foo foo;

		public Bar(Foo foo) {
			this.foo = foo;
		}

		@Override
		public String toString() {
			return foo.toString();
		}
	}

}
