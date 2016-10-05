package com.simpleentity.serialize2.integration.testclasses;

public class OuterClass {
	public String text = "The outer class should have some text too.";

	public class InnerClass {
		public String name = "The text has been lost.";
		public int value = 73;

		public String getText() {
			return text;
		}
	}
}