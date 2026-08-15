package com.kingmotion.engine.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectFactoryTest {
    @Test fun `blank project name is generated and format reaches composition`() {
        val config = ProjectConfiguration(width = 2160, height = 3840, frameRate = 60, quality = ProjectQuality.ULTRA)
        val project = ProjectFactory.create("  ", config, index = 3, now = 42)
        assertEquals("King Motion Project 03", project.name)
        assertEquals(2160, project.compositions.single().width)
        assertEquals(60f, project.compositions.single().frameRate)
        assertEquals("9:16", project.configuration.aspectRatio)
    }

    @Test fun `custom dimensions report custom ratio`() {
        assertEquals("Custom", ProjectConfiguration(width = 2000, height = 1080).aspectRatio)
    }
}
