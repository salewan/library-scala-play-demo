package model.entities

import model.Tables
import slick.jdbc.MySQLProfile.api._

case class Book(id: Long, userId: Long, isn: String, name: String)

class Books(tag: Tag) extends Table[Book](tag, "books") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id")
  def isn = column[String]("isn")
  def name = column[String]("name")

  def * = (id, userId, isn, name) <> (Book.tupled, Book.unapply)

  def fkUser = foreignKey("fk_user_id", userId, Tables.users)(_.id, onDelete = ForeignKeyAction.Cascade)
}
