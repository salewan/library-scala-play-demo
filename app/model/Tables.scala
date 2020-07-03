package model

import model.entities.{Books, Users}
import slick.jdbc.MySQLProfile.api._

object Tables {
  val users = TableQuery[Users]
  val books = TableQuery[Books]
}
