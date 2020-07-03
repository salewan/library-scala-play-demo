package actions

import controllers.routes
import dao.{BookDAO, UserDAO}
import javax.inject.Inject
import model.entities.User
import play.api.cache.AsyncCacheApi
import play.api.mvc.Results.{Redirect, NotFound}
import play.api.mvc.{ActionFilter, Request, Result}

import scala.concurrent.{ExecutionContext, Future}

class ActionFilters @Inject()(userDAO: UserDAO, bookDAO: BookDAO, cache: AsyncCacheApi) {

  def findUser(id: String)(implicit ec: ExecutionContext): Future[Option[User]] = {
    val maybeUser: Option[User] = cache.sync.get(id);
    if (maybeUser.isDefined) Future.successful(maybeUser)
    else {
      val futureUser: Future[Option[User]] = userDAO.findById(id.toLong)
      futureUser.foreach {
        case Some(user) => cache.set(id, user)
        case None =>
      }
      futureUser
    }
  }

  def PrivateAction(implicit ec: ExecutionContext) = new ActionFilter[Request] {
    def executionContext = ec

    def filter[A](request: Request[A]) = {
      val cookieId = request.session.get(config.COOKIE_ID_NAME)
      if (cookieId.isEmpty) Future successful Some(Redirect(routes.AuthController.signIn()))
      else {
        findUser(cookieId.get).map {
          case Some(user) => None
          case None => Some(Redirect(routes.AuthController.signIn()).withNewSession)
        }
      }
    }
  }

  def PublicAction(implicit ec: ExecutionContext) = new ActionFilter[Request] {
    def executionContext = ec

    def filter[A](request: Request[A]) = {
      val cookieId = request.session.get(config.COOKIE_ID_NAME)
      if (cookieId.isEmpty) Future successful None
      else {
        findUser(cookieId.get).map {
          case Some(_) => Some(Redirect(routes.HomeController.index()))
          case None => None
        }
      }
    }
  }

  def BookActionFilter(implicit ec: ExecutionContext) = new ActionFilter[BookRequest] {
    def executionContext = ec

    override protected def filter[A](request: BookRequest[A]): Future[Option[Result]] = Future successful {
      if (request.maybeBook.isDefined) None
      else Some(NotFound)
    }
  }
}

