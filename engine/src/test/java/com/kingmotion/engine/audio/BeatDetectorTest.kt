package com.kingmotion.engine.audio
import org.junit.Assert.assertTrue
import org.junit.Test
class BeatDetectorTest {@Test fun detectsSeparatedEnergyPeaks(){val samples=FloatArray(12_000); listOf(2_000,5_000,8_000).forEach{start->for(i in start until start+300)samples[i]=1f};val beats=BeatDetector.detect(samples,10_000,windowSize=200);assertTrue(beats.size>=3)}}
