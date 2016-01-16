package controllers;

import play.mvc.With;

@Check("admin")
@With(Secure.class)
class Posts extends CRUD{

}
