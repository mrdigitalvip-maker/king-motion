import test from 'node:test'
import assert from 'node:assert/strict'
import { createImportedLayer, createWaveform, validateMediaMetadata } from '../src/media-engine.js'

test('video import is an atomic timeline layer at the playhead', () => {
  const validation = validateMediaMetadata({ type: 'Video', width: 1080, height: 1920, duration: 8 })
  const layer = createImportedLayer({ type: 'Video', name: 'clip.mp4', mime: 'video/mp4', size: 42, duration: 8, width: 1080, height: 1920, playhead: 3 })
  assert.equal(validation.ok, true)
  assert.deepEqual([layer.type, layer.start, layer.end, layer.sourceIn, layer.sourceOut], ['Video', 3, 11, 0, 8])
})

test('quality policy reports measured dimensions and does not inspect fps', () => {
  const result = validateMediaMetadata({ type: 'Video', width: 1280, height: 720, duration: 4, fps: 24 })
  assert.equal(result.ok, false)
  assert.match(result.reason, /Detected: 1280×720/)
})

test('audio waveform is bounded and preserves peaks', () => {
  const wave = createWaveform(Float32Array.from([0, -.5, 1, .2, -.8, 0]), 3)
  assert.deepEqual(wave.slice(0, 2), [.5, 1])
  assert.ok(Math.abs(wave[2] - .8) < 1e-6)
})
