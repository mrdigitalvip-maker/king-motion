import test from 'node:test'
import assert from 'node:assert/strict'
import { audioEffectRecipes, createCameraTrack, createLayer, createMask, defaultChromaKey, duplicateLayer, exportAudit, extractAudioLayer, splitLayer, textStyleRecipes, trimLayer } from '../src/toolkit-engine.js'

test('split is non-destructive and advances source trim', () => {
  const source = createLayer('Video', 'Clip', { start: 2, end: 10, trimStart: 3, speed: 2, effects: [{ name: 'Glow' }], masks: [createMask()] })
  const [left, right] = splitLayer(source, 6)
  assert.deepEqual([left.start, left.end, right.start, right.end], [2, 6, 6, 10])
  assert.equal(right.trimStart, 11)
  assert.notEqual(right.id, source.id)
  assert.deepEqual(right.effects, source.effects)
  assert.equal(source.end, 10)
})

test('duplicate deeply copies professional layer properties', () => {
  const source = createLayer('Text', 'Title', { keyframes: { opacity: [{ time: 0, value: 0 }] }, masks: [createMask('Ellipse')] })
  const copy = duplicateLayer(source)
  copy.masks[0].opacity = 20
  assert.notEqual(copy.id, source.id)
  assert.equal(source.masks[0].opacity, 100)
})

test('audio extraction aligns with video without claiming browser demux', () => {
  const video = createLayer('Video', 'Interview', { start: 4, end: 14, mime: 'video/mp4' })
  const audio = extractAudioLayer(video)
  assert.deepEqual([audio.start, audio.end, audio.sourceLayerId, audio.needsDemux], [4, 14, video.id, true])
})

test('trim updates source in while preserving non-destructive media timing', () => {
  const source = createLayer('Video', 'Clip', { start: 2, end: 10, trimStart: 3, speed: 2 })
  const trimmed = trimLayer(source, 'start', 5)
  assert.deepEqual([trimmed.start, trimmed.end, trimmed.trimStart], [5, 10, 9])
  assert.equal(source.start, 2)
  assert.equal(trimLayer(source, 'end', 8).end, 8)
})

test('camera, mask, chroma, text and audio recipes expose editable models', () => {
  assert.equal(createCameraTrack(8, 2).end, 10)
  assert.equal(createMask('Polygon').shape, 'Polygon')
  assert.equal(defaultChromaKey().color, '#00ff00')
  assert.equal(textStyleRecipes.length, 100)
  assert.ok(audioEffectRecipes.length >= 15)
  assert.ok(textStyleRecipes.every(recipe => recipe.editable))
})

test('export audit reports real browser and relink limitations', () => {
  const project = { width: 1080, height: 1920, fps: 60, duration: 12, layers: [createLayer('Video', 'Clip', { needsRelink: true })] }
  const audit = exportAudit(project, { mediaRecorder: false })
  assert.equal(audit.layers, 1)
  assert.equal(audit.warnings.length, 2)
})
