package controllers;

import models.User;

public class Security extends Secure.Security {
	
	static boolean authenticate(String name, String password) {
		return User.connect(name, password) != null;
	}
	
}
