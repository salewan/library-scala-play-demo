package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import model.Tables
import model.entities.User
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

class UserDAO @Inject() (val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def all(): Future[Seq[User]] = db.run(Tables.users.result)

  def insert(user: User): Future[Long] = db.run((Tables.users returning Tables.users.map(_.id)) += user)

  def findByLogin(login: String): Future[Option[User]] = db.run(Tables.users.filter(_.login === login).result.headOption)

  def findById(id: Long): Future[Option[User]] = db.run(Tables.users.filter(_.id === id).result.headOption)
}