import {Request, Response, Router} from "express"
import {Anime, ExternalSource, Streaming, StreamingWithExternal} from "../db/models";

const router = Router()

router.post("/anime", async (req: Request, res: Response): Promise<Response> => {
  try {
    if (Array.isArray(req.body)) {
      await Anime.bulkCreate(req.body, {updateOnDuplicate: ['picture', 'season', 'year', 'status', 'type']})
    } else {
      await Anime.upsert(req.body)
    }
    return res.json({message: "Anime inserted correctly"})
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message, message: 'Anime insertion failed'});
  }
});

router.post("/streaming", async (req: Request, res: Response): Promise<Response> => {
  try {
    if (Array.isArray(req.body)) {
      await Streaming.bulkCreate(req.body, {updateOnDuplicate: ['picture', 'status', 'episodes']})
    } else {
      await Streaming.upsert(req.body)
    }
    return res.json({message: "Streaming items inserted correctly"})
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message, message: 'Streaming insertion failed'});
  }
});

router.post("/streaming-anime", async (req: Request, res: Response): Promise<Response> => {
  try {
    if (Array.isArray(req.body)) {
      for (let it of req.body) {
        const streaming = await Streaming.findOne({
          where: {slug: it.slug, source: it.source},
          include: [{model: ExternalSource, as: 'externalSources', include: ["anime"]}]
        })
        if (streaming) {
          const externalSource = streaming.externalSources?.[0]
          if (externalSource) {
            streaming.animeId = externalSource.animeId
            await streaming.save()
          }
        }
      }
    } else {
      const streaming = await Streaming.findOne({
        where: {slug: req.body.slug, source: req.body.source},
        include: [{model: ExternalSource, as: 'externalSources', include: ["anime"]}]
      })
      if (streaming) {
        const externalSource = streaming.externalSources?.[0]
        if (externalSource) {
          streaming.animeId = externalSource.animeId
          await streaming.save()
        }
      }
    }
    return res.json({message: "StreamingAnime insertion completed"})
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message, message: 'StreamingAnime insertion failed'});
  }
});

router.post("/streaming-external", async (req: Request, res: Response): Promise<Response> => {
  try {
    if (Array.isArray(req.body)) {
      const streamingExternal = []
      for (let it of req.body) {
        const streaming = await Streaming.findOne({where: {slug: it.slug, source: it.source}})
        if (streaming) {
          for (let source of it.externalSources) {
            const externalSource = await ExternalSource.findOne({where: {slug: source.slug, source: source.source}})
            if (externalSource) {
              streamingExternal.push({
                streamingId: streaming.id,
                externalSourceId: externalSource.id
              })
            }
          }
        }
      }
      await StreamingWithExternal.bulkCreate(streamingExternal, {ignoreDuplicates: true})
    } else {
      const streamingExternal = []
      const streaming = await Streaming.findOne({where: {slug: req.body.slug, source: req.body.source}})
      if (streaming) {
        for (let source of req.body.externalSources) {
          const externalSource = await ExternalSource.findOne({where: {slug: source.slug, source: source.source}})
          if (externalSource) {
            streamingExternal.push({
              streamingId: streaming.id,
              externalSourceId: externalSource.id
            })
          }
        }
      }
      await StreamingWithExternal.bulkCreate(streamingExternal, {ignoreDuplicates: true})
    }
    return res.json({message: "StreamingExternal insertion completed"})
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message, message: "StreamingExternal insertion failed"});
  }
});

router.post("/external", async (req: Request, res: Response): Promise<Response> => {
  try {
    if (Array.isArray(req.body)) {
      const sources = []
      for (let it of req.body) {
        const anime = await Anime.findOne({where: {slug: it.slug}})
        if (anime) {
          it.externalSources.forEach(source => {
            sources.push({
              animeId: anime.id,
              slug: source.slug,
              source: source.source
            })
          })
        }
      }
      await ExternalSource.bulkCreate(sources, {ignoreDuplicates: true})
    } else {
      const sources = []
      const anime = await Anime.findOne({where: {slug: req.body.slug}})
      if (anime) {
        req.body.externalSources.forEach(source => {
          sources.push({
            animeId: anime.id,
            slug: source.slug,
            source: source.source
          })
        })
      }
      await ExternalSource.bulkCreate(sources, {ignoreDuplicates: true})
    }
    return res.json({message: "ExternalSource insertion completed"})
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message, message: "ExternalSource insertion failed"});
  }
});

export default router
