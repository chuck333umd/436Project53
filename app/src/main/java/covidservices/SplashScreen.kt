package covidservices

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.gesture.*
import android.graphics.*
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock.sleep
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import java.sql.Time
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis


/** Initial splash screen for the app. Presents the user with a black screen that slowly fills with virus molecules.
 * There is a 75% chance a spawning molecule will be COVID, and a 25% chance it will be something else.
 * Clicking/touching the screen will open the app's main listview. As this splash screen is solely aesthetic, once
 * the user leaves this screen it will not reappear on subsequent resumes, or back button presses. The splash screen
 * will only appear on initial startup.
 *
 * Also, by press+hold+dragging, the user can generate multitudes of additional virus molecules
 * (until they use up all available memory and the app crashes)
 *
 * This screen demonstrates our mastery of android graphical elements (given that the rest of our app has no need for graphics).
 *
 * At present, I have not figured out how to add the viruses on a delay. Once that is working, this activity will be complete.
 */

class SplashScreen : Activity(){

    // The Main view
    private var mFrame: FrameLayout? = null

    private var mBitmap: Bitmap? = null
    private var mBitmap2: Bitmap? = null

    private var r2 = 0

    // Display dimensions


    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0

    private var touched = false

    // SoundPool
    private var mSoundPool: SoundPool? = null

    // ID for the virus popping sound
    private var mSoundID: Int = 0

    // Audio volume
    private var mStreamVolume: Float = 0.toFloat()
    var timeDown: Long = 0L
    var timeUp: Long = 0L
    var elapsed: Long = 0L


    // Gesture Library
    private lateinit var mLibrary: GestureLibrary

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

        bList = ArrayList<VirusView>()

        val intent = Intent(this, MainListScreen::class.java)


        mFrame!!.setOnTouchListener { _, event ->

            touched = true
            createVirus(event.x, event.y)



            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {
                   timeDown = System.currentTimeMillis()

                }
                MotionEvent.ACTION_UP -> {

                    timeUp = System.currentTimeMillis()

                    if ((timeUp - timeDown).absoluteValue < 500) {

                        startActivity(intent)
                    }
                }
            }
            true
        }

    }

    private fun createVirus(x: Float, y: Float){
        bList.add(VirusView(applicationContext, x, y, bList.size, System.currentTimeMillis()))
        bList[bList.size - 1].start()
        //bList[bList.size - 1].invalidate()
        mFrame!!.addView(bList[bList.size - 1])
        Log.i(TAG, "bList size = " + bList.size)
    }
    override fun onResume() {
        super.onResume()


        for (i in 0..10) {
           val xpos = (100..mDisplayWidth-100).random()
            val ypos = (100..mDisplayHeight-100).random()
            createVirus(xpos.toFloat(), ypos.toFloat())

        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {

            // Get the size of the display so this View knows where borders are
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

        // Return true if the VirusView is not on the screen after the move
        // operation
        private fun isOutOfView(): Boolean {
           // Log.i(TAG, "mXPos = $mXPos, mYPos = $mYPos")
           // Log.i(TAG, "mDisplayWidth = $mDisplayWidth, mDisplayHeight = $mDisplayHeight")
           // Log.i(TAG, "0 - mScaledBitmapWidth = " + (0 - mScaledBitmapWidth))
           // Log.i(TAG, "isOutOfView = " + (mXPos < 0 - mScaledBitmapWidth || mXPos > mDisplayWidth || mYPos < 0 - mScaledBitmapWidth || mYPos > mDisplayHeight))
           return  (mXPos < 0 - mScaledBitmapWidth || mXPos > mDisplayWidth || mYPos < 0 - mScaledBitmapWidth || mYPos > mDisplayHeight)
        }





        init {
            Log.i(TAG, "Creating Virus at: x:$x y:$y")

            // Create a new random number generator to
            // randomize size, rotation, speed and direction
            val r = Random()

            // Creates the virus bitmap for this VirusView
            createScaledBitmap(r)

            // Radius of the Bitmap
            mRadius = (mScaledBitmapWidth / 2).toFloat()
            mRadiusSquared = mRadius * mRadius

            // Adjust position to center the virus under user's finger
            mXPos = x - mRadius
            mYPos = y - mRadius

            // Set the VirusView's speed and direction
            setSpeedAndDirection(r)

            // Set the VirusView's rotation
            setRotation(r)

            mPainter.isAntiAlias = true

        }

        private fun setRotation(r: Random) {

            mDRotate = ((r.nextInt(3 * BITMAP_SIZE) + 1) / mScaledBitmapWidth).toLong()

        }


        fun changeVector(dX: Float, dY: Float) {
            mDx = dX
            mDy = dY

        }
        private fun setSpeedAndDirection(r: Random) {

            //  mDx = 20f
            //   mDy = 20f

            mDx = (r.nextInt(mScaledBitmapWidth * 3) + 1) / mScaledBitmapWidth.toFloat()
            mDx *= (if (r.nextInt() % 2 == 0) 1 else -1).toFloat()

            mDy = (r.nextInt(mScaledBitmapWidth * 3) + 1) / mScaledBitmapWidth.toFloat()
            mDy *= (if (r.nextInt() % 2 == 0) 1 else -1).toFloat()


        }

        private fun createScaledBitmap(r: Random) {



            mScaledBitmapWidth = r.nextInt(2 * BITMAP_SIZE) + BITMAP_SIZE

            r2 = (1..4).random()
            Log.i(TAG, "r2 = $r2")
            mScaledBitmap = if (r2 == 1) Bitmap.createScaledBitmap(mBitmap2!!, mScaledBitmapWidth, mScaledBitmapWidth, false) //25% chance to spawn ebola
            else Bitmap.createScaledBitmap(mBitmap!!, mScaledBitmapWidth, mScaledBitmapWidth, false) //75% chance to spawn covid


        }

        // Start moving the VirusView & updating the display
        fun start() {

            // Creates a WorkerThread
            val executor = Executors.newScheduledThreadPool(1)

            mMoverFuture = executor.scheduleWithFixedDelay({
                if (moveWhileOnScreen()) {
                    postInvalidate()
                } else
                    stop(false)
            }, 0, REFRESH_RATE.toLong(), TimeUnit.MILLISECONDS)
        }

        // Returns true if the VirusView intersects position (x,y)
        @Synchronized
        fun intersects(x: Float, y: Float): Boolean {

            val xDist = x - (mXPos + mRadius)
            val yDist = y - (mYPos + mRadius)

            return xDist * xDist + yDist * yDist <= mRadiusSquared

        }


        private fun stop(wasPopped: Boolean) {

            if (null != mMoverFuture) {

                if (!mMoverFuture!!.isDone) {
                    mMoverFuture!!.cancel(true)
                }

                // This work will be performed on the UI Thread
                mFrame!!.post {
                    // Remove the VirusView from mFrame
                    mFrame!!.removeView(this@VirusView)

                    // If the virus was popped by user,
                    // play the popping sound
                    if (wasPopped) {
                        mSoundPool!!.play(
                                mSoundID, mStreamVolume,
                                mStreamVolume, 1, 0, 1.0f
                        )
                    }
                }
            }



        }

        // Change the Virus's speed and direction
        @Synchronized
        fun deflect(velocityX: Float, velocityY: Float) {
            mDx = velocityX / REFRESH_RATE
            mDy = velocityY / REFRESH_RATE
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