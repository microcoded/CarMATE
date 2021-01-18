package fail.enormous.carmate

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import java.util.*

class LinearSearchActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var viewItems: MutableList<Any> = ArrayList()
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linear_search)
        setAnimation()

        this.mRecyclerView = findViewById<View>(R.id.linearSearchRecycler) as RecyclerView

        // Using a linear layout manager
        layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = layoutManager

        // Specifying adapter
        mAdapter = RecyclerAdapter(this, viewItems)
        mRecyclerView!!.adapter = mAdapter

        // Invoke onDone function when the search button on the keyboard is pressed.
        val search_input = findViewById<EditText>(R.id.search_input)
        search_input.onDone { searchButtonPress(search_input) }
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.TOP
        slide.duration = 200
        slide.interpolator = DecelerateInterpolator()
        window.exitTransition = slide
        window.enterTransition = slide
    }

    fun searchButtonPress(view: View) {
        val search_input = findViewById<EditText>(R.id.search_input)
        searchButtonPress(search_input)
    }

    private fun searchButtonPress(search_input: EditText) {
        hideKeyboard()
        val search_text = search_input.text.toString().toLowerCase()
        linearSearch(search_text)
    }

    private fun linearSearch(query: String) {
        // Mutuable list for placing search results into a JSON file
        val carlist = mutableListOf<Car>()

        // Get information from JSON file if it exits
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
                cars.forEachIndexed { idx, car ->
                    Log.w(
                        "Data from JSON file",
                        "> Item ${idx}:\n${car}\nBrand: ${car.brand}\nColor: ${car.color}\nModel: ${car.model}\nPrice: ${car.price}\nType: ${car.type}\nYear: ${car.year}\nPlate: ${car.plate}"
                    )
                }

                // Linear search algorithm - not case sensitive!
                var somethingFound = false // Checking if something at all is found
                for (i in cars.indices) {
                    if (cars[i].color.toLowerCase() == query || cars[i].brand.toLowerCase() == query || cars[i].model.toLowerCase() == query || cars[i].plate.toLowerCase() == query || cars[i].year.toString() == query || cars[i].price.toString() == query || cars[i].type.toLowerCase() == query) {
                        carlist.add(Car(cars[i].brand, cars[i].model, cars[i].year, cars[i].color, cars[i].type, cars[i].price, cars[i].plate))
                        somethingFound = true // Something was found!
                    }
                }

                // First, clear RecyclerView
                viewItems.clear()
                mAdapter!!.notifyDataSetChanged()
                mAdapter = RecyclerAdapter(this, viewItems)
                mRecyclerView!!.adapter = mAdapter

                if (somethingFound) {
                    // Save search results in a JSON file
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    val searchResults: String = gsonPretty.toJson(carlist)
                    saveJSON(searchResults)

                    // Display search results in RecylerView
                    displaySearchResults()
                }
                else {
                    // Display a "No results" toast
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.no_results),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun displaySearchResults() {
        addItemsFromJSON()
    }

    private fun saveJSON(searchResults: String) {
        val output: Writer
        val file = createFile()
        output = BufferedWriter(FileWriter(file))
        output.write(searchResults)
        output.close()
    }

    private fun createFile(): File {
        // Save in /sdcard/Android/data/fail.enormous.carmate/files/Documents/
        val fileName = "searchResults.json"
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

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                callback.invoke()
                true
            }
            false
        }
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

    private fun addItemsFromJSON() {
        try {
            val jsonDataString = readJSONDataFromFile()
            val jsonArray = JSONArray(jsonDataString)
            for (i in 0 until jsonArray.length()) {
                val itemObj = jsonArray.getJSONObject(i)
                val brand = itemObj.getString("brand")
                val model = itemObj.getString("model")
                val year = itemObj.getInt("year")
                val color = itemObj.getString("color")
                val type = itemObj.getString("type")
                val price = itemObj.getDouble("price").toBigDecimal()
                val plate = itemObj.getString("plate")
                val carlist = Car(brand, model, year, color, type, price, plate)
                viewItems.add(carlist)
            }
        }

        // Error handling and debugging
        catch (e: JSONException) {
            Log.d(TAG, "addItemsFromJSON: ", e)
        }
        catch (e: IOException) {
            Log.d(TAG, "addItemsFromJSON: ", e)
        }

    }

    private fun readJSONDataFromFile(): String {
        var inputStream: InputStream? = null
        val builder = StringBuilder()
        try {
            val fileName = "searchResults.json"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (storageDir != null) {
                // Make the directory if it doesn't exist
                if (!storageDir.exists()){
                    storageDir.mkdir()
                }
            }
            var jsonString: String? = null
            val bufferedReader = File(storageDir, fileName).bufferedReader()

            // Reading data per line, appending it to builder
            while (bufferedReader.readLine().also { jsonString = it } != null) {
                // Appending string information from the file to the builder
                builder.append(jsonString)
                Log.w("while loop", jsonString)
            }
        }
        finally {
            // Close file once finished
            inputStream?.close()
            Log.w("string builder", String(builder))
        }
        // Return the string
        Log.w("string builder", String(builder))
        return String(builder)
    }

    // Tags for error handling
    companion object {
        private const val TAG = "LinearSearchActivity"
    }


}
