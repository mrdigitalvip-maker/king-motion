import { cp, mkdir, rm } from 'node:fs/promises'

const output = new URL('../dist/', import.meta.url)
const root = new URL('../', import.meta.url)

await rm(output, { force: true, recursive: true })
await mkdir(output, { recursive: true })

for (const path of ['index.html', 'src', 'manifest.webmanifest', 'vercel.json']) {
  await cp(new URL(path, root), new URL(path, output), { recursive: true })
}

// Physical fallbacks make deep links work on static hosts as well as through
// the Vercel SPA rewrite.
for (const route of ['editor', 'project/new', 'privacy', 'terms']) {
  await mkdir(new URL(`${route}/`, output), { recursive: true })
  await cp(new URL('index.html', root), new URL(`${route}/index.html`, output))
}

console.log('Built static site in web/dist')
