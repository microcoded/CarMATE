package fail.enormous.carmate

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        setContentView(R.layout.activity_search)
    }

    fun LinearSearch(view: View) {
        startActivity(Intent(this, LinearSearchActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun BinarySearch(view: View) {
        startActivity(Intent(this, BinarySearchActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun setAnimation() {
        val slide = Slide()
        slide.slideEdge = Gravity.RIGHT
        slide.duration = 200
        slide.interpolator = DecelerateInterpolator()
        window.exitTransition = slide
        window.enterTransition = slide
    }
}