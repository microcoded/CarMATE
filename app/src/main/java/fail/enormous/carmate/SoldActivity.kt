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
import kotlin.random.Random

class SoldActivity : AppCompatActivity(), RecyclerAdapter.CellClickListener {
    private var mRecyclerView: RecyclerView? = null
    private val viewItems: MutableList<Any> = ArrayList()
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        setContentView(R.layout.activity_sold)

        this.mRecyclerView = findViewById<View>(R.id.soldRecycler) as RecyclerView


        // TODO: Check if this setting improves performance and doesn't cause bugs
        // mRecyclerView!!.setHasFixedSize(true)

        // Using a linear layout manager
        layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = layoutManager

        // Specifying adapter
        mAdapter = RecyclerAdapter(this, viewItems, this)
        mRecyclerView!!.adapter = mAdapter
        addItemsFromJSON()
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
            Log.d(SoldActivity.TAG, "addItemsFromJSON: ", e)
        }
        catch (e: IOException) {
            Log.d(SoldActivity.TAG, "addItemsFromJSON: ", e)
        }

    }

    companion object {
        private const val TAG = "SoldActivity"
    }

    @Throws(IOException::class)
    private fun readJSONDataFromFile(): String {
        var inputStream: InputStream? = null
        val builder = StringBuilder()
        try {
            val fileName = "soldlist.json"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            /*if (storageDir != null) {
                if (!storageDir.exists()){
            Don't need anything here because the directory would have been created in MainActivity, keeping in case of future
                }
            }*/
            var jsonString: String? = null
            //inputStream =
            val bufferedReader = File(storageDir, fileName).bufferedReader()
            // Open the JSON file
            //inputStream = resources.openRawResource(R.raw.sample_data)
            // resources.openRawResource(R.raw.sample_data).also { inputStream = it }
            // Reading the file
            //val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
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

    fun setAnimation() {
        val slide = Slide()
        slide.setSlideEdge(Gravity.LEFT)
        slide.setDuration(200)
        slide.setInterpolator(DecelerateInterpolator())
        getWindow().setExitTransition(slide)
        getWindow().setEnterTransition(slide)
    }

    override fun onCellClickListener(pos: Int) {
        Log.w("RecyclerPos", pos.toString())

        // Read JSON file
        val filename = "soldlist.json"
        val gson = Gson()
        val arrayCarType = object : TypeToken<Array<Car>>() {}.type
        val jsonFileString = getJsonDataFromAsset(applicationContext, filename)

        // Convert JSON data to Kotlin array, if the file exists
        val cars: Array<Car> = gson.fromJson(jsonFileString, arrayCarType)
        Log.w("RecyclerData", "> Item ${pos}:\n${cars}\nBrand: ${cars[pos].brand}\nColor: ${cars[pos].color}\nModel: ${cars[pos].model}\nPrice: ${cars[pos].price}\nType: ${cars[pos].type}\nYear: ${cars[pos].year}\nPlate: ${cars[pos].plate}")

        // Present dialogue
        deleteDialogue(cars, pos)
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

    private fun deleteDialogue(cars: Array<Car>, pos: Int) {
        var chosen = 0
        // Array of values to display in the list
        val listItems = arrayOf(getString(R.string.delete))
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // The title of the dialogue box
        builder.setTitle("${cars[pos].brand} ${cars[pos].model}")
        // Set the selected item to the first in the list, as a default
        val checkedItem = -1

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

                    // First, clear RecyclerView
                    viewItems.clear()
                    mAdapter!!.notifyDataSetChanged()
                    mAdapter = RecyclerAdapter(this, viewItems, this)
                    mRecyclerView!!.adapter = mAdapter

                    // Delete and move to sold functions
                    if (chosen == 0) deleteCar(cars, pos)

                })
        // Display the dialogue
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteCar(cars: Array<Car>, pos: Int) {
        // Definining a blank MutableList for holding data to be put into JSON
        // Reading the file
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val filename = "soldlist.json"
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
                var cars: Array<Car> = gson.fromJson(jsonFileString, arrayCarType)
                cars.forEachIndexed { idx, car ->
                    Log.w(
                            "Data from JSON file",
                            "> Item ${idx}:\n${car}\nBrand: ${car.brand}\nColor: ${car.color}\nModel: ${car.model}\nPrice: ${car.price}\nType: ${car.price}\nType: ${car.type}\nYear: ${car.year}\nPlate: ${car.plate}"
                    )
                }

                // Delete the car in pos and remove all spaces
                val carlist = remove(cars, pos)

                // Save new data
                val gsonPretty = GsonBuilder().setPrettyPrinting().create()
                val searchResults: String = gsonPretty.toJson(carlist)
                saveJSON(searchResults)

                // Display new items
                addItemsFromJSON()
            }
        }
    }

    private fun remove(arr: Array<Car>, index: Int): MutableList<Car> {
        // If the item we are trying to remove doesn't exist, return the array back as a MutableList
        if (index < 0 || index >= arr.size) {
            return arr.toMutableList()
        }
        // Convert the array to a MutableList with the item removed
        val result = arr.toMutableList()
        result.removeAt(index)
        return result
    }

    private fun saveJSON(jsonString: String) {
        val output: Writer
        val file = createFile()
        output = BufferedWriter(FileWriter(file))
        output.write(jsonString)
        output.close()
    }

    private fun createFile(): File {
        // Save as soldlist.json in /sdcard/Android/data/fail.enormous.carmate/files/Documents/
        val fileName = "soldlist.json"
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

    fun sortButtonPress(view: View) {
        var chosen = 0
        // Array of values to display in the list
        val listItems = arrayOf("Price (lowest first) [Bubble]", "Price (highest first) [Selection]", "Year (newest first) [Insertion]", "Year (oldest first) [Selection]", "Random")
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
            //val selectedSort = listItems[chosen]
            // sortItems(selectedSort)
            Log.w("chosen", chosen.toString())
            sortItems(chosen)
        })
        // Display the dialogue
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun sortItems(sort: Int) {
        // Definining a blank MutableList for holding data to be put into JSON
        val carlist = mutableListOf<Car>()
        // Reading the file
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val filename = "soldlist.json"
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
                cars.forEachIndexed { idx, car -> Log.w("Data from JSON file", "> Item ${idx}:\n${car}\nBrand: ${car.brand}\nColor: ${car.color}\nModel: ${car.model}\nPrice: ${car.price}\nType: ${car.type}\nYear: ${car.year}\nPlate: ${car.plate}") }

                if (sort == 0) {
                    // Bubble sort (Price, lowest first)
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

                if (sort == 1) {
                    // Selection sort (Price, highest first)
                    var pass = 0
                    while (pass < cars.size - 1) {
                        var count = pass + 1
                        var maximum = pass
                        while (count <= cars.size - 1) {
                            if (cars[count].price > cars[maximum].price) {
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

                if (sort == 2) {
                    // Year (newest first), insertion sort
                    var currentItem = 0
                    while (currentItem <= cars.size - 1) {
                        val currentDataItem = cars[currentItem]
                        var comparison = 0
                        var finish = false
                        while (comparison < currentItem && !finish) {
                            if (currentDataItem.year > cars[comparison].year) {
                                var shuffleItem = currentItem
                                while (shuffleItem > comparison) {
                                    cars[shuffleItem] = cars[shuffleItem - 1]
                                    shuffleItem -= 1
                                }
                                cars[comparison] = currentDataItem
                                finish = true
                            }
                            comparison += 1
                        }
                        currentItem += 1
                    }
                }

                if (sort == 3) {
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

                if (sort == 4) {
                    // Random sort with unique random values
                    // Initialise arrays
                    val taken = BooleanArray(cars.size + 1) { false }
                    val cars2: Array<Car> = gson.fromJson(jsonFileString, arrayCarType) // This array is the same as cars

                    // Set all used values of array as false
                    for (i in 0 until cars.size + 1) {
                        taken[i] = false
                    }

                    // Generate unique random numbers and sort cars2 by them, according to cars
                    for (i in cars.indices) {
                        var chosen = Random.nextInt(0, cars.size)

                        while (taken[chosen]) {
                            chosen = Random.nextInt(0, cars.size)
                        }

                        taken[chosen] = true

                        // Set each value of cars2 (starting at 0) as a unique random index of cars
                        Log.w("for i in cars.indices", "i=$i, chosen=$chosen")
                        cars2[i] = cars[chosen]
                    }

                    // Make cars2 identical to cars
                    for (i in cars.indices) {
                        cars[i] = cars2[i]
                    }
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
                    val plate = cars[i].plate
                    // Add it onto the MutableList
                    carlist.add(Car(brand, model, year, color, type, price, plate))
                }

            }
        }

        // Save the new JSON
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val newCarInfo: String = gsonPretty.toJson(carlist)
        saveJSON(newCarInfo)

        // Refresh the RecyclerView
        //reloadActivity()
        viewItems.clear()
        mAdapter!!.notifyDataSetChanged()
        mAdapter = RecyclerAdapter(this, viewItems, this)
        mRecyclerView!!.adapter = mAdapter
        addItemsFromJSON()
    }
}