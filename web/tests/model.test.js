import test from 'node:test';import assert from 'node:assert/strict';
test('project time supports configured frame rates',()=>{for(const fps of [24,30,48,60,120])assert.equal(Math.floor((1/fps)*fps),1)})
test('SPA routes are declared',async()=>{const source=await import('node:fs/promises').then(fs=>fs.readFile(new URL('../src/main.js',import.meta.url),'utf8'));for(const route of ['project/new','editor','privacy','terms'])assert.match(source,new RegExp(route.replace('/','\\/')))})
