import {DataTypes, Model, Optional} from "sequelize";
import {ExternalSource, Streaming} from ".";
import {Season, Status, Type} from "../../enums";
import sequelizeConnection from "../config";

interface AnimeAttributes {
  id: number;
  title: string;
  slug: string;
  synonyms?: string[];
  type?: Type;
  status?: Status;
  season?: Season;
  year?: number;
  picture?: string;

  createdAt?: Date;
  updatedAt?: Date;
  deletedAt?: Date;

  externalSources?: ExternalSource[];
  streamings?: Streaming[];
}

export interface AnimeInput extends Optional<AnimeAttributes, "id"> {
}

export interface AnimeOutput extends AnimeAttributes {
}

class Anime extends Model<AnimeAttributes, AnimeInput> implements AnimeAttributes {
  declare id: number;
  declare title: string;
  declare slug: string;
  declare synonyms: string[];
  declare type: Type;
  declare status: Status;
  declare season: Season;
  declare year: number;
  declare picture: string;

  // timestamps!
  declare readonly createdAt: Date;
  declare readonly updatedAt: Date;
  declare readonly deletedAt: Date;

  declare readonly externalSources: ExternalSource[];
  declare readonly streamings: Streaming[];
}

Anime.init(
    {
      id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
      },
      title: {
        type: DataTypes.STRING,
        allowNull: false,
      },
      slug: {
        type: DataTypes.STRING(500),
        allowNull: false,
        unique: true,
      },
      synonyms: {
        type: DataTypes.ARRAY({type: DataTypes.STRING}),
      },
      type: {
        type: DataTypes.STRING,
        defaultValue: Type.UNKNOWN,
      },
      status: {
        type: DataTypes.STRING,
        defaultValue: Status.UNKNOWN,
      },
      season: {
        type: DataTypes.STRING,
        defaultValue: Season.UNKNOWN,
      },
      year: {
        type: DataTypes.INTEGER,
        defaultValue: 0,
      },
      picture: {
        type: DataTypes.TEXT,
      },
    },
    {
      timestamps: true,
      sequelize: sequelizeConnection,
      scopes: {
        _streamings: {
          include: ["streamings"],
        },
        _externalSources: {
          include: ["externalSources"],
        },
      },
    }
)

export default Anime;
