package com.kingmotion.engine.timeline

import com.kingmotion.engine.model.*
import org.junit.Assert.*
import org.junit.Test

class TimelineEditorTest {
    private val layer = Layer(name="clip", type=LayerType.VIDEO, startTimeUs=0, durationUs=10_000, order=0, sourceUri="content://clip")
    private val composition = Composition(name="c",width=1920,height=1080,frameRate=30f,durationUs=10_000,layers=listOf(layer))
    @Test fun splitIsNonDestructiveAndOffsetsSource(){val result=TimelineEditor.split(composition,layer.id,4_000);assertEquals(listOf(4_000L,6_000L),result.layers.map{it.durationUs});assertEquals(4_000L,result.layers[1].sourceInTimeUs)}
    @Test fun trimStartPreservesSourceAlignment(){val result=TimelineEditor.trimStart(composition,layer.id,2_000).layers.single();assertEquals(8_000,result.durationUs);assertEquals(2_000,result.sourceInTimeUs)}
    @Test fun historySupportsUndoAndRedo(){val history=EditHistory(composition);history.execute(TimelineEditor.delete(composition,layer.id));assertTrue(history.current.layers.isEmpty());history.undo();assertEquals(1,history.current.layers.size);history.redo();assertTrue(history.current.layers.isEmpty())}
}
