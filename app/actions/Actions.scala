package actions

import javax.inject.Inject
import play.api.mvc.{ActionBuilderImpl, BodyParsers}

import scala.concurrent.ExecutionContext

class Actions @Inject()(actionFilters: ActionFilters, actionTransformers: ActionTransformers, parser: BodyParsers.Default) {

  def UserAction(implicit ec: ExecutionContext) =
    new ActionBuilderImpl(parser).
      andThen(actionFilters.PrivateAction).
      andThen(actionTransformers.UserTransformer)

  def AnonymousAction(implicit ec: ExecutionContext) =
    new ActionBuilderImpl(parser).
      andThen(actionFilters.PublicAction)

  def BookAction(bookId: Long)(implicit ec: ExecutionContext) =
    UserAction.
      andThen(actionTransformers.BookTransformer(bookId)).
      andThen(actionFilters.BookActionFilter)
}
