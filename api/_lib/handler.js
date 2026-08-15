const {AiImageService}=require('./ai-image-service')
const MAX_BODY=15_000_000
function imageHandler(mode){return async function(req,res){res.setHeader('Cache-Control','no-store');if(req.method!=='POST'){res.setHeader('Allow','POST');return res.status(405).json({error:'Method not allowed.'})}const length=Number(req.headers['content-length']||0);if(length>MAX_BODY)return res.status(413).json({error:'Request is too large.'});try{const input=typeof req.body==='string'?JSON.parse(req.body):req.body||{};const service=new AiImageService();const result=mode==='edit'?await service.editImage(input):await service.createImage(input);return res.status(200).json(result)}catch(error){if(error instanceof SyntaxError)return res.status(400).json({error:'Invalid JSON payload.'});return res.status(error.status||500).json({error:error.status?error.message:'Image generation failed unexpectedly.'})}}
}
module.exports={imageHandler}
