import { createLayer } from './toolkit-engine.js'

export const MIN_1080_SHORT_EDGE = 1080

export function validateMediaMetadata({ type, width = 0, height = 0, duration = 0 }) {
  if (!['Video', 'Image', 'Audio'].includes(type)) return { ok: false, reason: 'Unsupported media type.' }
  if (type !== 'Audio' && Math.min(width, height) < MIN_1080_SHORT_EDGE) return { ok: false, reason: `Media rejected\nDetected: ${width}×${height}\nMinimum: 1920×1080 or equivalent 1080-class media.` }
  if (type !== 'Image' && (!Number.isFinite(duration) || duration <= 0)) return { ok: false, reason: 'Invalid media duration.' }
  if (type !== 'Audio' && Math.max(width, height) / Math.min(width, height) > 3) return { ok: false, reason: 'Problematic aspect ratio.' }
  return { ok: true }
}

export function createImportedLayer({ type, name, mime, size, duration, width = 0, height = 0, playhead = 0 }) {
  const start = Math.max(0, playhead), usefulDuration = type === 'Image' ? Math.max(1, duration || 5) : duration
  return createLayer(type, name, { mime, size, duration: usefulDuration, width, height, start, end: start + usefulDuration, sourceIn: 0, sourceOut: usefulDuration, needsRelink: true })
}

export function createWaveform(samples, points = 500) {
  const output = [], step = Math.max(1, Math.ceil(samples.length / points))
  for (let i = 0; i < samples.length; i += step) {
    let peak = 0
    for (let j = i; j < Math.min(samples.length, i + step); j++) peak = Math.max(peak, Math.abs(samples[j]))
    output.push(peak)
  }
  return output
}
