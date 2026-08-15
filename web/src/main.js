const canvas = document.querySelector('#motion-canvas')

if (canvas) {
  const ctx = canvas.getContext('2d', { alpha: true })
  const reduceMotion = matchMedia('(prefers-reduced-motion: reduce)')
  const lowPower = navigator.hardwareConcurrency && navigator.hardwareConcurrency <= 4
  let width = 0
  let height = 0
  let dpr = 1
  let frame = 0
  let running = true
  let pointerX = 0
  let pointerY = 0
  let targetX = 0
  let targetY = 0

  const resize = () => {
    const rect = canvas.getBoundingClientRect()
    width = rect.width
    height = rect.height
    dpr = Math.min(devicePixelRatio || 1, lowPower ? 1 : 1.5)
    canvas.width = Math.round(width * dpr)
    canvas.height = Math.round(height * dpr)
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  }

  const pointer = (event) => {
    const point = event.touches?.[0] || event
    targetX = (point.clientX / innerWidth - 0.5) * 18
    targetY = (point.clientY / innerHeight - 0.5) * 12
  }

  const line = (x1, y1, x2, y2, color = 'rgba(74,143,220,.2)') => {
    ctx.strokeStyle = color
    ctx.beginPath()
    ctx.moveTo(x1, y1)
    ctx.lineTo(x2, y2)
    ctx.stroke()
  }

  const draw = (time = 0) => {
    if (!running) return
    pointerX += (targetX - pointerX) * 0.035
    pointerY += (targetY - pointerY) * 0.035
    ctx.clearRect(0, 0, width, height)
    ctx.lineWidth = 1
    const horizon = height * 0.57 + pointerY
    const originX = width * 0.68 + pointerX

    ctx.save()
    ctx.globalAlpha = 0.55
    const gridGap = width < 700 ? 54 : 42
    for (let x = -width; x < width * 2; x += gridGap) line(originX + (x - originX) * 0.65, horizon, x + pointerX, height, 'rgba(65,127,190,.13)')
    for (let y = horizon; y < height; y += 35) line(0, y, width, y, 'rgba(65,127,190,.12)')
    ctx.restore()

    const panelX = width * (width < 700 ? 0.08 : 0.53) + pointerX
    const panelW = width * (width < 700 ? 0.88 : 0.43)
    const panelY = height * 0.2 + pointerY
    ctx.fillStyle = 'rgba(8,13,21,.7)'
    ctx.strokeStyle = 'rgba(72,154,230,.23)'
    ctx.fillRect(panelX, panelY, panelW, height * 0.34)
    ctx.strokeRect(panelX, panelY, panelW, height * 0.34)
    line(panelX, panelY + 30, panelX + panelW, panelY + 30)
    ctx.fillStyle = 'rgba(104,143,188,.5)'
    ctx.font = '9px monospace'
    ctx.fillText('COMPOSITION / LIVE PREVIEW', panelX + 13, panelY + 19)

    const orbitX = panelX + panelW * 0.58
    const orbitY = panelY + height * 0.19
    for (let r = 38; r < 125; r += 36) {
      ctx.strokeStyle = `rgba(${r + 30},${165 - r / 3},255,.${r === 110 ? 3 : 14})`
      ctx.beginPath()
      ctx.ellipse(orbitX, orbitY, r * 1.35, r * 0.5, time / 24000 + r, 0, Math.PI * 2)
      ctx.stroke()
    }
    ctx.fillStyle = 'rgba(43,213,255,.9)'
    ctx.fillRect(orbitX - 2, orbitY - 2, 4, 4)

    const timelineY = height * 0.68 + pointerY * 0.4
    const timelineX = width * 0.39 + pointerX * 0.5
    const timelineW = width * 0.58
    line(timelineX, timelineY, timelineX + timelineW, timelineY, 'rgba(92,157,223,.32)')
    const clipCount = lowPower ? 3 : 5
    for (let i = 0; i < clipCount; i += 1) {
      const delay = i * 0.7
      const entrance = reduceMotion.matches ? 1 : Math.min(1, Math.max(0, (Math.sin(time / 1800 - delay) + 1.3)))
      const y = timelineY + 18 + i * 38
      const x = timelineX + (i % 2) * 42 + (1 - entrance) * 60
      const clipW = timelineW * (0.45 + (i % 3) * 0.12)
      ctx.fillStyle = i === 1 ? 'rgba(100,70,176,.22)' : i === 3 ? 'rgba(32,145,137,.2)' : 'rgba(30,103,164,.22)'
      ctx.strokeStyle = i === 1 ? 'rgba(139,108,255,.6)' : 'rgba(48,174,235,.46)'
      ctx.fillRect(x, y, clipW, 25)
      ctx.strokeRect(x, y, clipW, 25)
      ctx.fillStyle = 'rgba(196,218,239,.45)'
      ctx.font = '8px monospace'
      ctx.fillText(['VIDEO_01', 'MOTION FX', 'TITLE', 'AUDIO', 'ADJUSTMENT'][i], x + 8, y + 16)
      for (let k = 1; k < 4; k += 1) {
        ctx.save()
        ctx.translate(x + clipW * k / 4, y + 12)
        ctx.rotate(Math.PI / 4)
        ctx.fillStyle = 'rgba(99,218,255,.75)'
        ctx.fillRect(-2, -2, 4, 4)
        ctx.restore()
      }
    }
    const progress = reduceMotion.matches ? 0.42 : (time / 7000) % 1
    const playX = timelineX + progress * timelineW
    line(playX, timelineY - 10, playX, Math.min(height, timelineY + 225), 'rgba(73,226,255,.85)')
    ctx.fillStyle = '#49e2ff'
    ctx.beginPath()
    ctx.moveTo(playX - 5, timelineY - 10)
    ctx.lineTo(playX + 5, timelineY - 10)
    ctx.lineTo(playX, timelineY - 3)
    ctx.fill()

    if (!lowPower && width > 700) {
      ctx.strokeStyle = 'rgba(117,105,236,.42)'
      ctx.beginPath()
      ctx.moveTo(width * .1, height * .78)
      ctx.bezierCurveTo(width * .22, height * .78, width * .26, height * .58, width * .38, height * .61)
      ctx.stroke()
      for (let i = 0; i < 20; i += 1) {
        const x = width * .1 + i * width * .014
        const amp = 6 + Math.sin(i * 2.1 + time / 350) * 17
        line(x, height * .88 - amp, x, height * .88 + amp, 'rgba(45,172,224,.35)')
      }
    }

    frame = requestAnimationFrame(draw)
  }

  const visibility = () => {
    running = !document.hidden
    if (running) frame = requestAnimationFrame(draw)
    else cancelAnimationFrame(frame)
  }

  resize()
  addEventListener('resize', resize, { passive: true })
  addEventListener('pointermove', pointer, { passive: true })
  addEventListener('touchmove', pointer, { passive: true })
  document.addEventListener('visibilitychange', visibility)
  frame = requestAnimationFrame(draw)

  addEventListener('pagehide', () => {
    running = false
    cancelAnimationFrame(frame)
    removeEventListener('resize', resize)
    removeEventListener('pointermove', pointer)
    removeEventListener('touchmove', pointer)
    document.removeEventListener('visibilitychange', visibility)
  }, { once: true })
}
