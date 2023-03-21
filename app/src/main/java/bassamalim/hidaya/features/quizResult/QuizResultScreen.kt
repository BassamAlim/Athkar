package bassamalim.hidaya.features.quizResult

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import bassamalim.hidaya.R
import bassamalim.hidaya.core.models.QuizResultQuestion
import bassamalim.hidaya.core.ui.components.*
import bassamalim.hidaya.core.ui.theme.AppTheme

@Composable
fun QuizResultUI(
    vm: QuizResultVM = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()

    MyScaffold(stringResource(R.string.quiz_result)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it).padding(bottom = 5.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                MyText(
                    text = "${stringResource(R.string.your_score_is)} ${state.score}%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textColor = AppTheme.colors.onPrimary,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
                )
            }

            MyLazyColumn(
                lazyList = {
                    items(state.questions) { item ->
                        Question(item)
                    }
                }
            )
        }
    }
}

@Composable
fun Question(question: QuizResultQuestion) {
    MySurface {
        Column(
            Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Question number
            MyText(
                text = "${stringResource(R.string.question)} ${question.questionNum+1}",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 3.dp)
            )

            // Question text
            MyText(
                text = question.questionText,
                modifier = Modifier.padding(vertical = 3.dp)
            )

            MyHorizontalDivider(thickness = 2.dp)

            // Answers
            for (i in 0..3) {
                Answer(
                    ansNum = i,
                    ansText = question.answers[i],
                    correctAns = question.correctAns,
                    chosenAns = question.chosenAns
                )
            }
        }
    }
}

@Composable
private fun Answer(ansNum: Int, ansText: String, correctAns: Int, chosenAns: Int) {
    if (ansNum != 0) MyHorizontalDivider(modifier = Modifier.padding(horizontal = 5.dp))

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MyText(text = ansText, fontSize = 18.sp)

        Image(
            painter = painterResource(
                if (ansNum == correctAns) R.drawable.ic_check
                else R.drawable.ic_wrong
            ),
            contentDescription = "",
            Modifier
                .size(25.dp)
                .alpha(if (ansNum == chosenAns || ansNum == correctAns) 1F else 0F)
        )
    }
}