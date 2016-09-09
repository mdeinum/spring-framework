/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.support;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextHolder;

/**
 * Test based {@link org.springframework.beans.factory.config.Scope}
 * implementation.
 *
 * <p>Relies on a thread-bound {@link TestContext} instance, which
 * will  be exported through {@link org.springframework.test.context.TestContextManager}.
 *
 * The scope itself is registered by the {@code TestScopeCustomizer} when the {@code ApplicationContext} is being
 * prepared and loaded for the test at hand.
 *
 * @author Marten Deinum
 * @since 5.3
 */
class TestScope implements Scope {

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {

        final TestContext context = getCurrentTestContext();
        Object scopedObject = context.getAttribute(name);
        if (scopedObject == null) {
            scopedObject = objectFactory.getObject();
            context.setAttribute(name, scopedObject);
        }
        return scopedObject;
    }

    @Override
    public Object remove(String name) {

        final TestContext context = getCurrentTestContext();
        Object scopedObject = context.getAttribute(name);
        if (scopedObject != null) {
            context.removeAttribute(name);
        }
        context.removeDestructionCallback(name);
        return scopedObject;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        getCurrentTestContext().registerDestructionCallback(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {

        final TestContext context = getCurrentTestContext();
        return String.format("%s_%s_%s", context.getTestClass().getSimpleName(), context.getTestMethod().getName(), Thread.currentThread().getName());
    }

    private TestContext getCurrentTestContext() {
        return TestContextHolder.currentTestContext();
    }

}
