package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import model.Tables
import model.entities.Book
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

class BookDAO @Inject() (val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def all(): Future[Seq[Book]] = db.run(Tables.books.result)

  def insert(book: Book): Future[Unit] = db.run(Tables.books += book).map { _ => () }

  def findByIdAndAuthor(bookId: Long, authorId: Long): Future[Option[Book]] =
    db.run(Tables.books.filter(b => b.userId === authorId && b.id === bookId).result.headOption)

  def update(book: Book): Future[Int] = {
    val q = for { b <- Tables.books if b.id === book.id } yield b
    db.run(q.update(book))
  }

  def delete(bookId: Long): Future[Int] = db.run(Tables.books.filter(b => b.id === bookId).delete)

  def getMyBooks(userId: Long): Future[Seq[Book]] = db.run(Tables.books.filter(b => b.userId === userId).result)
}