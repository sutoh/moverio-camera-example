/*
 * Copyright(C) Seiko Epson Corporation 2016. All rights reserved.
 *
 * Warranty Disclaimers.
 * You acknowledge and agree that the use of the software is at your own risk.
 * The software is provided "as is" and without any warranty of any kind.
 * Epson and its licensors do not and cannot warrant the performance or results
 * you may obtain by using the software.
 * Epson and its licensors make no warranties, express or implied, as to non-infringement,
 * merchantability or fitness for any particular purpose.
 */

package com.example.p141.moverio_first

import android.hardware.Camera
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.util.Size
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView

import java.io.IOException

class MainActivity : Activity(), SurfaceHolder.Callback {

    private var mResolutionIndex: Int = 0

    private var mSurfaceHolder: SurfaceHolder? = null
    private var mCamera: Camera? = null

    private var mTextView: TextView? = null

    private val resolution: Size
        get() {
            if (RESOLUTIONS.size <= mResolutionIndex) {
                mResolutionIndex = 0
            } else if (mResolutionIndex < 0) {
                mResolutionIndex = RESOLUTIONS.size - 1
            }
            return RESOLUTIONS[mResolutionIndex]
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 19) {
            val window = window
            val view = window.decorView
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        setContentView(R.layout.activity_main)

        mTextView = findViewById(R.id.textView)

        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView) as SurfaceView
        mSurfaceHolder = surfaceView.holder
        mSurfaceHolder!!.addCallback(this)
    }

    override fun onResume() {
        super.onResume()
        mResolutionIndex = 0
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mCamera = Camera.open()
        try {
            mCamera!!.setPreviewDisplay(mSurfaceHolder)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        changeResolution()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    mResolutionIndex--
                    changeResolution()
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    mResolutionIndex++
                    changeResolution()
                }
                else -> {
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun changeResolution() {
        mCamera!!.stopPreview()
        val parameters = mCamera!!.parameters
        val resolution = resolution
        parameters.setPreviewSize(resolution.width, resolution.height)
        mCamera!!.parameters = parameters
        mCamera!!.startPreview()
        setText(resolution)
    }

    private fun setText(size: Size) {
        val text = size.width.toString() + " x " + size.height.toString()
        mTextView!!.text = text
    }

    companion object {

        private val RESOLUTIONS = arrayOf(Size(640, 480), Size(1280, 720), Size(1920, 1080))
    }
}
