# King Motion Web Studio

A separate, dependency-free browser studio for testing the King Motion editing model. User media stays in browser memory; only serializable project metadata is saved in local storage. Reopening imported media requires relinking because browsers do not grant persistent file access automatically.

## Commands

```bash
npm run lint
npm run typecheck
npm test
npm run build
```

## Vercel

The repository includes both root and `web/` Vercel configurations. Recommended project settings:

| Setting | Value |
| --- | --- |
| Root Directory | repository root (leave blank) |
| Framework Preset | Other |
| Install Command | leave blank / automatic |
| Build Command | `npm run build` |
| Output Directory | `web/dist` |

The root rewrite sends browser navigation (`/editor`, `/project/new`, `/privacy`, and `/terms`) to the SPA entry point. Alternatively, set Root Directory to `web`, Build Command to `npm run build`, and Output Directory to `dist`; `web/vercel.json` supplies the equivalent rewrite.

The textual manifest intentionally has no raster icon. Full cross-browser PWA install promotion may therefore be unavailable.

## AI Image Studio

### Architecture and routes

`/ai-studio` is a responsive SPA workspace implemented by `src/ai-studio.js`. It owns mode, prompt, source image, loading/error/result state, aspect ratio, style, transparency intent, and a bounded local history. The static build emits an `/ai-studio/index.html` fallback, while Vercel rewrites other application routes to the SPA without intercepting `/api`.

The frontend calls only these same-origin serverless endpoints:

- `POST /api/ai/image/create` — prompt-to-image.
- `POST /api/ai/image/edit` — image plus edit prompt.

Both use the shared `AiImageService` abstraction for prompt validation, Gemini request timeouts, response normalization, incomplete-response detection, and rate-limit/error mapping. `process.env.GEMINI_API_KEY` is read exclusively in the serverless runtime. Set it in Vercel project settings. `GEMINI_IMAGE_MODEL` is optional; it allows the deployed model identifier to be changed without shipping a new frontend.

### Workflows and editor integration

**Create** sends the prompt, style, ratio, quality, variation intent, and transparency intent. **Edit** additionally validates and sends an uploaded PNG/JPEG/WebP (10 MB client limit), and offers drag/drop, replace, and clear interactions. Quick actions such as **Remove Background** are honest prompt presets: Gemini is asked for alpha when supported, but the interface explicitly labels transparency as best effort and never claims that a non-transparent response has alpha.

The Home feature card opens the studio directly. Inside the editor, the Layers header, mobile `+` sheet, and selected-image inspector open it with project/layer context. A generated result is transferred through `sessionStorage`: **Insert at Playhead** creates and selects a five-second `Image` layer, while **Replace Selected Layer** updates the selected image layer. Generated pixels are session-local and the layer is marked for relinking after a browser restart, matching the editor's local-media model.

The last eight requests are stored locally where browser quota permits, with metadata-only fallback if an encoded result exceeds quota. Users can reopen/reuse prompts or delete entries. No history is uploaded to a King Motion database.

### Mobile behavior and limitations

Desktop uses input, preview, and settings columns. Tablet and mobile turn settings into an on-demand side sheet, prioritize the preview, keep generation actions reachable, respect safe areas, and prevent page zoom through the existing installed-app viewport. The API currently returns the first usable Gemini image even if multiple variations were requested. Source uploads and generated data are not persisted as project binaries, true transparency depends on model output, and a deployed Gemini key/quota is required for live generation.
