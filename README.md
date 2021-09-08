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

1. Go to `Preferences` -> `Plugins`
1. Click on `Browse Repositories...` and select the `Languages` category
1. Look for the `k3` plugin and click `Install`

## Contributing
### Getting Set Up

1. Make sure the `Grammar-Kit` and `PsiViewer` plugins are installed
1. Create a **fork** of this repository and **clone** it locally
1. Open the repository in IntelliJ via the "**Open**" option on the splash page
1. Open the Gradle tool window (`View` -> `Tool Windows` -> `Gradle`) and click on "**Refresh all Gradle Projects**"
1. From the command line, **run** `./gradlew compileJava compileTestJava`
1. **Build** the project (`Build` -> `Build Project`)

### Submitting Changes

1. Import and **use** the project [codestyle](codestyle.xml) (`Editor` -> `Code Style` -> `Scheme` -> `Import...`)
1. Push your work to a **branch** in your fork
1. Open a **Pull Request** and tag me (@a2ndrade)
