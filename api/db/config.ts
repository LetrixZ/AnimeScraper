import {Sequelize} from "sequelize";

const sequelizeConnection = new Sequelize("scraper-kotlin", "postgres", "1234", {
  host: "localhost",
  dialect: "postgres",
  logging: false,
});

export default sequelizeConnection;
