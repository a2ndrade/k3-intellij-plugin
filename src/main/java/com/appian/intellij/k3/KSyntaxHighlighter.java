package com.appian.intellij.k3;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

import java.awt.Font;
import java.util.Map;

import com.appian.intellij.k3.psi.KTypes;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;

public final class KSyntaxHighlighter extends SyntaxHighlighterBase {

  public static final TextAttributesKey ADVERB = createTextAttributesKey("K3_ADVERB", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("K3_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
  public static final TextAttributesKey IDENTIFIER_SYS = createTextAttributesKey("K3_SYSFUNCTION", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey SYMBOL = createTextAttributesKey("K3_SYMBOL", DefaultLanguageHighlighterColors.CONSTANT);
  public static final TextAttributesKey NUMBER = createTextAttributesKey("K3_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
  public static final TextAttributesKey KEYWORD = createTextAttributesKey("K3_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey STRING = createTextAttributesKey("K3_STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey BRACES = createTextAttributesKey("K3_BRACES", DefaultLanguageHighlighterColors.BRACES);
  public static final TextAttributesKey BRACKETS = createTextAttributesKey("K3_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
  public static final TextAttributesKey PARENS = createTextAttributesKey("K3_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
  public static final TextAttributesKey COMMENT = createTextAttributesKey("K3_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

  static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("K3_BAD_CHARACTER",
    new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));

  private static final TextAttributesKey[] ADVERB_KEYS = new TextAttributesKey[]{ADVERB};
  private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
  private static final TextAttributesKey[] IDENTIFIER_SYS_KEYS = new TextAttributesKey[]{IDENTIFIER_SYS};
  private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
  private static final TextAttributesKey[] SYMBOL_KEYS = new TextAttributesKey[]{SYMBOL};
  private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
  private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
  private static final TextAttributesKey[] BRACES_KEYS = new TextAttributesKey[]{BRACES};
  private static final TextAttributesKey[] BRACKETS_KEYS = new TextAttributesKey[]{BRACKETS};
  private static final TextAttributesKey[] PARENS_KEYS = new TextAttributesKey[]{PARENS};
  private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};

  private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];
  private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};

  @Override
  public Lexer getHighlightingLexer() {
    return new FlexAdapter(new KLexer(null));
  }

  private static final Map<IElementType, TextAttributesKey[]> M = buildMappings();

  private static Map<IElementType,TextAttributesKey[]> buildMappings() {

      return Map.ofEntries(Map.entry(KTypes.USER_IDENTIFIER, IDENTIFIER_KEYS),
              Map.entry(KTypes.K3_SYSTEM_FUNCTION, IDENTIFIER_SYS_KEYS),
              Map.entry(KTypes.NUMBER, NUMBER_KEYS),
              Map.entry(KTypes.NUMBER_VECTOR, NUMBER_KEYS),
              Map.entry(KTypes.CHAR, STRING_KEYS),
              Map.entry(KTypes.STRING, STRING_KEYS),
              Map.entry(KTypes.SYMBOL, SYMBOL_KEYS),
              Map.entry(KTypes.SYMBOL_VECTOR, SYMBOL_KEYS),
              Map.entry(KTypes.PRIMITIVE_VERB, NUMBER_KEYS),
              Map.entry(KTypes.ADVERB, ADVERB_KEYS),
              Map.entry(KTypes.COMMAND, IDENTIFIER_SYS_KEYS),
              Map.entry(KTypes.CURRENT_NAMESPACE, IDENTIFIER_SYS_KEYS),
              Map.entry(KTypes.COLON, KEYWORD_KEYS),
              Map.entry(KTypes.CONTROL, KEYWORD_KEYS),
              Map.entry(KTypes.CONDITIONAL, KEYWORD_KEYS),
              Map.entry(KTypes.OPEN_BRACE, BRACES_KEYS),
              Map.entry(KTypes.CLOSE_BRACE, BRACES_KEYS),
              Map.entry(KTypes.OPEN_BRACKET, BRACKETS_KEYS),
              Map.entry(KTypes.CLOSE_BRACKET, BRACKETS_KEYS),
              Map.entry(KTypes.OPEN_PAREN, PARENS_KEYS),
              Map.entry(KTypes.CLOSE_PAREN, PARENS_KEYS),
              Map.entry(KTypes.COMMENT, COMMENT_KEYS),
              Map.entry(TokenType.BAD_CHARACTER, BAD_CHAR_KEYS));
  }

  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    final TextAttributesKey[] keys = M.get(tokenType);
    if (keys != null) {
      return keys;
    }
    return EMPTY_KEYS;
  }

}
