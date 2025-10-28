package com.example.expensetracker

import android.graphics.Color
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import android.widget.*
import android.view.View
import androidx.compose.ui.text.font.Typeface

class MainActivity : ComponentActivity() {
    private lateinit var numPeopleInput: EditText
    private lateinit var nextButton: Button
    private lateinit var nameContainer: LinearLayout
    private lateinit var confirmNamesButton: Button
    private lateinit var expenseContainer: LinearLayout
    private lateinit var calculateButton: Button
    private lateinit var resultText: TextView

    private val nameInputs = mutableListOf<EditText>()
    private val expenseInputs = mutableMapOf<String, MutableList<EditText>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numPeopleInput = findViewById(R.id.numPeopleInput)
        nextButton = findViewById(R.id.nextButton)
        nameContainer = findViewById(R.id.nameContainer)
        confirmNamesButton = findViewById(R.id.confirmNamesButton)
        expenseContainer = findViewById(R.id.expenseContainer)
        calculateButton = findViewById(R.id.calculateButton)
        resultText = findViewById(R.id.resultText)

        nextButton.setOnClickListener {
            val numPeople = numPeopleInput.text.toString().toIntOrNull()
            if (numPeople == null || numPeople <= 0) {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            nameContainer.removeAllViews()
            nameInputs.clear()
            nameContainer.visibility = View.VISIBLE
            confirmNamesButton.visibility = View.VISIBLE

            for (i in 1..numPeople) {
                val nameInput = EditText(this).apply {
                    hint = "Name of person $i"
                }
                nameInputs.add(nameInput)
                nameContainer.addView(nameInput)
            }
        }

        confirmNamesButton.setOnClickListener {
            expenseContainer.removeAllViews()
            expenseInputs.clear()
            expenseContainer.visibility = View.VISIBLE
            calculateButton.visibility = View.VISIBLE

            for (nameInput in nameInputs) {
                val name = nameInput.text.toString()
                if (name.isBlank()) {
                    Toast.makeText(this, "Please enter all names", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val personLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 16, 0, 16)
                }

                val title = TextView(this).apply {
                    text = "$name's Expenses"
                    textSize = 18f
                    
                }

                val expenseList = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                }

                val addExpenseButton = Button(this).apply {
                    text = "Add Expense"
                }

                val inputs = mutableListOf<EditText>()
                addExpenseButton.setOnClickListener {
                    val expenseInput = EditText(this).apply {
                        hint = "Enter expense"
                        inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                    }
                    inputs.add(expenseInput)
                    expenseList.addView(expenseInput)
                }

                expenseInputs[name] = inputs
                personLayout.addView(title)
                personLayout.addView(addExpenseButton)
                personLayout.addView(expenseList)
                expenseContainer.addView(personLayout)
            }
        }

        calculateButton.setOnClickListener {
            val people = mutableListOf<Pair<String, Double>>()

            for ((name, inputs) in expenseInputs) {
                var totalExpense = 0.0
                for (input in inputs) {
                    val amount = input.text.toString().toDoubleOrNull()
                    if (amount != null) totalExpense += amount
                }
                people.add(name to totalExpense)
            }

            val total = people.sumOf { it.second }
            val perPerson = total / people.size

            val result = StringBuilder()
            result.append("💰 Total Expense: ₹%.2f\n".format(total))
            result.append("📊 Per Person Share: ₹%.2f\n\n".format(perPerson))

            result.append("🧾 Individual Totals:\n")
            for ((name, amount) in people) {
                result.append("- $name spent ₹%.2f\n".format(amount))
            }

            result.append("\n📉 Balances:\n")
            for ((name, amount) in people) {
                val balance = amount - perPerson
                val status = if (balance > 0) "gets back" else "owes"
                result.append("- $name $status ₹%.2f\n".format(kotlin.math.abs(balance)))
            }

            resultText.text = result.toString()
        }
        val clearButton = findViewById<Button>(R.id.clearButton)

        clearButton.setOnClickListener {
            numPeopleInput.text.clear()
            nameContainer.removeAllViews()
            expenseContainer.removeAllViews()
            resultText.text = ""
            nameContainer.visibility = View.GONE
            confirmNamesButton.visibility = View.GONE
            expenseContainer.visibility = View.GONE
            calculateButton.visibility = View.GONE
            nameInputs.clear()
            expenseInputs.clear()
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerTheme {
        Greeting("Android")
    }
}