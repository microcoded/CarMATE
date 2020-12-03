package fail.enormous.carmate

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.animation.DecelerateInterpolator

class SoldActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAnimation()
        setContentView(R.layout.activity_sold)
    }

    fun setAnimation() {
        val slide = Slide()
        slide.setSlideEdge(Gravity.LEFT)
        slide.setDuration(200)
        slide.setInterpolator(DecelerateInterpolator())
        getWindow().setExitTransition(slide)
        getWindow().setEnterTransition(slide)
    }
}