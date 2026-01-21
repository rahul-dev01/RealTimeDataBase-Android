package com.example.realtimedatabase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val empName = findViewById<EditText>(R.id.etEmpName)
        val empAge = findViewById<EditText>(R.id.etEmpAge)
        val empSalary = findViewById<EditText>(R.id.etEmpSalary)

        val tvResult = findViewById<TextView>(R.id.tvResult)

        val submitBtn = findViewById<Button>(R.id.btnSubmit)
        val fetchBtn = findViewById<Button>(R.id.btnFetch)

        database = FirebaseDatabase.getInstance().reference.child("Employees")

        submitBtn.setOnClickListener {
            val name = empName.text.toString().trim()
            val age = empAge.text.toString().trim()
            val salary = empSalary.text.toString().trim()

            if (name.isEmpty() || age.isEmpty() || salary.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val employee = Employee(name, age, salary)

            val key = database.push().key!!

            database.child(key).setValue(employee)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
                    empName.text.clear()
                    empAge.text.clear()
                    empSalary.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                }

        }


        fetchBtn.setOnClickListener {
            database.limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            for (data in snapshot.children) {
                                val employee = data.getValue(Employee::class.java)
                                employee?.let {

                                    tvResult.text =
                                        "Employee Name : ${it.empName}\n" +
                                                "Employee Age  : ${it.empAge}\n" +
                                                "Employee Salary : ${it.empSalary}"
                                }
                            }
                        } else {
                            tvResult.text = "No data found"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        tvResult.text = error.message
                    }
                })
        }
    }
}