package actions

import model.entities.Book

class BookRequest[A](val maybeBook: Option[Book], request: UserRequest[A]) extends UserRequest(request.maybeUser, request) {

  def book: Book = maybeBook.get
}