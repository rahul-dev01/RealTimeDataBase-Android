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
    private var employeeKey: String? = null


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
        val updateBtn = findViewById<Button>(R.id.btnUpdate)
        val deleteBtn = findViewById<Button>(R.id.btnDelete)


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

                                employeeKey = data.key

                                val employee = data.getValue(Employee::class.java)
                                employee?.let {

                                    tvResult.text =
                                        "Employee Name : ${it.empName}\n" +
                                                "Employee Age  : ${it.empAge}\n" +
                                                "Employee Salary : ${it.empSalary}"
                                    empName.setText(it.empName)
                                    empAge.setText(it.empAge)
                                    empSalary.setText(it.empSalary)
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


        updateBtn.setOnClickListener {

            if (employeeKey == null) {
                Toast.makeText(this, "Fetch data first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedEmployee = Employee(
                empName.text.toString(),
                empAge.text.toString(),
                empSalary.text.toString()
            )

            database.child(employeeKey!!)
                .setValue(updatedEmployee)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Updated Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                }
        }



        deleteBtn.setOnClickListener {

            if (employeeKey == null) {
                Toast.makeText(this, "Fetch data first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            database.child(employeeKey!!)
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Deleted", Toast.LENGTH_SHORT).show()

                    tvResult.text = ""
                    empName.text.clear()
                    empAge.text.clear()
                    empSalary.text.clear()
                    employeeKey = null
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show()
                }
        }



    }
}