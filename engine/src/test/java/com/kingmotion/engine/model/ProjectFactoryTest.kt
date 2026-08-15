package com.kingmotion.engine.model
import org.junit.Assert.assertEquals
import org.junit.Test
class ProjectFactoryTest {@Test fun suppliesNumberedDefaultAndRealSettings(){val p=ProjectFactory.create("",2560,1440,60f,projectNumber=3,nowMs=1);assertEquals("King Motion Project 03",p.name);assertEquals(60f,p.compositions.single().frameRate);assertEquals(2560,p.compositions.single().width)}}
