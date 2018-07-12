package com.appian.intellij.k3;

import com.intellij.lang.Language;

/**
 * K Intellij integration.
 *
 * @author antonio.andrade
 */
public final class KLanguage extends Language {

  public static final KLanguage INSTANCE = new KLanguage();

  private KLanguage() {
    super("k3");
  }

}
