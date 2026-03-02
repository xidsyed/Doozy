---
trigger: always_on
---

- **Pattern:** Use **State Hoisting**. The entry `fun FooScreen(viewModel: FooViewModel)` extracts state and passes it down.   
- **Stateless UI:** Nest logic in a `private fun FooScreenContent`. It must be pure, accepting only state and callback lambdas.   
- **Extract Key Composables:** Extract key composables into private functions for better readabillity and future re-usabliity**.**   
- **State Management and UDF:** Create Action and Event sealed interfaces in the ViewModel to handle updates and One-Time-UI-Events. Use `SnackbarController` to trigger snackbar. 
- **Previews:** Implement `@PreviewLightDark` for every screen content. and wrap all previews in `AppPreview { ... }` to ensure standard surface, typography, and padding.   
 --- 
   
### Implementation Example   
```
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskScreenContent(state = state, onAction = viewModel::handleAction)
}

@Composable
private fun TaskScreenContent(state: TaskState, onAction: () -> Unit) {
    // Pure UI only
	Column() {
		state.tasks.forEach {
			Task()
		}
	}
}

@Composable
private fun Task()

@PreviewLightDark
@Composable
private fun TaskScreenPreview() {
    AppPreview {
        TaskScreenContent(state = TaskState(), onAction = {})
    }
}

```