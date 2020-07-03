package controllers

import actions.{Actions, BookRequest, UserRequest}
import dao.BookDAO
import javax.inject._
import model.entities.Book
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(actions: Actions, bookDAO: BookDAO, cc: ControllerComponents)
                              (implicit executionContext: ExecutionContext) extends AbstractController(cc)
  with play.api.i18n.I18nSupport {

  val UserAction: ActionBuilder[UserRequest, AnyContent] = actions.UserAction
  def BookAction(bookId: Long): ActionBuilder[BookRequest, AnyContent] = actions.BookAction(bookId)

  def bookForm = Form(
    tuple(
      "isn" -> text.verifying(require("book.error.ISNRequired")),
      "name" -> text.verifying(require("login.error.NameRequired"))
    )
  )

  def require(errorKey: String): Constraint[String] = Constraint("") {
    pass => if (pass.nonEmpty) Valid else Invalid(errorKey)
  }

  def index = UserAction async { implicit request =>
    bookDAO.getMyBooks(request.user.id).map(books => Ok(views.html.index(books)))
  }

  def addBook = UserAction { implicit request =>
    Ok(views.html.bookForm(bookForm))
  }

  def addBookPostNew = UserAction async { implicit request =>
    bookForm.bindFromRequest().fold(
      formWithErrors => {
        Future successful Ok(views.html.bookForm(formWithErrors))
      },
      data => {
        val userId = request.maybeUser.get.id
        bookDAO.insert(Book(0, userId, data._1, data._2)).map(_ => Redirect(routes.HomeController.index()))
      }
    )
  }

  def bookPage(bookId: Long) = BookAction(bookId) { implicit request =>
    Ok(
      views.html.bookForm(
        bookForm.fill((request.book.isn, request.book.name)), bookId))
  }

  def editBookPost(bookId: Long) = BookAction(bookId) async { implicit request =>
    bookForm.bindFromRequest().fold(
      formWithErrors => {
        Future successful Ok(views.html.bookForm(formWithErrors))
      },
      data => {
        bookDAO.update(request.book.copy(isn = data._1, name = data._2)).
          map(_ => Redirect(routes.HomeController.index()))
      }
    )
  }

  def deleteBook(bookId: Long) = BookAction(bookId) async { implicit request =>
    bookDAO.delete(bookId).map(_ => Redirect(routes.HomeController.index()))
  }

  def logout = UserAction {
    Redirect(routes.AuthController.signIn()).withNewSession
  }
}
