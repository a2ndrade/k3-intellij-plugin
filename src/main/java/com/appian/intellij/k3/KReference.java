package com.appian.intellij.k3;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.appian.intellij.k3.psi.KAssignment;
import com.appian.intellij.k3.psi.KLambda;
import com.appian.intellij.k3.psi.KLambdaParams;
import com.appian.intellij.k3.psi.KNamespaceDeclaration;
import com.appian.intellij.k3.psi.KUserId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;

public final class KReference extends PsiReferenceBase<PsiElement> implements PsiReference {

  public KReference(KUserId element, TextRange textRange) {
    super(element, textRange);
  }

  @Override
  public PsiElement resolve() {
    final PsiElement reference = resolve0();
    // avoid including a variable declaration as a reference to itself
    return reference == myElement ? null : reference;
  }

  private PsiElement resolve0() {
    final VirtualFile sameFile = myElement.getContainingFile().getOriginalFile().getVirtualFile();
    if (sameFile == null || sameFile.getCanonicalPath() == null) {
      return null;
    }
    final String sameFilePath = sameFile.getCanonicalPath();
    final PsiElement context = myElement.getContext();
    if (context instanceof KNamespaceDeclaration) {
      return null;
    }
    final Project project = myElement.getProject();
    final KUserId reference = (KUserId)this.myElement;
    final String referenceName = reference.getName();
    final KUserIdCache cache = KUserIdCache.getInstance();
    final KLambda enclosingLambda = PsiTreeUtil.getContextOfType(this.myElement, KLambda.class);
    final KUserId foundInSameFile = Optional.ofNullable(enclosingLambda)
        .map(KLambda::getLambdaParams)
        .map(KLambdaParams::getUserIdList)
        .orElse(Collections.emptyList())
        .stream()
        .filter(id -> referenceName.equals(id.getName())) // 1) check the enclosing enclosingLambda params
        .findFirst()
        .orElse(PsiTreeUtil.findChildrenOfType(enclosingLambda, KAssignment.class) // 2) check locals
            .stream()
            .map(KAssignment::getUserId)
            .filter(id -> referenceName.equals(id.getName()))
            .findFirst()
            .orElseGet(() -> {
              // 3) check same-file globals
              if (KUtil.isAbsoluteId(referenceName)) {
                return cache.findFirstExactMatch(project, sameFile, referenceName);
              }
              // transform relative reference into an absolute one using its current namespace
              final String currentNs = KUtil.getCurrentNamespace(reference);
              final String fqn = KUtil.generateFqn(currentNs, referenceName);
              return cache.findFirstExactMatch(project, sameFile, fqn);
            }));
    if (foundInSameFile != null) {
      return foundInSameFile;
    }
    // 4) check other file's globals
    final String fqnOrName = KUtil.getFqnOrName(reference);
    final Collection<VirtualFile> otherFiles = FileTypeIndex.getFiles(KFileType.INSTANCE,
        GlobalSearchScope.allScope(project));
    for (VirtualFile otherFile : otherFiles) {
      if (sameFilePath.equals(otherFile.getCanonicalPath())) {
        continue; // already processed above
      }
      final KUserId foundInOtherFile = cache.findFirstExactMatch(project, otherFile, fqnOrName);
      if (foundInOtherFile != null) {
        return foundInOtherFile;
      }
    }
    return null;
  }

  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    if (myElement instanceof KUserId) {
      // inline rename
      ((KUserId) myElement).setName(newElementName);
      KUserIdCache.getInstance().remove(myElement.getContainingFile().getVirtualFile());
      return myElement;
    }
    // cross-language rename
    return super.handleElementRename(newElementName);
  }
}
