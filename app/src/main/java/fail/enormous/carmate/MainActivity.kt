package fail.enormous.carmate

import fail.enormous.carmate.RecyclerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
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


       /* TODO: Check if this setting improves performance and doesn't cause bugs
        mRecyclerView!!.setHasFixedSize(true)
        */

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
                val year = itemObj.getString("year")
                val color = itemObj.getString("color")
                val type = itemObj.getString("type")
                val price = itemObj.getString("price")
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
            var jsonString: String? = null
            inputStream = resources.openRawResource(R.raw.sample_data)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            while (bufferedReader.readLine().also { jsonString = it } != null) {
                builder.append(jsonString)
            }
        } finally {
            inputStream?.close()
        }
        return String(builder)
    }

    // I don't remember what this does but I don't want to destroy CarMATE so this will stay!
    companion object {
        private const val TAG = "MainActivity"
    }
}