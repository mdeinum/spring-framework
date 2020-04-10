package org.springframework.test.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;

public class TestScope implements Scope {

	private static final Log logger = LogFactory.getLog(TestScope.class);

	private final TestContextManager testContextManager;

	public TestScope(TestContextManager testContextManager) {
		this.testContextManager = testContextManager;
	}

	private TestContext getTestContext() {
		return this.testContextManager.getTestContext();
	}

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Object scopedObject = getTestContext().getAttribute(name);
		if (scopedObject == null) {
			scopedObject = objectFactory.getObject();
			getTestContext().setAttribute(name, scopedObject);
		}
		return scopedObject;
	}

	@Override
	@Nullable
	public Object remove(String name) {
		return getTestContext().removeAttribute(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		logger.warn("TestScope does not support destruction callbacks. ");
	}

	@Override
	@Nullable
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return getTestContext().getTestClass() + "."+ getTestContext().getTestMethod();
	}
}
