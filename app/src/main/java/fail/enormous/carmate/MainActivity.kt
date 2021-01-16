package fail.enormous.carmate

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
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


class MainActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private val viewItems: MutableList<Any> = ArrayList()
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.mRecyclerView = findViewById<View>(R.id.mainRecycler) as RecyclerView

        // TODO: Check if this setting improves performance and doesn't cause bugs
        // mRecyclerView!!.setHasFixedSize(true)

        // Using a linear layout manager
        layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = layoutManager

        // Specifying adapter
        mAdapter = RecyclerAdapter(this, viewItems)
        mRecyclerView!!.adapter = mAdapter
        addItemsFromJSON()

        // TODO: on clicking an item on recyclerview
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
                val carlist = Car(brand, model, year, color, type, price)
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

    @Throws(IOException::class)
    private fun readJSONDataFromFile(): String {
        var inputStream: InputStream? = null
        val builder = StringBuilder()
        try {
            val fileName = "carlist.json"
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
        private const val TAG = "MainActivity"
    }

    // When the SOLD button is pressed
    fun soldButtonPress(view: View) {
        startActivity()
    }

    // When the SORT button is pressed
    fun sortButtonPress(view: View) {
        var chosen = 0
        // Array of values to display in the list
        val listItems = arrayOf("Bubble", "Selection", "Insertion")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // The title of the dialogue box
        builder.setTitle(R.string.select_sort)
        // Set the selected item to the first in the list, as a default
        val checkedItem = 0

        // Do something when an item is pressed
        builder.setSingleChoiceItems(listItems, checkedItem, DialogInterface.OnClickListener { dialog, which ->
             chosen = which
        })

        // Do something when dialogue is confirmed
        builder.setPositiveButton(R.string.select, DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            // val selectedSort = listItems[which]  --> this grabs the select button as -1, so no :(
            val selectedSort = listItems[chosen]
            sortItems(selectedSort)
        })
        // Display the dialogue
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun sortItems(sort: String) {
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
                cars.forEachIndexed { idx, car -> Log.w("Data from JSON file", "> Item ${idx}:\n${car}\nBrand: ${car.brand}\nColor: ${car.color}\nModel: ${car.model}\nPrice: ${car.price}\nType: ${car.price}\nType: ${car.type}\nYear: ${car.year}") }
                /*
                for (i in cars.indices) {
                    Log.w("Array for loop, i =", i.toString())
                    // Get each value from created array
                    val brand = cars[i].brand
                    val model = cars[i].model
                    val year = cars[i].year
                    val color = cars[i].color
                    val type = cars[i].type
                    val price = cars[i].price
                }
                 */

                if (sort == "Bubble") {
                   /* var sorted = false
                    while (!sorted) {
                        sorted = true
                        for (j in 0 until cars.size - 1) {
                            if (cars[j + 1].price.toDouble() < cars[j].price.toDouble()) {
                                val temp = cars[j + 1]
                                cars[j + 1] = cars[j]
                                cars[j] = temp
                            }
                        }
                    } */

                    var swapped = true
                    var pass = 0
                    while (swapped) {
                        swapped = false
                        var comparison = 0
                        while (comparison < cars.size - 1 - pass) {
                            if (cars[comparison].price > cars[comparison + 1].price) {
                                val temp = cars[comparison + 1]
                                cars[comparison + 1] = cars[comparison]
                                cars[comparison] = temp
                                swapped = true
                            }
                            comparison += 1
                        }
                        pass += 1
                    }
                }

                if (sort == "Selection") {
                    // Insert selection sort code
                }
                if (sort == "Insertion") {
                    // Insert insertion sort code
                }

                // Saving the sorted data
                for (i in cars.indices) {
                    Log.w("Array for loop, i =", i.toString())
                    // Get each value from sorted array
                    val brand = cars[i].brand
                    val model = cars[i].model
                    val year = cars[i].year
                    val color = cars[i].color
                    val type = cars[i].type
                    val price = cars[i].price
                    // Add it onto the MutableList
                    carlist.add(Car(brand, model, year, color, type, price))
                }

            }
        }

        // Save the new JSON
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val newCarInfo: String = gsonPretty.toJson(carlist)
        saveJSON(newCarInfo)

        // Reload the RecyclerView
        addItemsFromJSON()
        mAdapter!!.notifyDataSetChanged()
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

    private fun startActivity() {
        val i = Intent(this, SoldActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivity(i, options.toBundle())
        // setContentView(R.layout.activity_sold)
    }

    fun addButtonPress(view: View) {
        val i = Intent(this, AddActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivity(i, options.toBundle())
    }

    override fun onBackPressed() {
        val gohome = Intent(Intent.ACTION_MAIN)
        gohome.addCategory(Intent.CATEGORY_HOME)
        gohome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(gohome)
    }

    fun searchButtonPress(view: View) {
        /* TODO: Linear and binary searching
         */
    }

    fun randomButtonPress(view: View) {
        startActivity(Intent(this, RandomActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

}