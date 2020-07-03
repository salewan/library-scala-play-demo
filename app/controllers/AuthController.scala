package controllers

import actions.{ActionFilters, Actions}
import dao.UserDAO
import javax.inject.{Inject, Singleton}
import model.entities.User
import play.api.mvc.{AbstractController, ActionBuilder, AnyContent, ControllerComponents, Request, Session}
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject() (userDAO: UserDAO, actions: Actions, cc: ControllerComponents)
                               (implicit executionContext: ExecutionContext)
  extends AbstractController(cc)
  with play.api.i18n.I18nSupport {

  val AnonymousAction: ActionBuilder[Request, AnyContent] = actions.AnonymousAction

  def signInForm = Form (
    tuple(
      "login" -> text.verifying(require("login.error.LoginRequired")),
      "password" -> text.verifying(require("login.error.PasswordRequired"))
    )
  )

  def require(errorKey: String): Constraint[String] = Constraint("") {
    pass => if (pass.nonEmpty) Valid else Invalid(errorKey)
  }

  def signIn = AnonymousAction { implicit request =>
    Ok(views.html.signIn(signInForm))
  }

  def signInPost = AnonymousAction async { implicit request =>
    signInForm.bindFromRequest().fold(formWithErrors => {
      Future successful Ok(views.html.signIn(formWithErrors))
    }, data => {
      userDAO.findByLogin(data._1).flatMap {
        case Some(user) =>
          if (user.password.equals(data._2)) {
            Future successful Redirect(routes.HomeController.index()).
              withSession(Session(Map(config.COOKIE_ID_NAME -> user.id.toString)))
          } else {
            Future successful Ok(views.html.signIn(signInForm.fill((data._1, "")).
              withGlobalError("login.error.WrongPassword")))
          }
        case None =>
          userDAO.insert(User(0, data._1, data._2)).map(id =>
            Redirect(routes.HomeController.index()).
              withSession(Session(Map(config.COOKIE_ID_NAME -> id.toString))))
      }
    })
  }

}
