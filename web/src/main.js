import { home, newProject, editor, legal } from './editor.js'
import { aiStudio } from './ai-studio.js'
import { getProject } from './store.js'

const app = document.querySelector('#app')
export const go = (path) => { history.pushState({}, '', path); route() }
export function route() {
  const path = location.pathname.replace(/\/$/, '') || '/'
  if (path === '/') home(app)
  else if (path === '/project/new') newProject(app)
  else if (path === '/editor') editor(app, getProject(new URLSearchParams(location.search).get('id')))
  else if (path === '/ai-studio') aiStudio(app)
  else if (path === '/privacy' || path === '/terms') legal(app, path.slice(1))
  else { history.replaceState({}, '', '/'); home(app) }
}
document.addEventListener('click', e => { const link=e.target.closest('[data-route]'); if(link){e.preventDefault();go(link.getAttribute('href'))} })
addEventListener('popstate', route); route()
