package com.kingmotion.engine.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectCreationTest {
    @Test
    fun suppliesNumberedDefaultAndRealSettings() {
        val project = ProjectFactory.create(
            name = "",
            width = 2560,
            height = 1440,
            fps = 60f,
            projectNumber = 3,
            nowMs = 1L,
        )

        assertEquals("King Motion Project 03", project.name)
        assertEquals(1L, project.createdAtEpochMs)
        assertEquals(60f, project.compositions.single().frameRate, 0f)
        assertEquals(2560, project.compositions.single().width)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUnsupportedFrameRate() {
        ProjectFactory.create(name = "Invalid", width = 1920, height = 1080, fps = 25f)
    }
}
