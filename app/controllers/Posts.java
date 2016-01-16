package controllers;

import play.mvc.With;

@With(Secure.class)
class Posts extends CRUD{

}
