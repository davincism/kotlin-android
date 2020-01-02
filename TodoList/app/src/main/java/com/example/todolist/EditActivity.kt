package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.util.*

class EditActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()
    val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val id = intent.getLongExtra("id", -1L)
        if (id == -1L) insertMode()
        else updateMode(id)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
    }

    private fun insertMode() {
        deleteFab.hide() //삭제 버튼 감추기
        doneFab.setOnClickListener { insertTodo() }
    }

    private fun updateMode(id: Long) {
        val todo = realm.where<Todo>().equalTo("id", id).findFirst()!!
        todoEditText.setText(todo.title)
        calendarView.date = todo.date

        doneFab.setOnClickListener { updateTodo(id) }
        deleteFab.setOnClickListener { deleteTodo(id) }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun insertTodo() {
        realm.beginTransaction()

        val newItem = realm.createObject<Todo>(nextId())
        newItem.title = todoEditText.text.toString()
        newItem.date = calendar.timeInMillis

        realm.commitTransaction()

        alert("내용이 추가되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    private fun updateTodo(id: Long) {
        realm.beginTransaction()

        val updateItem = realm.where<Todo>().equalTo("id", id).findFirst()!!
        updateItem.title = todoEditText.text.toString()
        updateItem.date = calendar.timeInMillis

        realm.commitTransaction()

        alert("내용이 변경되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    private fun deleteTodo(id: Long) {
        realm.beginTransaction()

        val deleteItem = realm.where<Todo>().equalTo("id", id).findFirst()!!
        deleteItem.deleteFromRealm()

        realm.commitTransaction()

        alert("내용이 변경되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    private fun nextId(): Int {
        val maxId = realm.where<Todo>().max("id")
        if (maxId != null) return maxId.toInt() + 1
        return 0
    }
}
