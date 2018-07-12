package com.appian.intellij.k3.psi.impl;

import com.appian.intellij.k3.KAstWrapperPsiElement;
import com.appian.intellij.k3.psi.KNamedElement;
import com.intellij.lang.ASTNode;

public abstract class KNamedElementImpl extends KAstWrapperPsiElement implements KNamedElement {
  public KNamedElementImpl(ASTNode node) {
    super(node);
  }
}
