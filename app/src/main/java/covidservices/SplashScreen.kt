package covidservices

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.gesture.*
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue


/** Initial splash screen for the app. Presents the user with a black screen that slowly fills with
 * virus molecules. There is a high chance a spawn will be COVID, and a small chance it will not.
 *
 * The viruses will auto-generate until 150 are on screen, and will bounce off the walls at a right
 * vector  they are completely offscreen.
 *
 * The subtext under the app name starts off black and slowly turns to red as the number of virus
 * molecules increases.
 *
 * Also, by touch+hold+drag, the user can generate multitudes of additional virus molecules
 * (until they use up all available memory and the app crashes)
 *
 * Touching the screen or touch+hold for less than 500ms will open the app's main listview once
 * there are at least 64 viruses on screen.
 *
 * As this splash screen is solely aesthetic, once the user leaves this screen it will not
 * reappear on subsequent resumes, or back button presses. The splash screen will only appear
 * on initial startup (noHistory = true).
 *
 * This screen demonstrates our mastery of android graphical elements
 * (given that the rest of our app has no need for graphics).
 *
 * Author: Chuck Daniels
 */

class SplashScreen : Activity(){

    // The Main view
    private var mFrame: FrameLayout? = null

    private var mBitmap: Bitmap? = null
    private var mBitmap2: Bitmap? = null
    private var mBitmap3: Bitmap? = null
    private var mBitmap4: Bitmap? = null
    private var mBitmap5: Bitmap? = null
    private var mBitmap6: Bitmap? = null
    private var mSplashTextView: TextView? = null
    private var mSplashSubTextView: TextView? = null
    private var mSplashSubTextView2: TextView? = null

    private var r2 = 0
    private var active: Boolean = true;

    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0

    var timeDown: Long = 0L
    var timeUp: Long = 0L

    private lateinit var bList: ArrayList<VirusView>

    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        mDisplayWidth =  size.x
        mDisplayHeight = size.y

        mFrame = findViewById<View>(R.id.frame) as FrameLayout
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.b64)
        mBitmap2 =  BitmapFactory.decodeResource(resources, R.drawable.e64)
        mBitmap3 =  BitmapFactory.decodeResource(resources, R.drawable.f64)
        mBitmap4 =  BitmapFactory.decodeResource(resources, R.drawable.z64)
        mBitmap5 =  BitmapFactory.decodeResource(resources, R.drawable.g64)
        mBitmap6 =  BitmapFactory.decodeResource(resources, R.drawable.d64)
        mSplashTextView = findViewById<TextView>(R.id.covidServices)
        mSplashSubTextView = findViewById<TextView>(R.id.subText)
        mSplashSubTextView2 = findViewById<TextView>(R.id.subText2)

        bList = ArrayList<VirusView>()

        val intentMainListScreen = Intent(this, MainListScreen::class.java)

        mFrame!!.setOnTouchListener { _, event ->

            createVirus(event.x, event.y)

            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {
                    timeDown = System.currentTimeMillis()

                }
                MotionEvent.ACTION_UP -> {

                    timeUp = System.currentTimeMillis()

                    if (bList.size > 64 && (timeUp - timeDown).absoluteValue < 500 ) {
                    //if (/*bList.size > 64 && */(timeUp - timeDown).absoluteValue < 500 ) {

                        bList.forEach { b -> b.kill()}
                        active = false

                        startActivity(intentMainListScreen)
                    }
                }
            }
            true
        }

    }

    private fun createVirus(x: Float, y: Float){
        bList.add(VirusView(applicationContext, x, y, bList.size, System.currentTimeMillis()))
        bList[bList.size - 1].start()
        mFrame!!.addView(bList[bList.size - 1])


        /** Changes text color based on the number of virusViews */
        if (bList.size < 64) {
            val colorHexStr = "#" + "%02x".format(bList.size * 4) + "0000"
            mSplashSubTextView?.setTextColor(Color.parseColor(colorHexStr))
        }
        if (bList.size in 32..95) {
            val colorHexStr = "#" + "%02x".format((bList.size * 4) - (32 *4)) + "0000"
            //Log.i("color", "bListSize = " + bList.size +", color = $colorHexStr")
            mSplashSubTextView2?.setTextColor(Color.parseColor(colorHexStr))

        }
    }
    override fun onResume() {
        super.onResume()

        val handler = Handler(mainLooper)
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (bList.size < 128) {
                    /** Randomizes the horizontal or vertical spawn position*/
                    val xpos = (0..mDisplayWidth).random().toFloat()
                    val ypos = (0..mDisplayHeight).random().toFloat()
                    /** Randomizes which screen border the viruses spawn from*/
                    when((1..4).random()){
                        1 -> createVirus(xpos, -100F)
                        2 -> createVirus(mDisplayWidth.toFloat()+100F, ypos)
                        3 -> createVirus(xpos, mDisplayHeight.toFloat()+100F)
                        4 -> createVirus(-100F, ypos)
                    }
                    if (active) handler.postDelayed(this, 1000)
                }
            }
        }
        if (active) handler.postDelayed(runnable, 1000)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            mDisplayWidth = mFrame!!.width
            mDisplayHeight = mFrame!!.height
        }
    }


    override fun onPause() {
        super.onPause()
    }


    // VirusView is a View that displays a virus.
    // A new VirusView is created for each virus on the display

    inner class VirusView internal constructor(context: Context, x: Float, y: Float, var p: Int, var t: Long) : View(context) {
        private val mPainter = Paint()
        private var mMoverFuture: ScheduledFuture<*>? = null
        private var mScaledBitmapWidth: Int = 0
        private var mScaledBitmap: Bitmap? = null
        private val BITMAP_SIZE = 64
        private val REFRESH_RATE = 40

        // location, speed and direction of the virus
        private var mXPos: Float = 0.toFloat()
        private var mYPos: Float = 0.toFloat()
        private var mDx: Float = 0.toFloat()
        private var mDy: Float = 0.toFloat()
        private val mRadius: Float
        private val mRadiusSquared: Float
        private var mRotate: Long = 0
        private var mDRotate: Long = 0

        private fun isOutOfView(): Boolean {
           return  (mXPos < -50 - mScaledBitmapWidth || mXPos > mDisplayWidth + 50 || mYPos < -50 - mScaledBitmapWidth || mYPos > mDisplayHeight + 50)
        }

        init {
            Log.i(TAG, "Creating Virus at: x:$x y:$y")

            val r = Random()

            createScaledBitmap(r)

            mRadius = (mScaledBitmapWidth / 2).toFloat()
            mRadiusSquared = mRadius * mRadius

            mXPos = x - mRadius
            mYPos = y - mRadius

            setSpeedAndDirection(r)
            setRotation(r)

            mPainter.isAntiAlias = true

        }

        private fun setRotation(r: Random) {

            mDRotate = ((r.nextInt(3 * BITMAP_SIZE) + 1) / mScaledBitmapWidth).toLong()

        }


        private fun setSpeedAndDirection(r: Random) {

            mDx = (r.nextInt(mScaledBitmapWidth * 3) + 1) / mScaledBitmapWidth.toFloat()
            mDx *= (if (r.nextInt() % 2 == 0) 1 else -1).toFloat()

            mDy = (r.nextInt(mScaledBitmapWidth * 3) + 1) / mScaledBitmapWidth.toFloat()
            mDy *= (if (r.nextInt() % 2 == 0) 1 else -1).toFloat()


        }

        private fun createScaledBitmap(r: Random) {



            mScaledBitmapWidth = r.nextInt(2 * BITMAP_SIZE) + BITMAP_SIZE

            r2 = (1..40).random()
            Log.i(TAG, "r2 = $r2")
            mScaledBitmap = when (r2) {
                1 -> Bitmap.createScaledBitmap(mBitmap2!!, mScaledBitmapWidth, mScaledBitmapWidth, false)
                2 -> Bitmap.createScaledBitmap(mBitmap2!!, mScaledBitmapWidth, mScaledBitmapWidth, false)
               // 3 -> Bitmap.createScaledBitmap(mBitmap4!!, mScaledBitmapWidth, mScaledBitmapWidth, false)
               // 4 -> Bitmap.createScaledBitmap(mBitmap5!!, mScaledBitmapWidth, mScaledBitmapWidth, false)
                5 -> Bitmap.createScaledBitmap(mBitmap6!!, mScaledBitmapWidth, mScaledBitmapWidth, false)
                else -> Bitmap.createScaledBitmap(mBitmap!!, mScaledBitmapWidth, mScaledBitmapWidth, false) //chance to spawn covid
            }

        }

        fun start() {


            val executor = Executors.newScheduledThreadPool(1)

            mMoverFuture = executor.scheduleWithFixedDelay({
                if (moveWhileOnScreen()) {
                    postInvalidate()
                } else
                    revector()
                    //stop(false)
            }, 0, REFRESH_RATE.toLong(), TimeUnit.MILLISECONDS)
        }

        @Synchronized
        fun intersects(x: Float, y: Float): Boolean {

            val xDist = x - (mXPos + mRadius)
            val yDist = y - (mYPos + mRadius)

            return xDist * xDist + yDist * yDist <= mRadiusSquared

        }


        fun kill() {

            if (null != mMoverFuture) {

                if (!mMoverFuture!!.isDone) {
                    mMoverFuture!!.cancel(true)
                }

                // This work will be performed on the UI Thread
                mFrame!!.post {
                    mFrame!!.removeView(this@VirusView)
                }
            }



        }

        // Change the Virus's speed and direction
        @Synchronized
        fun deflect(velocityX: Float, velocityY: Float) {
            mDx = velocityX / REFRESH_RATE
            mDy = velocityY / REFRESH_RATE
        }

        @Synchronized
        fun revector() {
            Log.i("vector", "Changing vector mDx = $mDx, mDy = $mDy")


            if (mXPos > mDisplayWidth - 100 || mXPos < 100) {
                mDx = -mDx
                return
            }
            if (mYPos > mDisplayHeight - 100 || mYPos < 100) {
                mDy = -mDy
                return
            }
            mDy = -mDy
            mDx = -mDx


        }



        // Draw the Virus at its current location
        @Synchronized
        override fun onDraw(canvas: Canvas) {

            // save the canvas
            canvas.save()

            // Increase the rotation of the original image by mDRotate
            mRotate += mDRotate

            // Rotate the canvas by current rotation
            // Hint - Rotate around the virus's center, not its position

            canvas.rotate(
                    mRotate.toFloat(),
                    mXPos + mScaledBitmapWidth / 2,
                    mYPos + mScaledBitmapWidth / 2
            )

            // Draw the bitmap at it's new location
            canvas.drawBitmap(mScaledBitmap!!, mXPos, mYPos, mPainter)

            // Restore the canvas
            canvas.restore()

        }

        // Returns true if the VirusView is still on the screen after the move
        // operation
        @Synchronized
        private fun moveWhileOnScreen(): Boolean {

            mXPos += mDx
            mYPos += mDy

            return !isOutOfView()

        }
    }

    //override fun onBackPressed() {}


    companion object {

        private val TAG = "SplashScreen"
    }
}