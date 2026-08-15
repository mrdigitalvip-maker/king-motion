package com.kingmotion.engine.keyframes

import org.junit.Assert.assertEquals
import org.junit.Test

class AnimatedFloatTest {
    @Test fun returnsInitialValueBeforeFirstKeyframe() {
        val value = AnimatedFloat(.25f, listOf(Keyframe(1_000, 1f)))
        assertEquals(.25f, value.valueAt(500), 0.0001f)
    }

    @Test fun linearlyInterpolatesBetweenKeyframes() {
        val value = AnimatedFloat(0f, listOf(Keyframe(0, 0f), Keyframe(1_000, 10f)))
        assertEquals(5f, value.valueAt(500), 0.0001f)
    }

    @Test fun holdsLastKeyframeValue() {
        val value = AnimatedFloat(0f, listOf(Keyframe(100, 2f), Keyframe(200, 4f)))
        assertEquals(4f, value.valueAt(300), 0.0001f)
    }
}
