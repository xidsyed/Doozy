---
trigger: always_on
---


* **Pattern:** Use **State Hoisting**. The entry `fun FooScreen(viewModel: FooViewModel)` extracts state and passes it down.
* **Stateless UI:** Nest logic in a `private fun FooScreenContent`. It must be pure, accepting only state and callback lambdas.
* **Previews:** Implement `@PreviewLightDark` for every screen content. and wrap all previews in `AppPreview { ... }` to ensure standard surface, typography, and padding.

---

### Implementation Example

```kotlin
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskScreenContent(state = state, onAction = viewModel::handleAction)
}

@Composable
private fun TaskScreenContent(state: TaskState, onAction: () -> Unit) {
    // Pure UI only
}

@PreviewLightDark
@Composable
private fun TaskScreenPreview() {
    AppPreview {
        TaskScreenContent(state = TaskState(), onAction = {})
    }
}

```