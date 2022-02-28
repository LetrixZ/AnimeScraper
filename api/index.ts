import cors from 'cors'
import express, {Request, Response} from "express";
import dbInit from "./db/init";
import {insertRoutes, streamingRouter} from "./routes";

await dbInit()

const app = express()

app.use(cors())
app.use(express.json({limit: '50mb'}))

app.use("/streaming", streamingRouter)
app.use(insertRoutes)

app.get("/", async (req: Request, res: Response): Promise<Response> => {
  return res.json({message: "OK"});
});


app.listen(4000, () => {
  console.log(`Server running on http://localhost:4000`);
});