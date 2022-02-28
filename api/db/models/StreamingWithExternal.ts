import {DataTypes, Model, Optional} from "sequelize";
import {ExternalSource, Streaming} from ".";
import sequelizeConnection from "../config";

interface StreamingWithExternalAttributes {
  id: number;
  streamingId: number;
  externalSourceId: number;

  createdAt?: Date;
  updatedAt?: Date;
  deletedAt?: Date;

  streaming?: Streaming;
  externalSource?: ExternalSource;
}

export interface StreamingWithExternalInput extends Optional<StreamingWithExternalAttributes, "id"> {
}

export interface StreamingWithExternalOutput extends Required<StreamingWithExternalAttributes> {
}

class StreamingWithExternal extends Model<StreamingWithExternalAttributes, StreamingWithExternalInput> implements StreamingWithExternalAttributes {
  declare id: number;
  declare streamingId: number;
  declare externalSourceId: number;

  // timestamps!
  declare readonly createdAt: Date;
  declare readonly updatedAt: Date;
  declare readonly deletedAt: Date;

  declare readonly streaming: Streaming;
  declare readonly externalSource: ExternalSource;
}

StreamingWithExternal.init(
    {
      id: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
      },
      streamingId: {
        type: DataTypes.INTEGER,
        references: {model: Streaming},
        allowNull: false,
        unique: {name: "stid-exid", msg: "avion"},
      },
      externalSourceId: {
        type: DataTypes.INTEGER,
        references: {model: ExternalSource},
        allowNull: false,
        unique: {name: "stid-exid", msg: "avion"},
      },
    },
    {timestamps: true, sequelize: sequelizeConnection}
);

// @Table
// class StreamingWithExternal extends Model<StreamingWithExternalAttributes, StreamingWithExternalInput> implements StreamingWithExternalAttributes {
//   @Unique
//   @PrimaryKey
//   @AutoIncrement
//   @Column(DataType.INTEGER)
//   declare id: number;

//   @ForeignKey(() => Streaming)
//   @Column(DataType.INTEGER)
//   declare streamingId: number;

//   @ForeignKey(() => ExternalSource)
//   @Column(DataType.INTEGER)
//   declare externalSourceId: number;
// }

export default StreamingWithExternal;
