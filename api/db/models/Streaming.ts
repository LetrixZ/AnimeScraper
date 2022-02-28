import {DataTypes, Model, Op, Optional} from "sequelize";
import {Anime, ExternalSource} from ".";
import {Status, StreamingSource} from "../../enums";
import sequelizeConnection from "../config";

interface StreamingAttributes {
  id: number;
  title: string;
  slug: string;
  source: StreamingSource;
  episodes?: number;
  status?: Status;
  picture?: string;
  animeId?: number;

  createdAt?: Date;
  updatedAt?: Date;
  deletedAt?: Date;

  externalSources?: ExternalSource[];
  anime?: Anime;
}

export interface StreamingInput extends Optional<StreamingAttributes, "id"> {
}

export interface StreamingOutput extends StreamingAttributes {
}

class Streaming extends Model<StreamingAttributes, StreamingInput> implements StreamingAttributes {
  declare id: number;
  declare title: string;
  declare slug: string;
  declare source: StreamingSource;
  declare episodes: number;
  declare status: Status;
  declare picture: string;
  declare animeId: number;

  // timestamps!
  declare readonly createdAt: Date;
  declare readonly updatedAt: Date;
  declare readonly deletedAt: Date;

  declare readonly externalSources: ExternalSource[];
  declare readonly anime: Anime;
}

Streaming.init(
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
        unique: "slug-source",
      },
      source: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: "slug-source",
      },
      episodes: {
        type: DataTypes.INTEGER,
        defaultValue: 0
      },
      status: {
        type: DataTypes.STRING,
        defaultValue: Status.UNKNOWN,
      },
      picture: {
        type: DataTypes.TEXT,
      },
      animeId: {
        type: DataTypes.INTEGER,
        references: {model: Anime},
      },
    },
    {
      timestamps: true,
      sequelize: sequelizeConnection,
      scopes: {
        _anime: {
          include: ["anime"],
        },
        _externalSources: {
          include: ["externalSources"],
        },
        _unmatched: {
          include: [{model: ExternalSource, required: false, as: "externalSources"}],
          where: {
            $externalSources$: {[Op.eq]: null},
          },
        },
      },
    }
);

// @Table
// class Streaming extends Model<StreamingAttributes, StreamingInput> implements StreamingAttributes {
//   @Unique
//   @PrimaryKey
//   @AutoIncrement
//   @Column(DataType.INTEGER)
//   declare id: number;

//   @AllowNull(false)
//   @Column(DataType.STRING)
//   declare title: string;

//   @Unique("slug-source")
//   @AllowNull(false)
//   @Column(DataType.STRING)
//   declare slug: string;

//   @Unique("slug-source")
//   @AllowNull(false)
//   @Column(DataType.STRING)
//   declare source: StreamingSource;

//   @Column(DataType.INTEGER)
//   declare episodes: number;

//   @Column(DataType.STRING)
//   declare status: Status;

//   @Column(DataType.TEXT)
//   declare picture: string;

//   @BelongsToMany(() => ExternalSource, () => StreamingWithExternal)
//   declare externalSources: ExternalSource[];

//   @ForeignKey(() => Anime)
//   @Column(DataType.INTEGER)
//   declare animeId: number;

//   @BelongsTo(() => Anime)
//   declare anime: Anime;
// }

export default Streaming;
