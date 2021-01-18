package fail.enormous.carmate

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import java.math.BigDecimal
import java.util.*

class BinarySearchActivity : AppCompatActivity(), RecyclerAdapter.CellClickListener {
    private var mRecyclerView: RecyclerView? = null
    private var viewItems: MutableList<Any> = ArrayList()
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        setContentView(R.layout.activity_binary_search)

        this.mRecyclerView = findViewById<View>(R.id.binarySearchRecycler) as RecyclerView

        // Using a linear layout manager
        layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = layoutManager

        // Specifying adapter
        mAdapter = RecyclerAdapter(this, viewItems, this)
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

    private fun EditText.onDone(callback: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                callback.invoke()
                true
            }
            false
        }
    }

    fun searchButtonPress(view: View) {
        val search_input = findViewById<EditText>(R.id.search_input)
        searchButtonPress(search_input)
    }

    private fun searchButtonPress(search_input: EditText) {
        hideKeyboard()
        // Check if input is blank to avoid crashing
        if (search_input.text.toString() != "") { val search_text = search_input.text?.toString()?.toBigDecimal()
            binarySearchDialogue(search_text)
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
            )
    }

    private fun binarySearchDialogue(target: BigDecimal?) {
        // Display a dialogue, because we need to perform a sort before we perform a binary search.
        // This will search for year or price

        var chosen = 0
        // Array of values to display in the list
        val listItems = arrayOf("Year", "Price")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // The title of the dialogue box
        builder.setTitle(R.string.search_for)
        // Set the selected item to the first in the list, as a default
        val checkedItem = 0

        // Do something when an item is pressed
        builder.setSingleChoiceItems(
            listItems,
            checkedItem,
            DialogInterface.OnClickListener { dialog, which ->
                    chosen = which
                })

        // Do something when dialogue is confirmed
        builder.setPositiveButton(
            R.string.select,
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                Log.w("chosen", chosen.toString())
                sortItems(chosen, target)
            })
        // Display the dialogue
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun sortItems(chosen: Int, target: BigDecimal?) {
        // Definining a blank MutableList for holding data to be put into JSON
        val carlist = mutableListOf<Car>()
        // Reading the file
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
                        "> Item ${idx}:\n${car}\nBrand: ${car.brand}\nColor: ${car.color}\nModel: ${car.model}\nPrice: ${car.price}\nType: ${car.price}\nType: ${car.type}\nYear: ${car.year}\nPlate: ${car.plate}"
                    )
                }
                if (chosen == 0) {
                    // Sort by year

                    // Year (oldest first), selection sort
                    var pass = 0
                    while (pass < cars.size - 1) {
                        var count = pass + 1
                        var minimum = pass
                        while (count <= cars.size - 1) {
                            if (cars[count].year < cars[minimum].year) {
                                minimum = count
                            }
                            count += 1
                        }
                        val temp = cars[minimum]
                        cars[minimum] = cars[pass]
                        cars[pass] = temp
                        pass += 1
                    }
                }

                if (chosen == 1) {
                    // Sort by price

                    // Selection sort (Price, lowest first)
                    var pass = 0
                    while (pass < cars.size - 1) {
                        var count = pass + 1
                        var maximum = pass
                        while (count <= cars.size - 1) {
                            if (cars[count].price < cars[maximum].price) {
                                maximum = count
                            }
                            count += 1
                        }
                        val temp = cars[maximum]
                        cars[maximum] = cars[pass]
                        cars[pass] = temp
                        pass += 1
                    }
                }
                // Binary search algorithm
                var lower = 0
                var upper = cars.size - 1
                var found = false
                var success = false
                while (!found && upper >= lower) {
                    val mid = upper + lower / 2 .toInt()
                    if (chosen == 0) success = cars[mid].year.toBigDecimal() == target
                    else success = cars[mid].price == target
                    if (success) {
                        // Add the found car into the carlist
                        found = true
                        carlist.add(Car(cars[mid].brand, cars[mid].model, cars[mid].year, cars[mid].color, cars[mid].type, cars[mid].price, cars[mid].plate))
                    }
                    if (cars[mid].price < target) lower = mid + 1
                    if (cars[mid].price > target) upper = mid - 1
                }
                // First, clear RecyclerView
                viewItems.clear()
                mAdapter!!.notifyDataSetChanged()
                mAdapter = RecyclerAdapter(this, viewItems, this)
                mRecyclerView!!.adapter = mAdapter

                if (found) {
                    // Save search results in a JSON file
                    val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                    val searchResults: String = gsonPretty.toJson(carlist)
                    saveJSON(searchResults)

                    // Display search results in RecylerView
                    addItemsFromJSON()
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
            val fileName = "binarySearchResults.json"
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
        private const val TAG = "BinarySearchActivity"
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
        val fileName = "binarySearchResults.json"
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

    override fun onCellClickListener(pos: Int) {
        // Do nothing
    }

}