package com.kingmotion.engine.media
import org.junit.Assert.*
import org.junit.Test
class MediaPolicyTest {@Test fun acceptsLandscapeAndPortrait1080p(){val p=VideoImportPolicy();assertEquals(MediaValidation.Accepted,p.validate(MediaInfo(1920,1080,1,false)));assertEquals(MediaValidation.Accepted,p.validate(MediaInfo(1080,1920,1,false)))}@Test fun rejectsLowResolution(){assertTrue(VideoImportPolicy().validate(MediaInfo(1280,720,1,false)) is MediaValidation.Rejected)}}
