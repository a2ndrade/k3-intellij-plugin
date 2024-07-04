package com.appian.intellij.k3;

import org.jetbrains.annotations.NotNull;

import com.appian.intellij.k3.psi.KFile;
import com.appian.intellij.k3.psi.KLambda;
import com.appian.intellij.k3.psi.KUserId;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

final class KUnusedVariableHighlightVisitor implements HighlightVisitor {

  private HighlightInfoHolder holder;

  @Override
  public boolean suitableForFile(@NotNull PsiFile file) {
    return file instanceof KFile;
  }

  @Override
  public boolean analyze(
      @NotNull PsiFile file, boolean updateWholeFile, @NotNull HighlightInfoHolder holder, @NotNull Runnable action) {
    this.holder = holder;
    try {
      action.run();
    } finally {
      this.holder = null;
    }
    return true;
  }

  @Override
  public void visit(@NotNull PsiElement element) {
    if (!isUnusedKLocalDeclaration(element)) {
      return;
    }
    // gray out unused variables
    holder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.UNUSED_SYMBOL)
        .range(element)
        .textAttributes(CodeInsightColors.NOT_USED_ELEMENT_ATTRIBUTES)
        .create());
  }

  @NotNull
  @Override
  public HighlightVisitor clone() {
    return new KUnusedVariableHighlightVisitor();
  }

  private static boolean isUnusedKLocalDeclaration(@NotNull PsiElement element) {
    if (!(element instanceof KUserId usage)) {
      return false;
    }
    if (!usage.isDeclaration()) {
      return false; // ignore. we want to highlight declarations, not usages
    }
    final KLambda enclosingLambda = PsiTreeUtil.getContextOfType(usage, KLambda.class);
    if (enclosingLambda == null) {
      return false; // ignore. not a local variable
    }
    final KUserId firstDeclaration = KReference.findLocalDeclaration(usage);
    for (KUserId found : PsiTreeUtil.findChildrenOfType(enclosingLambda, KUserId.class)) {
      if (found.isDeclaration()) {
        continue; // ignore. we're interested in usages, not declarations
      }
      KUserId localDeclaration = KReference.findLocalDeclaration(found);
      if (firstDeclaration == localDeclaration) {
        return false; // at least one usage found!
      }
    }
    return true;
  }
}
