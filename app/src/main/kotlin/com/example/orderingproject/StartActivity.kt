package com.example.orderingproject

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.orderingproject.databinding.ActivityStartBinding
import com.google.android.material.appbar.AppBarLayout
import java.lang.Math.abs

class StartActivity : AppCompatActivity() {

    val binding by lazy { ActivityStartBinding.inflate(layoutInflater) }

    private var isGatheringMotionAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        makeStatusBarTransParent()

        initAppBar()

        initInsetMargin()

        initScrollViewListeners()

        initMotionLayoutListeners()

    }

    private fun initScrollViewListeners(){
        binding.scrollView.smoothScrollTo(0,0)

        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrolledValue = binding.scrollView.scrollY

            if (scrolledValue > 150f.dpToPx(this@StartActivity).toInt()) {
                if (isGatheringMotionAnimating.not()) {
                    binding.gatheringThingsBackgroundMotionLayout.transitionToEnd()
                    binding.gatheringThingsLayout.transitionToEnd()
                    binding.buttonShownMotionLayout.transitionToEnd()
                }
            } else {
                if (isGatheringMotionAnimating.not()) {
                    binding.gatheringThingsBackgroundMotionLayout.transitionToStart()
                    binding.gatheringThingsLayout.transitionToStart()
                    binding.buttonShownMotionLayout.transitionToStart()
                }
            }

            if(scrolledValue > binding.curationAnimationMotionLayout.bottom) {
                if(isCurationMotionAnimating.not()){
                    binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start1, R.id.curation_animation_end1)
                    binding.curationAnimationMotionLayout.transitionToEnd()
                    isCurationMotionAnimating = true
                }
            }
        }
    }

    private fun initMotionLayoutListeners(){
        binding.gatheringThingsLayout.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int){
                isGatheringMotionAnimating = true
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) = Unit

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int){
                isGatheringMotionAnimating = false
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) = Unit
        })

        binding.curationAnimationMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) = Unit

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) = Unit

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int){
                when(currentId){
                    R.id.curation_animation_end1 ->{
                        binding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start2, R.id.curation_animation_end2)
                        binding.curationAnimationMotionLayout.transitionToEnd()
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) = Unit
        })
    }

    private fun initAppBar() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this)
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            val realAlphaVerticalOffset =
                if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if (abstractOffset < topPadding) {
                binding.toolbarBackgroundView.alpha = 0f
                return@OnOffsetChangedListener
            }
            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            binding.toolbarBackgroundView.alpha =
                1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }

    private fun initInsetMargin() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(coordinator) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            toolbarContainer.layoutParams =
                (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0, insets.systemWindowInsetTop, 0, 0)
                }
            collapsingToolbarContainer.layoutParams =
                (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0, 0, 0, 0)
                }

            insets.consumeSystemWindowInsets()
        }
    }

    private fun initActionBar() = with(binding) {
        toolbar.navigationIcon = null
        toolbar.setContentInsetsAbsolute(0, 0)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

}

fun Float.dpToPx(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)


// 상태바를 투명하게 만드는 작업
fun Activity.makeStatusBarTransParent() {
    window.apply {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = Color.TRANSPARENT
    }
}