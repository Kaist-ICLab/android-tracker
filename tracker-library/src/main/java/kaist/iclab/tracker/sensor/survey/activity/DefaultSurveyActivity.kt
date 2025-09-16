package kaist.iclab.tracker.sensor.survey.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kaist.iclab.tracker.sensor.survey.activity.ui.DefaultSurveyTheme
import kaist.iclab.tracker.sensor.survey.question.CheckboxQuestion
import kaist.iclab.tracker.sensor.survey.question.Question
import kaist.iclab.tracker.sensor.survey.question.RadioQuestion
import kaist.iclab.tracker.sensor.survey.question.TextQuestion
import kotlinx.coroutines.flow.collectLatest

class DefaultSurveyActivity: SurveyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DefaultSurveyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SurveyScreen(
                        questionList = survey.getFlatQuestions(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SurveyScreen(
    questionList: List<Question<*>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxSize()
    ) {
        items(questionList) { question ->
            when(question) {
                is RadioQuestion -> RadioQuestion(question)
                is CheckboxQuestion -> CheckboxQuestion(question)
                is TextQuestion -> TextQuestion(question)
            }
        }
    }
}

@Composable
fun RadioQuestion(
    question: RadioQuestion,
    modifier: Modifier = Modifier
) {
    val response = question.response.collectAsState()
    val isHidden = question.isHidden.collectAsState()

    if(isHidden.value) return

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()

    ) {
        Text(
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            text = question.question,
        )
        question.option.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = (option == response.value),
                    onClick = { question.setResponse(option) },
                    modifier = Modifier.size(20.dp),
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = question.optionDisplayText?.get(index) ?: option
                )
            }
        }
    }
}

@Composable
fun CheckboxQuestion(
    question: CheckboxQuestion,
    modifier: Modifier = Modifier
) {
    val response = question.response.collectAsState()
    val isHidden = question.isHidden.collectAsState()

    if(isHidden.value) return

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            text = question.question,
        )
        question.option.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = (option in response.value),
                    onCheckedChange = { isChecked -> question.toggleResponse(option, isChecked)},
                    modifier = Modifier.size(20.dp)
                )
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Text(
                    text = question.optionDisplayText?.get(index) ?: option
                )
            }
        }
    }
}

@Composable
fun TextQuestion(
    question: TextQuestion,
    modifier: Modifier = Modifier
) {
    val response = question.response.collectAsState()
    val isHidden = question.isHidden.collectAsState()

    if(isHidden.value) return

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            text = question.question,
        )
        TextQuestionInput(
            value = response.value,
            onValueChange = { question.setResponse(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextQuestionInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialValue = remember(value) { value }
    val interactionSource = remember { MutableInteractionSource() }
    val state = rememberTextFieldState(initialValue)
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collectLatest {
           onValueChange(it)
        }
    }

    BasicTextField(
        state = state,
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .clearFocusOnKeyboardDismiss(),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorator = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value,
                innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(horizontal = 2.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                )
            )
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeIsVisible = WindowInsets.isImeVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}