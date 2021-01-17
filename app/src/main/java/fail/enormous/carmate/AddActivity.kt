package fail.enormous.carmate

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.*
import java.math.BigDecimal

class AddActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var position: Int = 0
    var isEmpty: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        setContentView(R.layout.activity_add)
        fillSpinner()
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.setSlideEdge(Gravity.BOTTOM)
        slide.setDuration(200)
        slide.setInterpolator(DecelerateInterpolator())
        getWindow().setExitTransition(slide)
        getWindow().setEnterTransition(slide)
    }

    private fun fillSpinner() {

        val spinner = findViewById<Spinner>(R.id.type_spinner)
        spinner.onItemSelectedListener = this
        // Filling spinner with list from ArrayAdapter -- layout is from Android
        val typeSpinnerAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
                this,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.type_array))

        // Simple layout (from Android) for each item
        typeSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)

        // Setting the defined adapter
        spinner.adapter = typeSpinnerAdapter
    }

    override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View, position: Int,
            id: Long,
    ) {

        // Do to item if position selected is not the default (blank value)
        /* if (position != 0) {
            // Set value of name selected in lowercase
            // var cartype: String = resources.getStringArray(R.array.type_array)[position].toLowerCase(Locale.ROOT) // tolowercase in English because it's technically better to do so
        } */

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    fun doneButtonPress(view: View) {
        //Log.d(resources.getStringArray(R.array.type_array)[this.position])
        addCar()
    }

    @SuppressLint("DefaultLocale")
    private fun addCar(): Boolean {
        try {
            // Defining input components
            val brandInput = this.findViewById<EditText>(R.id.brand_input)
            val modelInput = this.findViewById<EditText>(R.id.model_input)
            val yearInput = this.findViewById<EditText>(R.id.year_input)
            val colorInput = this.findViewById<EditText>(R.id.color_input)
            val typeInput = this.findViewById<Spinner>(R.id.type_spinner)
            val priceInput = this.findViewById<EditText>(R.id.price_input)
            val plateInput = this.findViewById<EditText>(R.id.plate_input)

            // Grabbing data from input
            val brandContent = brandInput.text.toString().toLowerCase().capitalize()
            val modelContent = modelInput.text.toString()
            val yearContent = yearInput.text.toString().toInt()
            val colorContent = colorInput.text.toString().toLowerCase()
            val typeContent = typeInput.selectedItem.toString()
            val priceContent = priceInput.text.toString().toBigDecimal()
            val plateContent = plateInput.text.toString().toUpperCase()

            Log.w("Content", "$brandContent, $modelContent, $yearContent, $colorContent, $typeContent, $priceContent, $plateContent")

            addCarToJSON(brandContent, modelContent, yearContent, colorContent, typeContent, priceContent, plateContent)
            goToMainActivity()

            return true
        } catch (t: Throwable) {
            Log.w("Error:", t.toString())
            return false
        }
    }

    private fun addCarToJSON(brand: String, model: String, year: Int, color: String, type: String, price: BigDecimal, plate: String) {
        // Put data into JSON array, with love from https://stackoverflow.com/questions/65591615/how-do-i-output-data-as-a-json-array-in-kotlin-on-android
        // After reading comments, I wrote my own answer to my question on this site.

        val carlist = mutableListOf(
                Car(brand, model, year, color, type, price, plate)
        )

        // If the file exists, read from it! If not, don't do anything.
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val filename = "carlist.json"
        val file = File(storageDir, filename)
        if (file.exists()) {
            // Read file
            val jsonFileString = getJsonDataFromAsset(applicationContext, filename)
            if (jsonFileString != "z") {
                Log.w("Data", jsonFileString.toString())

                // Gson
                val gson = Gson()
                val arrayCarType = object : TypeToken<Array<Car>>() {}.type

                // Convert JSON data to Kotlin array, if the file exists
                val cars: Array<Car> = gson.fromJson(jsonFileString, arrayCarType)
                cars.forEachIndexed { idx, car -> Log.w("Data from JSON file", "> Item ${idx}:\n${car}\nBrand: ${car.brand}\nColor: ${car.color}\nModel: ${car.model}\nPrice: ${car.price}\nType: ${car.price}\nType: ${car.type}\nYear: ${car.year}\nPlate: ${car.plate}") }
                // Add previous items into JSON array
                for (i in cars.indices) {
                    Log.w("Array for loop, i =", i.toString())
                    // Get each value from created array
                    val brand = cars[i].brand
                    val model = cars[i].model
                    val year = cars[i].year
                    val color = cars[i].color
                    val type = cars[i].type
                    val price = cars[i].price
                    val plate = cars[i].plate
                    // Add it onto the MutableList
                    // Documentation for the below: https://kotlinlang.org/docs/reference/collection-write.html
                    carlist.add(Car(brand, model, year, color, type, price, plate))
                }
            }
        }

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val newCarInfo: String = gsonPretty.toJson(carlist)
        saveJSON(newCarInfo)

    }

    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val jsonString: String
        try {
            jsonString = File(storageDir, fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    private fun saveJSON(jsonString: String) {
        val output: Writer
        val file = createFile()
        output = BufferedWriter(FileWriter(file))
        output.write(jsonString)
        output.close()
    }

    private fun createFile(): File {
        // Save as carlist.json in /sdcard/Android/data/fail.enormous.carmate/files/Documents/
        val fileName = "carlist.json"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (storageDir != null) {
            if (!storageDir.exists()){
                // Make folder if nonexistent
                storageDir.mkdir()
            }
        }

        return File(
                storageDir,
                fileName
        )
    }

    private fun goToMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivity(i, options.toBundle())
    }
}
