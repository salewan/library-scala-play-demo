package actions

import model.entities.User
import play.api.mvc.{Request, WrappedRequest}

class UserRequest[A](val maybeUser: Option[User], request: Request[A]) extends WrappedRequest[A](request) {

  def user: User = maybeUser.get
}
