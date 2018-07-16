# k3-intellij-plugin
k3 language (kx.com) plugin for IntelliJ IDEA. For q/k4 support, see [q-intellij-plugin](https://github.com/a2ndrade/q-intellij-plugin)

## Features

This plugin supports k3 syntax. Features include:

- Syntax highlighting
- Navigate to declaration
- Code completion
- Find usages
- Rename refactoring
- File structure
- Go to symbol
- Color settings
- Code folding

## Installation

0. Go to `Preferences` -> `Plugins`
0. Click on `Browse Repositories...` and select the `Languages` category
0. Look for the `k3` plugin and click `Install`

## Building from sources

0. Configure an IntelliJ Platform SDK following [these instructions](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html).
0. Make sure the `Grammar-Kit` and `PsiViewer` plugins are installed.
0. Clone this repository.
0. Open it as an IntelliJ IDEA project.
0. Open `src/main/resources/com/appian/intellij/k/k.flex` file and generate the lexer code (*)
0. Open `src/main/resources/com/appian/intellij/k/k.bnf` file and generate the parser code (*)
0. Build the project. Make sure the project is configured to use the IntelliJ Platform SDK configured in step 1.

(*) either from a context menu or using the keyboard shortcut ⇧⌘G
