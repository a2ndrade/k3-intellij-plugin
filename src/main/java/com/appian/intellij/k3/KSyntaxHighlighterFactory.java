package com.appian.intellij.k3;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public final class KSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
  @Override
  public SyntaxHighlighter getSyntaxHighlighter(
    Project project, VirtualFile virtualFile) {
    return new KSyntaxHighlighter();
  }
}
