package com.appian.intellij.k3.psi;

import com.appian.intellij.k3.KLanguage;
import com.intellij.psi.tree.IElementType;

public final class KTokenType extends IElementType {

  public KTokenType(String debugName) {
    super(debugName, KLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return super.toString();
  }

}
