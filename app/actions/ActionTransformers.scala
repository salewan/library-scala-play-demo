package actions

import dao.BookDAO
import javax.inject.Inject
import model.entities.User
import play.api.cache.AsyncCacheApi
import play.api.mvc.{ActionTransformer, Request}

import scala.concurrent.{ExecutionContext, Future}

class ActionTransformers @Inject()(cache: AsyncCacheApi, bookDAO: BookDAO) {

  def UserTransformer(implicit ec: ExecutionContext) = new ActionTransformer[Request, UserRequest] {
    override protected def transform[A](request: Request[A]): Future[UserRequest[A]] = {
      val idOpt = request.session.get(config.COOKIE_ID_NAME)
      val futureMaybeUser = if (idOpt.isDefined) cache.get[User](idOpt.get) else Future.successful(None)
      futureMaybeUser.map(maybeUser => new UserRequest[A](maybeUser, request))
    }

    override protected def executionContext: ExecutionContext = ec
  }

  def BookTransformer(bookId: Long)(implicit ec: ExecutionContext) = new ActionTransformer[UserRequest, BookRequest] {
    override protected def transform[A](request: UserRequest[A]): Future[BookRequest[A]] = {
      bookDAO.findByIdAndAuthor(bookId, request.user.id).map(op => new BookRequest[A](op, request))
    }

    override protected def executionContext: ExecutionContext = ec
  }
}
