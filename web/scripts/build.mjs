import { cp, mkdir, rm } from 'node:fs/promises'

const output = new URL('../dist/', import.meta.url)
const root = new URL('../', import.meta.url)

await rm(output, { force: true, recursive: true })
await mkdir(output, { recursive: true })

for (const path of ['index.html', 'privacy', 'terms', 'src']) {
  await cp(new URL(path, root), new URL(path, output), { recursive: true })
}

console.log('Built static site in web/dist')
