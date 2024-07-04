package com.appian.intellij.k3;

import com.intellij.lexer.FlexAdapter;

public final class KLexerAdapter extends FlexAdapter {
  public KLexerAdapter() {
    super(new KLexer(null));
  }
}
