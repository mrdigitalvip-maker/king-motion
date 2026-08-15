export const SUPPORTED_FPS = Object.freeze([24, 30, 48, 60, 120])
export function assertFps(fps) { const value=Number(fps); if(!SUPPORTED_FPS.includes(value)) throw new RangeError(`Unsupported project FPS: ${fps}`); return value }
export const frameDuration=fps=>1/assertFps(fps)
export const frameToTime=(frame,fps)=>Number(frame)*frameDuration(fps)
export const timeToFrame=(time,fps)=>Math.round(Number(time)*assertFps(fps))
export function formatTimecode(time,fps){const rate=assertFps(fps),totalFrames=Math.max(0,timeToFrame(time,rate)),frames=totalFrames%rate,totalSeconds=Math.floor(totalFrames/rate),seconds=totalSeconds%60,minutes=Math.floor(totalSeconds/60)%60,hours=Math.floor(totalSeconds/3600);return[hours,minutes,seconds,frames].map(value=>String(value).padStart(2,'0')).join(':')}
export function parseTimecode(value,fps){const rate=assertFps(fps),parts=String(value).trim().split(':').map(Number);if(parts.length!==4||parts.some(part=>!Number.isInteger(part)||part<0))throw new TypeError('Timecode must use HH:MM:SS:FF');const[hours,minutes,seconds,frames]=parts;if(minutes>59||seconds>59||frames>=rate)throw new RangeError('Timecode is outside the project frame rate');return hours*3600+minutes*60+seconds+frameToTime(frames,rate)}
export const snapTimeToFrame=(time,fps)=>frameToTime(timeToFrame(time,fps),fps)
