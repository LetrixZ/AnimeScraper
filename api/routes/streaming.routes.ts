import {Request, Response, Router} from "express"
import {Status} from "../enums";
import {Streaming} from "../db/models";

const streamingRouter = Router()

const castToStatus = (status?: string) => {
  switch (status) {
    case "ONGOING":
      return Status.ONGOING;
    case "FINISHED":
      return Status.FINISHED;
    case "UPCOMING":
      return Status.UPCOMING;
    case "UNKNOWN":
      return Status.UNKNOWN;
    default:
      throw new Error("Invalid status");
  }
};

streamingRouter.get("/", async (req: Request, res: Response) => {
  try {
    return res.json({message: "OK"});
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message});
  }
});

streamingRouter.post("/check", async (req: Request, res: Response) => {
  try {
    if (Array.isArray(req.body)) {
      const missing = []
      for (let it of req.body) {
        const streaming = await Streaming.findOne({where: {slug: it.slug, source: it.source}})
        if (!streaming) {
          missing.push(it)
        }
      }
      return res.json(missing)
    } else {
      const missing = []
      const streaming = await Streaming.findOne({where: {slug: req.body.slug, source: req.body.source}})
      if (!streaming) {
        missing.push(req.body)
      }
      return res.json(missing)
    }
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message});
  }
});

streamingRouter.get("/status/:status", async (req: Request, res: Response) => {
  try {
    let {status: statusParam} = req.params;
    const status: Status = castToStatus(statusParam.toUpperCase());
    if (!status) {
      return res.status(400).json({error: "Valid status: ONGOING | FINISHED | UPCOMING | UNKNOWN"});
    }
    const data = await Streaming.findAll({where: {status}})
    return res.json(data);
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message});
  }
});

streamingRouter.put("/", async (req: Request, res: Response) => {
  try {
    if (Array.isArray(req.body)) {
      const entries = req.body.length
      let updated = 0
      for (let it of req.body) {
        const {slug, source} = it
        const streaming = await Streaming.findOne({where: {slug, source}})
        if (streaming) {
          const {episodes, status} = it
          if (episodes != streaming.episodes || status != streaming.status) updated++
          streaming.episodes = episodes
          streaming.status = status
          await streaming.save()
        }
      }
      return res.json({message: `${updated}/${entries} entries updated`});
    } else {
      const {slug, source} = req.body
      const streaming = await Streaming.findOne({where: {slug, source}})
      if (streaming) {
        const {episodes, status} = req.body
        streaming.episodes = episodes
        streaming.status = status
        await streaming.save()
        return res.json({streaming});
      }
    }
    return res.json({message: "Invalid body"});
  } catch (e) {
    console.log(e);
    return res.status(500).json({error: e.message});
  }
});

export default streamingRouter