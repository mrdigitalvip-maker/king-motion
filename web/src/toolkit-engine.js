const id = () => globalThis.crypto?.randomUUID?.() || `km-${Date.now()}-${Math.random().toString(36).slice(2)}`
const clone = value => structuredClone(value)

export const defaultTransform = () => ({ x: 50, y: 50, scaleX: 100, scaleY: 100, rotation: 0, anchorX: 50, anchorY: 50, flipX: false, flipY: false, opacity: 100 })
export const createLayer = (type, name, extra = {}) => ({ id: id(), type, name, start: 0, end: 12, trimStart: 0, visible: true, locked: false, muted: false, volume: 100, speed: 1, transform: defaultTransform(), effects: [], keyframes: {}, masks: [], ...extra })

export function duplicateLayer(layer) {
  const copy = clone(layer)
  copy.id = id()
  copy.name = `${layer.name} Copy`
  return copy
}

export function splitLayer(layer, at) {
  if (!(at > layer.start && at < layer.end)) throw new RangeError('Playhead must be inside the layer')
  const left = clone(layer), right = clone(layer)
  left.end = at
  right.id = id()
  right.name = `${layer.name} split`
  right.start = at
  right.trimStart = (layer.trimStart || 0) + (at - layer.start) * (layer.speed || 1)
  return [left, right]
}

export function extractAudioLayer(video) {
  if (video.type !== 'Video') throw new TypeError('Audio can only be extracted from a video layer')
  return createLayer('Audio', `${video.name} · Audio`, { start: video.start, end: video.end, trimStart: video.trimStart || 0, sourceLayerId: video.id, sourceMime: video.mime, needsDemux: true, fadeIn: 0, fadeOut: 0 })
}

export const createCameraTrack = (duration, start = 0) => createLayer('Camera', 'Global Camera Track', { start, end: start + duration, zoom: 100, trackingMode: 'transform-2d', status: 'Ready' })
export const createMask = (shape = 'Rectangle') => ({ id: id(), shape, feather: 0, expansion: 0, opacity: 100, invert: false, points: [], keyframes: {} })
export const defaultChromaKey = () => ({ enabled: false, color: '#00ff00', threshold: 35, tolerance: 20, edgeSoftness: 10, spillReduction: 25, strength: 100 })

const audioPresets = [
  ['Deep Bass','EQ',{low:8,mid:0,high:-2}],['Cinema Voice','EQ',{low:2,mid:4,high:2}],['Radio','Band Pass',{lowCut:350,highCut:3400}],
  ['Telephone','Band Pass',{lowCut:500,highCut:2500}],['Distant','Reverb',{room:65,mix:35}],['Dark Echo','Delay',{time:.38,feedback:42,mix:32}],
  ['Dream Reverb','Reverb',{room:82,mix:46}],['Hall','Reverb',{room:90,mix:38}],['Tight Bass','EQ',{low:5,mid:1,high:0}],
  ['Wide Audio','Stereo Pan',{width:145,pan:0}],['Soft Voice','EQ',{low:1,mid:2,high:-1}],['Impact','Gain',{gain:6}],['Vintage','Low Pass',{frequency:7200}],
  ['Low Pass Transition','Low Pass',{frequency:1200}],['High Pass Transition','High Pass',{frequency:1800}]
]
export const audioEffectRecipes = Object.freeze(audioPresets.map(([name, engine, parameters], index) => ({ id: `audio-${index + 1}`, name, engine, parameters, editable: true, local: true })))

const styleCategories = ['Clean','Cinematic','Bold','Minimal','Luxury','Gaming','Tech','Neon','Dark','Social','Subtitle','Impact','Retro','Editorial']
const families = ['system-ui','Arial, sans-serif','Georgia, serif','Verdana, sans-serif','Trebuchet MS, sans-serif','Courier New, monospace']
export const textStyleRecipes = Object.freeze(Array.from({ length: 100 }, (_, index) => ({
  id: `text-style-${index + 1}`, name: `${styleCategories[index % styleCategories.length]} ${Math.floor(index / styleCategories.length) + 1}`,
  category: styleCategories[index % styleCategories.length], family: families[index % families.length], weight: [400, 500, 600, 700, 800, 900][index % 6],
  size: 44 + (index % 7) * 8, tracking: (index % 5) - 2, lineHeight: 1.05 + (index % 4) * .1, align: ['left','center','right'][index % 3],
  fill: `hsl(${index * 47 % 360} 75% ${index % 3 ? 72 : 55}%)`, stroke: index % 4 === 0 ? '#05070b' : 'transparent', shadow: index % 3 === 0 ? '0 6px 18px #0009' : 'none', editable: true
})))

export function exportAudit(project, capabilities) {
  const unsupported = project.layers.flatMap(layer => (layer.effects || []).filter(effect => effect.status === 'Planned').map(effect => `${layer.name}: ${effect.name}`))
  const warnings = []
  if (!capabilities.mediaRecorder) warnings.push('MediaRecorder is unavailable in this browser.')
  if (project.layers.some(layer => layer.needsRelink)) warnings.push('Runtime media must be relinked before export.')
  return { resolution: `${project.width} × ${project.height}`, fps: project.fps, layers: project.layers.length, duration: project.duration, unsupported, warnings }
}
