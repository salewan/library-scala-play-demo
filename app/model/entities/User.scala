package model.entities

import slick.jdbc.MySQLProfile.api._

case class User(id: Long, login: String, password: String)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def login = column[String]("login")
  def password = column[String]("password")

  def * = (id, login, password) <> (User.tupled, User.unapply)
}
