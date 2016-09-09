/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.test.context;

/**
 * Holder to easily obtain the current {@code TestContext} for the test execution.
 *
 * The {@code TestContext} will be set before the test method is called and cleaned when the test has finished
 * execution. This is respectivly done in the {@code TestContextManager#beforeTestMethod}
 * and @{@code TextContextManager#afterTestMethod}.
 *
 * @author Marten Deinum
 * @since 5.3
 */
public abstract class TestContextHolder {

    private static final ThreadLocal<TestContext> testContextHolder = new ThreadLocal<>();

    static void setTestContext(TestContext testContext) {
        testContextHolder.set(testContext);
    }

    static void resetTestContext() {
        testContextHolder.remove();
    }

    public static TestContext currentTestContext() {
        TestContext currentTestContext = testContextHolder.get();
        if (currentTestContext == null) {
            throw new IllegalStateException("No thread-bound TestContext found.");
        }
        return currentTestContext;
    }




}
