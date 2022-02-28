import {DataTypes, Model, Optional} from "sequelize";
import {Anime, Streaming} from ".";
import {ExternalSourceType} from "../../enums";
import sequelizeConnection from "../config";

interface ExternalSourceAttributes {
  id: number;
  source: ExternalSourceType;
  slug: string;
  animeId: number;

  createdAt?: Date;
  updatedAt?: Date;
  deletedAt?: Date;

  anime?: Anime;
  streamings?: Streaming[];
}

export interface ExternalSourceInput extends Optional<ExternalSourceAttributes, "id"> {
}

export interface ExternalSourceOutput extends ExternalSourceAttributes {
}

class ExternalSource extends Model<ExternalSourceAttributes, ExternalSourceInput> implements ExternalSourceAttributes {
  declare id: number;
  declare source: ExternalSourceType;
  declare slug: string;
  declare animeId: number;

  // timestamps!
  declare readonly createdAt: Date;
  declare readonly updatedAt: Date;
  declare readonly deletedAt: Date;

  declare readonly anime: Anime;
  declare readonly streamings: Streaming[];
}

ExternalSource.init(
    {
      id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
      },
      source: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: {name: "source-slug", msg: "SourceSlug"},
      },
      slug: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: {name: "source-slug", msg: "SourceSlug"},
      },
      animeId: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {model: Anime},
      },
    },
    {timestamps: true, sequelize: sequelizeConnection}
);

// @Table
// class ExternalSource extends Model<ExternalSourceAttributes, ExternalSourceInput> implements ExternalSourceAttributes {
//   @Unique
//   @PrimaryKey
//   @AutoIncrement
//   @Column(DataType.INTEGER)
//   declare id: number;

//   @AllowNull(false)
//   @Column(DataType.STRING)
//   declare source: string;

//   @AllowNull(false)
//   @Column(DataType.STRING)
//   declare slug: string;

//   @ForeignKey(() => Anime)
//   @Column(DataType.INTEGER)
//   declare animeId: number;

//   @BelongsTo(() => Anime)
//   declare anime: Anime;

//   @BelongsToMany(() => Streaming, () => StreamingWithExternal)
//   declare streamings: Streaming[];
// }

export default ExternalSource;
