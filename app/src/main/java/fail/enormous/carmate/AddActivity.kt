package fail.enormous.carmate

import android.annotation.SuppressLint
import android.app.ActivityOptions
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
import com.google.gson.GsonBuilder
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer



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
        slide.setSlideEdge(Gravity.RIGHT)
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

            // Grabbing data from input
            val brandContent = brandInput.text.toString().toLowerCase().capitalize()
            val modelContent = modelInput.text.toString()
            val yearContent = yearInput.text.toString().toInt()
            val colorContent = colorInput.text.toString().toLowerCase()
            val typeContent = typeInput.selectedItem.toString()
            val priceContent = priceInput.text.toString().toDouble()

            Log.w("Content", "$brandContent, $modelContent, $yearContent, $colorContent, $typeContent, $priceContent")

            addCarToJSON(brandContent, modelContent, yearContent, colorContent, typeContent, priceContent)
            goToMainActivity()

            return true
        }
        catch (t: Throwable) {
            Log.w("Error:", t.toString())
            return false
        }
    }

    fun addCarToJSON(brand: String, model: String, year: Int, color: String, type: String, price: Double) {
        // TODO: finish function to append data to JSON file

        // val carlist: List<String> = listOf(brand, model, year.toString(), color, type, price.toString())
       // var carlist: CarList = CarList(brand, model, year, color, type, price)


        // Put data into JSON array, with love from https://stackoverflow.com/questions/65591615/how-do-i-output-data-as-a-json-array-in-kotlin-on-android

        var carlist = listOf(CarList(brand, model, year, color, type, price))
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val newCarInfo: String = gsonPretty.toJson(carlist)
        saveJSON(newCarInfo)
    }

    fun saveJSON(jsonString: String) {
        val output: Writer
        val file = createFile()
        output = BufferedWriter(FileWriter(file))
        output.write(jsonString)
        output.close()
    }

    private fun createFile(): File {
        val fileName = "carlist.json"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (storageDir != null) {
            if (!storageDir.exists()){
                storageDir.mkdir()
            }
        }

        return File(
                storageDir,
                fileName
        )
    }

    fun goToMainActivity() {
        val i = Intent(this, MainActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivity(i, options.toBundle())
    }
}
