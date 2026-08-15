const KEY='king-motion-projects-v2'
export const uid=()=>crypto.randomUUID?.()||`${Date.now()}-${Math.random()}`
export const projects=()=>{try{return JSON.parse(localStorage.getItem(KEY)||'[]')}catch{return[]}}
export const saveProject=p=>{const list=projects(),i=list.findIndex(x=>x.id===p.id);p.updatedAt=Date.now(); const safe=JSON.parse(JSON.stringify(p,(k,v)=>k==='url'||k==='audioBuffer'?undefined:v)); if(i<0)list.unshift(safe);else list[i]=safe;localStorage.setItem(KEY,JSON.stringify(list));return p}
export const getProject=id=>projects().find(p=>p.id===id)||null
export const removeProject=id=>localStorage.setItem(KEY,JSON.stringify(projects().filter(p=>p.id!==id)))
export const makeProject=(options={})=>({id:uid(),name:options.name?.trim()||'King Motion Project 01',type:options.type||'Video Edit',width:+options.width||1080,height:+options.height||1920,aspect:options.aspect||'9:16',fps:+options.fps||30,quality:options.quality||'High',background:options.background||'#000000',duration:12,playhead:0,layers:[],markers:[],updatedAt:Date.now()})
