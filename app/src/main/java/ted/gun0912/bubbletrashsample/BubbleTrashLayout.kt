/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
package ted.gun0912.bubbletrashsample

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Vibrator
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import ted.gun0912.bubbletrashsample.BubbleTrashLayout

internal class BubbleTrashLayout(context: Context) : FrameLayout(context) {

    private var magnetismApplied = false
    private var isVibrateInThisSession = false

    fun show() {
        animate()
            .translationY(0f)
            .setInterpolator(FastOutSlowInInterpolator())
            .withStartAction {
                isVisible = true
            }
            .setDuration(ANIMATE_DURATION.toLong())
            .start()

    }

    fun hide() {
        animate()
            .translationY(-height.toFloat())
            .setInterpolator(FastOutSlowInInterpolator())
            .withEndAction {
                isInvisible = true
            }
            .setDuration(ANIMATE_DURATION.toLong())
            .start()
    }

    fun applyMagnetism() {
        if (!magnetismApplied) {
            magnetismApplied = true
            playAnimation(R.animator.bubble_trash_shown_magnetism_animator)
        }
    }

    private fun playAnimation(animationResourceId: Int) {
        if (!isInEditMode) {
            (AnimatorInflater.loadAnimator(context, animationResourceId) as AnimatorSet)
                .apply {
                    setTarget(getChildAt(0))
                }.start()
        }
    }

    fun vibrate() {
        if (!isVibrateInThisSession) {
            isVibrateInThisSession = true

            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
                .vibrate(VIBRATION_DURATION_IN_MS)
        }
    }

    fun releaseMagnetism() {
        if (magnetismApplied) {
            magnetismApplied = false
            playAnimation(R.animator.bubble_trash_hide_magnetism_animator)
        }
        isVibrateInThisSession = false
    }

    companion object {
        const val VIBRATION_DURATION_IN_MS = 70L
        private const val ANIMATE_DURATION = 500
    }
}