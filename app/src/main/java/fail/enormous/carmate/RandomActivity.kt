package fail.enormous.carmate

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random


class RandomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        // Make status bar colour orange
        val window: Window = this@RandomActivity.window
        window.statusBarColor = ContextCompat.getColor(this@RandomActivity, R.color.random_orange)
        setContentView(R.layout.activity_random)
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.setSlideEdge(Gravity.RIGHT)
        slide.setDuration(200)
        slide.setInterpolator(DecelerateInterpolator())
        getWindow().setExitTransition(slide)
        getWindow().setEnterTransition(slide)
    }

    fun generateRandomNumbers(view: View) {
        // TODO: Grab min and max from user inputs, max - min cannot be < numbers
        val minEditText = this.findViewById<EditText>(R.id.minEditText)
        val maxEditText = this.findViewById<EditText>(R.id.maxEditText)
        val amountEditText = this.findViewById<EditText>(R.id.amountEditText)
        val randomTextView = findViewById<TextView>(R.id.randomTextView)

        // Make text scrollable
        randomTextView.movementMethod = ScrollingMovementMethod()

        if (minEditText.text.toString() != "" && maxEditText.text.toString() != "" && amountEditText.text.toString() != "") {

            val min = minEditText.text.toString().toInt()
            val max = maxEditText.text.toString().toInt()
            var numbers = amountEditText.text.toString().toInt()
            numbers += 1 // Increment numbers by 1, simplifying loops

            if (max == min || min > max) {
                // Warning if minimum is not greater than maximum
                // Could also be written as if(max <= min) really, oops
                Toast.makeText(
                    applicationContext,
                    getString(R.string.min_max_error),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else if (max - min >= numbers) {
                // Clear text view
                randomTextView.setText("")

                // Initialise array
                val taken = BooleanArray(max) { false }

                // Set first 200 values of array as false
                for (i in min until max) {
                    taken[i] = false
                }

                // Generate unique random numbers and append them to the TextView.
                for (i in min until numbers) {
                    var chosen = Random.nextInt(min, max)

                    while (taken[chosen]) {
                        chosen = Random.nextInt(min, max)
                    }
                    taken[chosen] = true
                    Log.w("array", chosen.toString())
                    randomTextView.append(chosen.toString() + "\n")
                }

            }
            else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.max_min_warning),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else {
            Toast.makeText(
                applicationContext,
                getString(R.string.random_blank),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}