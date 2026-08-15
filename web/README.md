# King Motion web

The official King Motion landing page is intentionally isolated from the Android modules in the repository root.

## Local development

```bash
npm run build
```

Build and preview the production output:

```bash
npm run build
npm run preview
```

## Vercel settings

| Setting | Value |
| --- | --- |
| Root Directory | `web` |
| Framework Preset | `Other` |
| Build Command | `npm run build` |
| Output Directory | `dist` |

The production build contains static entry points for `/`, `/privacy/`, and `/terms/`.
