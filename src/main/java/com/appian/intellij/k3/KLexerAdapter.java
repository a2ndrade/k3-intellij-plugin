package com.appian.intellij.k3;

import java.io.Reader;

import com.intellij.lexer.FlexAdapter;

public final class KLexerAdapter extends FlexAdapter {
  public KLexerAdapter() {
    super(new KLexer((Reader)null));
  }
}
