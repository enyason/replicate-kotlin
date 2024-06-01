# Contributing Guidelines for Replicate-Kotlin

We appreciate your interest in contributing to Replicate-Kotlin! This document outlines the process for submitting code contributions and improvements to the project.


## Getting Started

1. **Fork the Repository:** Create a fork of the Replicate-Kotlin repository on GitHub. This allows you to make changes and submit pull requests.
2. **Set Up Development Environment:** Ensure you have the necessary tools to develop in Kotlin. Refer to the [official Kotlin documentation](https://kotlinlang.org/docs/home.html) for setup instructions.

## Coding Style

1. **Follow Kotlin Conventions:** Adhere to the standard Kotlin coding conventions for consistent code formatting and readability.
2. **Enforce Formatting with Ktlint:** We use Ktlint, a static code analysis tool, to automatically format Kotlin code according to a defined style.
    * **Check for Ktlint errors:** Run the following command in your terminal to check for formatting inconsistencies:

    ```bash
    ./gradlew ktlintCheck
    ```
    * **Fix formatting issues:** If the `ktlintCheck` command reports errors, you can fix them automatically using the following command:

    ```bash
    ./gradlew ktlintFormat
    ```

## Identify an Issue

You can choose an existing issue to start working on or suggest a new issue for a feature or bug fix [here](https://github.com/enyason/replicate-kotlin/issues).

## Work on a Branch

Create a new branch from the `main` branch for your specific contribution.

## Testing

Ensure your changes are accompanied by relevant unit tests written in Kotlin. This helps maintain code quality and prevent regressions.

## Pull Requests

1. **Push Your Changes:** Once your changes are complete and tested, push your branch to your forked repository on GitHub.
2. **Create a Pull Request:** Go to your forked repository on GitHub and create a pull request from your branch to the main branch of the upstream repository.
3. **Address Feedback:** We will review your pull request and provide feedback. Be prepared to address any comments or suggestions before your contribution is merged.

## Licensing

All contributions must comply with the License under which this project is released. Refer to the [LICENSE](LICENSE) file for details.


## Additional Notes

Feel free to reach out to the project maintainers with any questions or suggestions.


Thank you for your contribution to Replicate-Kotlin!