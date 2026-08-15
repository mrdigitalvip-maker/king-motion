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
