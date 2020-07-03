package model

import javax.inject.Inject
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import model.Tables.{books, users}
import slick.jdbc.MySQLProfile.api._

class CreateDDL @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  def createDDLScript() = {
    val setup = (books.schema ++ users.schema).create
    db.run(setup)
  }

  createDDLScript()
}
