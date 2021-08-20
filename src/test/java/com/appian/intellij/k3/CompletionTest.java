package com.appian.intellij.k3;

import java.util.List;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class CompletionTest extends BasePlatformTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected String getTestDataPath() {
    return "src/test/resources/" + getClass().getName().replace('.', '/');
  }

  public void testGlobals() {
    testCompletion(new String[] {"globals1.k", "globals2.k"}, "square", "sum", "someFnInDefaultNsInAnotherFile");
  }

  public void testSystemFns_k_k() {
    testCompletion("system_fns_k.k", "_bd", "_bin", "_binl");
  }

  public void testSystemFns_k_q() {
    testCompletion("system_fns_k.q"); // none expected
  }

  public void testSystemFns_q_q() {
    testCompletion("system_fns_q.q"); // none expected
  }

  public void testSystemFns_q_k() {
    testCompletion("system_fns_q.k"); // none expected
  }

  private void testCompletion(String fileName, String... expectedSuggestions) {
    testCompletion(new String[] {fileName}, expectedSuggestions);
  }

  private void testCompletion(String[] fileNames, String... expectedSuggestions) {
    myFixture.configureByFiles(fileNames);
    myFixture.complete(CompletionType.BASIC, 1);
    final List<String> strings = myFixture.getLookupElementStrings();
    assertOrderedEquals(strings, expectedSuggestions);
  }
}
