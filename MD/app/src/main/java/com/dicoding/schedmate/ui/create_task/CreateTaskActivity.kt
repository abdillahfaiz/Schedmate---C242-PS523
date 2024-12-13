package com.dicoding.schedmate.ui.create_task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.databinding.ActivityCreateTaskBinding
import com.dicoding.schedmate.ui.detail_task.DetailTaskActivity
import com.dicoding.schedmate.ui.detail_task.DetailTaskActivity.Companion
import java.util.Calendar

class CreateTaskActivity : AppCompatActivity() {

    companion object {
        const val ID_CLASS = "id_class"
        const val RESULT_CODE = 120
        const val RESULT_UPDATE = "result_update"
    }

    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var viewModel: CreateTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val idClass = intent.getIntExtra(ID_CLASS, -1)
        val userPreference = UserPreference.getInstance(dataStore)
        val factory = CreateTaskViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[CreateTaskViewModel::class.java]

        binding.btnCreateTask.setOnClickListener {
            val title = binding.etTitleTask.text.toString().trim()
            val desc = binding.etDescTask.text.toString().trim()
            val deadline = binding.etDeadlineDate.text.toString() + " " + binding.etDeadlineTime.text.toString().trim()
            val mapel = binding.dropdownMapel.text.toString().trim()
            val category = binding.dropdownKategori.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty() || deadline.isEmpty() || mapel.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Data tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show()
            }else {
                viewModel.addTask(idClass, title, desc, deadline, category, mapel)
            }

        }



        viewModel.isLoading.observe(this) {isLoading ->
            binding.btnCreateTask.text = if (isLoading) "Loading..." else "Simpan"
        }

        viewModel.createTaskRes.observe(this) { response ->
                Log.d("CreateTaskActivity", "onCreate: ${response.message}")
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                val result = Intent()
                result.putExtra(RESULT_UPDATE, true)
                setResult(RESULT_CODE, result)
                finish()
        }

        viewModel.errorMessage.observe(this) {error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }




        val listMapel = resources.getStringArray(R.array.mapel)
        val listMapelUpdate = ArrayAdapter(this, R.layout.dropdown_item, listMapel)

        val listCategory = resources.getStringArray(R.array.category)
        val listCategoryUpdate = ArrayAdapter(this, R.layout.dropdown_item, listCategory)

        binding.dropdownMapel.setAdapter(listMapelUpdate)
        binding.dropdownKategori.setAdapter(listCategoryUpdate)
        binding.etDeadlineDate.setOnClickListener{
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = (year.toString() + "-" + (monthOfYear + 1) + "-" +  dayOfMonth.toString()  )
                    binding.etDeadlineDate.setText(dat)
                },
                year,
                month,
                day,
            )

            datePickerDialog.show()
        }

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        binding.etDeadlineTime.setOnClickListener {
            val timePicker: TimePickerDialog = TimePickerDialog(
                this, timePickerDialogListener, 24, 10, true
            )
            timePicker.show()
        }




    }

    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                binding.etDeadlineTime.setText(formattedTime)
            }
        }
}