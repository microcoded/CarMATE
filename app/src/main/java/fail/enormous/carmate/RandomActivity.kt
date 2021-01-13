package fail.enormous.carmate

import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class RandomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
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
        // Initialise array
        var taken = BooleanArray(200) { false }

        // Set first 200 values of array as false
        for (i in 0 until 200) {
            taken[i] = false
        }

        for (i in 0 until 10) {
            var chosen = Random.nextInt(0 , 20)

            while (taken[chosen]) {
                chosen = Random.nextInt(0 , 20)
            }
            taken[chosen] = true
            Log.w("array", chosen.toString())
        }
    }
}