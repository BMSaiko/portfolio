# Git Management Guidelines

We use three primary branches in our Git workflow:

## Main Branch

- **`main`** is the production branch where the **final code** from each iteration/sprint is merged.
- **Do not develop directly on `main`.** Only pull requests from the `development` branch are merged into `main`.

## Development Branch

- **`development`** is the main branch for ongoing development work.
- All branches for each **User Story (US)** or **subtask** should be created from `development`.
- Once a US or subtask is complete, create a pull request to merge it back into `development`.

## Documentation Branch

- **`documentation`** is the dedicated branch for documentation updates.
- Documentation changes should be made here, with pull requests merged back into `development` upon completion.

## Workflow for Creating and Merging Branches

1. **Creating a Branch for a US or Subtask**  
   - Go to the corresponding issue and create a branch directly from there.
   - GitHub automatically includes the **ID** and **name of the US/subtask** in the branch name. Use this format to maintain consistency, such as “1-USX-product-inventory”.

2. **Completing and Merging a US or Subtask**  
   - Once finished, create a pull request to merge the branch into `development`.

3. **Handling Bugs**  
   - If a bug is identified, open a new **issue for the fix**.
   - Create a branch specifically for the fix under the associated US/subtask, if applicable.
   - Once resolved, submit a pull request to `development`.

## Additional Best Practices

1. **Standard Branch Naming**  
   - Use the GitHub-generated naming convention, which includes the **ID** followed by the **US/subtask name**. For example: “1-USX-product-inventory”.

2. **Pull Request (PR) Review**  
   - Ensure that each PR undergoes at least one review before being approved. This step helps maintain code quality and prevents bugs from being propagated to `development` or `main`.

3. **Consistent Commit Messages**  
   - Use clear and consistent commit messages, such as “fix: resolved issue with inventory display” or “feat: added sorting to product list,” to improve traceability.

4. **Definition of Ready for Merge**  
   - Clearly define when a branch is considered ready for merge, such as by passing all automated tests and obtaining code review approval. This ensures a consistent quality standard.

5. **Cleaning Up Old Branches**  
   - Periodically delete merged or outdated branches to prevent clutter and confusion in the repository.
