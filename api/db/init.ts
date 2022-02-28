import {Anime, ExternalSource, Streaming, StreamingWithExternal} from "./models";
import sequelizeConnection from "./config";

const dbInit = async () => {
  defineAssociations();
  await sequelizeConnection.sync();
  await Anime.sync({alter: true})
  await Streaming.sync({alter: true})
  await ExternalSource.sync({alter: true})
  await StreamingWithExternal.sync({alter: true})

};

const defineAssociations = () => {
  Anime.hasMany(ExternalSource, {onDelete: "CASCADE", foreignKey: "animeId", as: "externalSources"});
  ExternalSource.belongsTo(Anime, {foreignKey: "animeId", as: "anime"});
  Anime.hasMany(Streaming, {onDelete: "CASCADE", foreignKey: "animeId", as: "streamings"});
  Streaming.belongsTo(Anime, {foreignKey: "animeId", as: "anime"});

  Streaming.belongsToMany(ExternalSource, {
    through: StreamingWithExternal,
    foreignKey: "streamingId",
    as: "externalSources"
  });
  ExternalSource.belongsToMany(Streaming, {
    through: StreamingWithExternal,
    foreignKey: "externalSourceId",
    as: "streamings"
  });
  Streaming.hasMany(StreamingWithExternal, {foreignKey: "streamingId"});
  StreamingWithExternal.belongsTo(Streaming, {foreignKey: "streamingId", as: "streaming"});
  ExternalSource.hasMany(StreamingWithExternal, {foreignKey: "externalSourceId"});
  StreamingWithExternal.belongsTo(ExternalSource, {foreignKey: "externalSourceId", as: "externalSource"});
};

export default dbInit;
