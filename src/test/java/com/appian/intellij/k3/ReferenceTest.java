package com.appian.intellij.k3;

import org.junit.Test;

import com.appian.intellij.k3.psi.KAssignment;
import com.appian.intellij.k3.psi.KUserId;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class ReferenceTest extends BasePlatformTestCase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected String getTestDataPath() {
    return "src/test/resources/" + getClass().getName().replace('.', '/');
  }
  @Test
  public void testNoNs() {
    assertReferenceFound("usage_no_ns.k", "noNs", null);
  }

  @Test
  public void testUsageUnderNs() {
    assertReferenceFound("usage_under_ns.k", "noNs", null);
  }

  @Test
  public void testImplicitNs() {
    assertReferenceFound("usage_implicit_ns.k", "implicitNs", ".a.b.implicitNs");
  }

  @Test
  public void testExplicitNs() {
    assertReferenceFound("usage_explicit_ns.k", ".a.b.explicitNs", null);
  }

  @Test
  public void testOtherExplicitNs() {
    assertReferenceFound("usage_other_explicit_ns.k", ".x.otherExplicitNs", null);
  }

  @Test
  public void testPopLastNs() {
    assertReferenceFound("usage_pop_last_ns.k", "popLastLevelNs", ".a.popLastLevelNs");
  }

  @Test
  public void testImplicitSubNs() {
    assertReferenceFound("usage_implicit_sub_ns.k", "implicitSubNs", ".a.c.implicitSubNs");
  }

  @Test
  public void testOtherImplicitNs() {
    assertReferenceFound("usage_other_implicit_ns.k", "otherImplicitNs", ".x.otherImplicitNs");
  }

  @Test
  public void testExplicitRootNs() {
    assertReferenceFound("usage_explicit_root_ns.k", ".explicitRootNs", null);
  }

  @Test
  public void testImplicitRootNs() {
    assertReferenceFound("usage_implicit_root_ns.k", "implicitRootNs", ".implicitRootNs");
  }

  @Test
  public void testOtherNoNs(){
    assertReferenceFound("usage_other_no_ns.k", "otherNoNs", null);
  }

  @Test
  public void testNotFound() {
    assertReferenceNotFound("usage_only_ns_found.k");
  }

  @Test
  public void testDefWithImplicitNs() {
    assertReferenceNotFound("def_with_implicit_ns.k");
  }

  private void assertReferenceFound(String fromFile, String expectedId, String expectedFqnId) {
    assertReferenceFound(fromFile, expectedId, expectedFqnId, "references.k");
  }

  private void assertReferenceFound(
      String fromFile, String expectedId, String expectedFqnId, String expectedFile) {
    myFixture.configureByFiles(fromFile, "references.k");
    final PsiReference from = myFixture.getReferenceAtCaretPosition(fromFile);
    final PsiElement to = from.resolve();
    assertNotNull("Unresolved Reference: " + from.getElement().getText() + " (" +fromFile +")", to);
    assertInstanceOf(to, KUserId.class);
    assertInstanceOf(to.getContext(), KAssignment.class);
    assertEquals("Resolved to unexpected file: ", expectedFile,
        to.getContainingFile().getVirtualFile().getName());
    final String toId = to.getText();
    final String toFqnId= KUtil.getFqn((KUserId)to);
    assertEquals("explicit id", expectedId, toId);
    assertEquals("implicit id", expectedFqnId, toFqnId);
  }

  private void assertReferenceNotFound(
      String fromFile) {
    myFixture.configureByFiles(fromFile, "references.k");
    final PsiReference from = myFixture.getReferenceAtCaretPosition(fromFile);
    final PsiElement to = from.resolve();
    assertNull("Shouldn't have been resolved: " + from.getElement().getText() + " (" +fromFile +")", to);
  }

}
