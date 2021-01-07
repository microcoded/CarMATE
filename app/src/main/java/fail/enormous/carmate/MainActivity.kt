package fail.enormous.carmate

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.io.InputStream
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
                val price = itemObj.getDouble("price")
                val carlist = CarList(brand, model, year, color, type, price)
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
                if (!storageDir.exists()){

                }
            }
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

    // I don't remember what this does but I don't want to destroy CarMATE so this will stay!
    companion object {
        private const val TAG = "MainActivity"
    }

    // When the SOLD button is pressed
    fun soldButtonPress(view: View) {
        // startActivity(Intent(this, SoldActivity::class.java))
        startActivity()
    }

    // When the SORT button is pressed
    fun sortButtonPress(view: View) {
        // Array of values to display in the list
        val listItems = arrayOf("Bubble", "Selection", "Insertion")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        // The title of the dialogue box
        builder.setTitle(R.string.select_sort)
        // Set the selected item to the first in the list, as a default
        var checkedItem = 0

        // Do something when an item is pressed
        builder.setSingleChoiceItems(listItems, checkedItem, DialogInterface.OnClickListener { dialog, which ->
            // Toast.makeText(this, "Position: " + which + " Value: " + listItems[which], Toast.LENGTH_LONG).show()

        }  )

        // Do something when dialogue is confirmed
        builder.setPositiveButton(R.string.select, DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        }   )
        // Display the dialogue
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun startActivity() {
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

}