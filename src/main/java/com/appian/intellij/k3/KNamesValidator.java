package com.appian.intellij.k3;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;

public class KNamesValidator implements NamesValidator {

  private static final Predicate<String> IDENTIFIER = Pattern.compile("[.a-zA-Z][._a-zA-Z0-9]*").asPredicate();
  private static final String[] KEYWORDS = new String[] {"if","do","while"};

  @Override
  public boolean isKeyword(@NotNull String name, Project project) {
    for (String keyword : KEYWORDS) {
      if (keyword.equals(name)) {
          return true;
      }
    }
    return false;
  }

  @Override
  public boolean isIdentifier(@NotNull String name, Project project) {
    return IDENTIFIER.test(name);
  }
}
