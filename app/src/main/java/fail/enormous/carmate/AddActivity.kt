package fail.enormous.carmate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast

class AddActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        setContentView(R.layout.activity_add)
        fillSpinner()
    }

    fun setAnimation() {
        val slide = Slide()
        slide.setSlideEdge(Gravity.RIGHT)
        slide.setDuration(200)
        slide.setInterpolator(DecelerateInterpolator())
        getWindow().setExitTransition(slide)
        getWindow().setEnterTransition(slide)
    }

    fun fillSpinner() {
        /* From Android Developers documentation
        val spinner: Spinner = findViewById(R.id.type_spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        } */

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

    override fun onItemSelected(parent: AdapterView<*>?,
                                view: View, position: Int,
                                id: Long) {

        // Do to item if position selected is not the default (blank value)
        if (position != 0) {
            // Toast of name selected
            Toast.makeText(
                applicationContext,
                resources.getStringArray(R.array.type_array)[position],
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }
}