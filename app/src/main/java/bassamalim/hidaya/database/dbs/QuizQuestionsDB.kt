package bassamalim.hidaya.database.dbs

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "quiz_questions")
class QuizQuestionsDB(
    @field:ColumnInfo(name = "question_id") @field:PrimaryKey private val question_id: Int,
    @field:ColumnInfo(name = "question_text") private val question_text: String?,
    @field:ColumnInfo(name = "correct_answer_id") private val correct_answer_id: Int
) : Serializable {
    fun getQuestion_id(): Int {
        return question_id
    }

    fun getQuestion_text(): String {
        return question_text!!
    }

    fun getCorrect_answer_id(): Int {
        return correct_answer_id!!
    }
}