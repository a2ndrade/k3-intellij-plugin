package com.appian.intellij.k3;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.appian.intellij.k3.psi.KAssignment;
import com.appian.intellij.k3.psi.KLambda;
import com.appian.intellij.k3.psi.KTypes;
import com.appian.intellij.k3.psi.KUserId;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;

public class KCompletionContributor extends CompletionContributor {

  private static final String[] SYSTEM_FNS_K3 = new String[] {"_a", "_abs", "_acos", "_asin", "_atan", "_bd",
      "_bin", "_binl", "_ci", "_cos", "_cosh", "_d", "_db", "_di", "_div", "_dj", "_dot", "_draw", "_dv",
      "_dvl", "_exit", "_exp", "_f", "_floor", "_getenv", "_gtime", "_h", "_host", "_i", "_ic", "_in", "_inv",
      "_jd", "_k", "_lin", "_log", "_lsq", "_lt", "_mul", "_n", "_p", "_setenv", "_sin", "_sinh", "_size",
      "_s", "_sm", "_sqr", "_sqrt", "_ss", "_ssr", "_sv", "_T", "_t", "_tan", "_tanh", "_u", "_v", "_vs",
      "_w"};

  static {
    Arrays.sort(SYSTEM_FNS_K3);
  }

  public KCompletionContributor() {
    extend(CompletionType.BASIC,
        PlatformPatterns.psiElement(KTypes.USER_IDENTIFIER).withLanguage(KLanguage.INSTANCE),
        new CompletionProvider<>() {
          public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
            final CompletionResultSet caseInsensitiveResultSet = resultSet.caseInsensitive();
            final PsiElement element = parameters.getOriginalPosition();
            if (element == null) {
              return;
            }
            final String input = element.getText() == null ? "" : element.getText();
            final KLambda enclosingLambda = PsiTreeUtil.getContextOfType(element, KLambda.class);
            // system functions
            contributeSystemFunctions(resultSet, input);
            // params
            final Map<String,KUserId> uniques = new LinkedHashMap<>();
            Optional.ofNullable(enclosingLambda)
                .map(KLambda::getLambdaParams)
                .map(params -> params.getUserIdList()
                    .stream()
                    .filter(param -> param.getName().contains(input)))
                .orElse(Stream.empty())
                .forEach(param -> uniques.putIfAbsent(param.getName(), param));
            // locals
            Optional.ofNullable(PsiTreeUtil.findChildrenOfType(enclosingLambda, KAssignment.class).stream()
                .map(KAssignment::getUserId)
                .filter(id -> id.getName().contains(input)))
                .orElse(Stream.empty())
                .forEach(local -> uniques.putIfAbsent(local.getName(), local));
            for (KUserId local : uniques.values()) {
              caseInsensitiveResultSet.addElement(LookupElementBuilder.create(local));
            }
            // globals (same file)
            final Project project = element.getProject();
            final VirtualFile sameFile = element.getContainingFile().getVirtualFile();
            Optional.ofNullable(KUtil.findIdentifiers(project, sameFile).stream()
                .filter(id -> id.getName().contains(input)))
                .orElse(Stream.empty())
                .forEach(global -> caseInsensitiveResultSet.addElement(LookupElementBuilder.create(global)));
            // globals (other files)
            if (caseInsensitiveResultSet.isStopped()) {
              return;
            }
            final String sameFilePath = sameFile.getCanonicalPath();
            final KUserIdCache cache = KUserIdCache.getInstance();
            final Collection<VirtualFile> otherFiles = FileTypeIndex.getFiles(KFileType.INSTANCE,
                GlobalSearchScope.allScope(project));
            for (VirtualFile otherFile : otherFiles) {
              if (sameFilePath.equals(otherFile.getCanonicalPath())) {
                continue; // already processed above
              }
              Optional.ofNullable(cache.findAllIdentifiers(project, otherFile, input, new KUtil.PrefixMatcher(input)).stream())
                  .orElse(Stream.empty())
                  .forEach(global -> {
                    final String fqn = KUtil.getFqn(global);
                    final LookupElementBuilder lookup = fqn == null ?
                        LookupElementBuilder.create(global) :
                        LookupElementBuilder.create(global, fqn);
                    caseInsensitiveResultSet.addElement(lookup);
                  });
            }
          }
        }
    );
  }

  private void contributeSystemFunctions(CompletionResultSet resultSet, String input) {
    if (input.charAt(0) == '.') {
      return; // ignore
    }
    int i = Math.abs(Arrays.binarySearch(SYSTEM_FNS_K3, input) + 1);
    while (i < SYSTEM_FNS_K3.length && SYSTEM_FNS_K3[i].startsWith(input)) {
      resultSet.addElement(LookupElementBuilder.create(SYSTEM_FNS_K3[i++]));
    }
  }

}
