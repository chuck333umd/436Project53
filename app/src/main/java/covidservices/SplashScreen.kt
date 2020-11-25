package covidservices

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.gesture.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import covidservices.R
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.random.nextInt


class SplashScreen : Activity() {

    // The Main view
    private var mFrame: FrameLayout? = null

    private var mBitmap: Bitmap? = null
    private var mBitmap2: Bitmap? = null

    private var r2 = 0

    // Display dimensions
    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0

    // Sound variables

    // AudioManager
    private var mAudioManager: AudioManager? = null

    // SoundPool
    private var mSoundPool: SoundPool? = null

    // ID for the bubble popping sound
    private var mSoundID: Int = 0

    // Audio volume
    private var mStreamVolume: Float = 0.toFloat()

    // Gesture Detector
    private var mGestureDetector: GestureDetector? = null

    // Gesture Library
    private lateinit var mLibrary: GestureLibrary

    private lateinit var bList: ArrayList<BubbleView>

    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        // Set up user interface
        mFrame = findViewById<View>(R.id.frame) as FrameLayout

        // Load basic bubble Bitmap

        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.b64)
        mBitmap2 =  BitmapFactory.decodeResource(resources, R.drawable.e64)

        bList = ArrayList<BubbleView>()
        var xpos = 0F
        var ypos = 0F

        // DONE - Fetch GestureLibrary from raw TODO

        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)
        if (!mLibrary.load()) {
            finish()
        }

        val gestureOverlay = findViewById<View>(R.id.gestures_overlay) as GestureOverlayView

        // ??? - Make this the target of gesture detection callbacks TODO



        // TODO - implement OnTouchListener to pass all events received by the
        // TODO gestureOverlay to the basic gesture detector

        //gestureOverlay.setOnTouchListener { v, event -> true || false }

        gestureOverlay.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                var intersectFlag: Boolean = false
                var intersectFlagUp: Boolean = false
                var xPos: Float = event.x
                var yPos: Float = event.y
                var mostRecent: Int = 0
                var num: Int = 0
                var num2: Int = 0



                v.performClick()


                when (event.actionMasked) {


                    MotionEvent.ACTION_DOWN -> {

                        Log.i(TAG,"SetPos: " + event.x + ", " + event.y )
                        xpos = event.x
                        ypos = event.y
                        Log.i(TAG,"Pos: " + xpos + ", " + ypos )

                    }

                    MotionEvent.ACTION_UP -> {
                        Log.i(TAG,"Pos: " + xpos + ", " + ypos )

                        if ((xpos == event.x) && (ypos == event.y)) {

                            bList.forEachIndexed() { i, bv ->
                                mostRecent = bList[i].p

                                if (bv.intersects(xPos, yPos)) {
                                    intersectFlag = true
                                    num = bList[i].p
                                }
                            }
                            if (bList.size < 1 || (!intersectFlag))
                                synchronized(bList) {
                                    bList.add(BubbleView(applicationContext, event.x, event.y, bList.size, System.currentTimeMillis()))
                                    mFrame?.addView(bList[bList.size - 1])
                                    bList[bList.size - 1].start()
                                }
                            if (bList.size > 0 && intersectFlag) {
                                if (num != mostRecent) {
                                    bList[num].stop(true)
                                } else {
                                    if (bList[mostRecent].t < (System.currentTimeMillis() - 100L)) {
                                        bList[num].stop(true)
                                    }
                                }

                            }


                            Log.i(TAG, "ACTION_DOWN at " + event.x + ", " + event.y)
                        }


                    }

                }

                return true
            }
        })


        // Uncomment next line to turn off gesture highlights
        // gestureOverlay.setUncertainGestureColor(Color.TRANSPARENT);

        // Loads the gesture library
        mLibrary.apply {
            if (!load()) {
                Log.i(TAG, "Could not load Gesture Library")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Manage bubble popping sound
        // Use AudioManager.STREAM_MUSIC as stream type
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mStreamVolume = mAudioManager!!
                .getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / mAudioManager!!.getStreamMaxVolume(
                AudioManager.STREAM_MUSIC
        )

        val musicAttribute = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        mSoundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(musicAttribute)
                .build()

        mSoundID = mSoundPool!!.load(this, R.raw.bubble_pop, 1)
        // setupGestureDetector()
        mSoundPool!!.setOnLoadCompleteListener { soundPool, sampleId, status ->  }

        mSoundID = mSoundPool!!.load(this, R.raw.bubble_pop, 1)


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {

            // Get the size of the display so this View knows where borders are
            mDisplayWidth = mFrame!!.width
            mDisplayHeight = mFrame!!.height

        }
    }

    private fun addTen() {
        for (i in 1..10) {

            var xPos = kotlin.random.Random.nextInt(1..mDisplayWidth)
            var yPos = kotlin.random.Random.nextInt(1..mDisplayHeight)
            synchronized(bList) {
                bList.add(BubbleView(applicationContext, xPos.toFloat(), yPos.toFloat(), bList.size, System.currentTimeMillis()))
                mFrame?.addView(bList[bList.size - 1])
                bList[bList.size - 1].start()
            }

        }

    }


    override fun onPause() {

        // Release all SoundPool resources

        mSoundPool!!.unload(mSoundID)
        mSoundPool!!.release()
        mSoundPool = null

        super.onPause()
    }


    // BubbleView is a View that displays a bubble.
    // This class handles animating, drawing, and popping amongst other actions.
    // A new BubbleView is created for each bubble on the display

    inner class BubbleView internal constructor(context: Context, x: Float, y: Float, var p: Int, var t: Long) : View(context) {
        private val mPainter = Paint()
        private var mMoverFuture: ScheduledFuture<*>? = null
        private var mScaledBitmapWidth: Int = 0
        private var mScaledBitmap: Bitmap? = null
        private val BITMAP_SIZE = 64
        private val REFRESH_RATE = 40

        // location, speed and direction of the bubble
        private var mXPos: Float = 0.toFloat()
        private var mYPos: Float = 0.toFloat()
        private var mDx: Float = 0.toFloat()
        private var mDy: Float = 0.toFloat()
        private val mRadius: Float
        private val mRadiusSquared: Float
        private var mRotate: Long = 0
        private var mDRotate: Long = 0

        // Return true if the BubbleView is not on the screen after the move
        // operation
        private val isOutOfView: Boolean
            get() = (mXPos < 0 - mScaledBitmapWidth || mXPos > mDisplayWidth
                    || mYPos < 0 - mScaledBitmapWidth || mYPos > mDisplayHeight)

        init {
            Log.i(TAG, "Creating Bubble at: x:$x y:$y")

            // Create a new random number generator to
            // randomize size, rotation, speed and direction
            val r = Random()

            // Creates the bubble bitmap for this BubbleView
            createScaledBitmap(r)

            // Radius of the Bitmap
            mRadius = (mScaledBitmapWidth / 2).toFloat()
            mRadiusSquared = mRadius * mRadius

            // Adjust position to center the bubble under user's finger
            mXPos = x - mRadius
            mYPos = y - mRadius

            // Set the BubbleView's speed and direction
            setSpeedAndDirection(r)

            // Set the BubbleView's rotation
            setRotation(r)

            mPainter.isAntiAlias = true

        }

        private fun setRotation(r: Random) {

            if (speedMode == RANDOM) {

                // Set rotation in range [1..3]
                mDRotate = ((r.nextInt(3 * BITMAP_SIZE) + 1) / mScaledBitmapWidth).toLong()
            } else {
                mDRotate = 0
            }
        }


        fun changeVector(dX: Float, dY: Float) {
            mDx = dX
            mDy = dY

        }
        private fun setSpeedAndDirection(r: Random) {

            // Used by test cases
            when (speedMode) {

                SINGLE -> {

                    mDx = 20f
                    mDy = 20f
                }

                STILL -> {

                    // No speed
                    mDx = 0f
                    mDy = 0f
                }

                else -> {

                    // Set movement direction and speed
                    // Limit movement speed in the x and y
                    // direction to [-3..3] pixels per movement.

                    mDx = (r.nextInt(mScaledBitmapWidth * 3) + 1) / mScaledBitmapWidth.toFloat()
                    mDx *= (if (r.nextInt() % 2 == 0) 1 else -1).toFloat()

                    mDy = (r.nextInt(mScaledBitmapWidth * 3) + 1) / mScaledBitmapWidth.toFloat()
                    mDy *= (if (r.nextInt() % 2 == 0) 1 else -1).toFloat()
                }
            }
        }

        private fun createScaledBitmap(r: Random) {

            if (speedMode != RANDOM) {
                mScaledBitmapWidth = BITMAP_SIZE * 3
            } else {

                // Set scaled bitmap size in range [1..3] * BITMAP_SIZE
                mScaledBitmapWidth = r.nextInt(2 * BITMAP_SIZE) + BITMAP_SIZE

            }

            // Create the scaled bitmap using size set above
            r2 = (1..4).random()
            Log.i(TAG, "r2 = $r2")
            if (r2 == 1)   mScaledBitmap = Bitmap.createScaledBitmap(mBitmap2!!, mScaledBitmapWidth, mScaledBitmapWidth, false)
            else mScaledBitmap = Bitmap.createScaledBitmap(mBitmap!!, mScaledBitmapWidth, mScaledBitmapWidth, false)


        }

        // Start moving the BubbleView & updating the display
        fun start() {

            // Creates a WorkerThread
            val executor = Executors
                    .newScheduledThreadPool(1)

            // Execute the run() in Worker Thread every REFRESH_RATE
            // milliseconds
            // Save reference to this job in mMoverFuture
            mMoverFuture = executor.scheduleWithFixedDelay({
                // Implement movement logic.
                // Each time this method is run the BubbleView should
                // move one step. If the BubbleView exits the display,
                // stop the BubbleView's Worker Thread.
                // Otherwise, request that the BubbleView be redrawn.

                if (moveWhileOnScreen()) {
                    postInvalidate()
                } else
                    stop(false)
            }, 0, REFRESH_RATE.toLong(), TimeUnit.MILLISECONDS)
        }

        // Returns true if the BubbleView intersects position (x,y)
        @Synchronized
        fun intersects(x: Float, y: Float): Boolean {

            // Return true if the BubbleView intersects position (x,y)

            val xDist = x - (mXPos + mRadius)
            val yDist = y - (mYPos + mRadius)

            return xDist * xDist + yDist * yDist <= mRadiusSquared

        }

        // Cancel the Bubble's movement
        // Remove Bubble from mFrame
        // Play pop sound if the BubbleView was popped

        fun stop(wasPopped: Boolean) {

            if (null != mMoverFuture) {

                if (!mMoverFuture!!.isDone) {
                    mMoverFuture!!.cancel(true)
                }

                // This work will be performed on the UI Thread
                mFrame!!.post {
                    // Remove the BubbleView from mFrame
                    mFrame!!.removeView(this@BubbleView)

                    // If the bubble was popped by user,
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

        // Change the Bubble's speed and direction
        @Synchronized
        fun deflect(velocityX: Float, velocityY: Float) {
            mDx = velocityX / REFRESH_RATE
            mDy = velocityY / REFRESH_RATE
        }

        // Draw the Bubble at its current location
        @Synchronized
        override fun onDraw(canvas: Canvas) {

            // save the canvas
            canvas.save()

            // Increase the rotation of the original image by mDRotate
            mRotate += mDRotate

            // Rotate the canvas by current rotation
            // Hint - Rotate around the bubble's center, not its position

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

        // Returns true if the BubbleView is still on the screen after the move
        // operation
        @Synchronized
        private fun moveWhileOnScreen(): Boolean {

            // Move the BubbleView

            mXPos += mDx
            mYPos += mDy

            return !isOutOfView

        }
    }

    override fun onBackPressed() {
        openOptionsMenu()
    }




    private fun exitRequested() {
        super.onBackPressed()
    }

    companion object {

        private val MIN_PRED_SCORE = 3.0

        // These variables are for testing purposes, do not modify
        private val RANDOM = 0
        private val SINGLE = 1
        private val STILL = 2
        var speedMode = RANDOM

        private val TAG = "Lab-Gestures"
    }
}