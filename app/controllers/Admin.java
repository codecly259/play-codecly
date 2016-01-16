package controllers;

import java.util.List;

import models.Post;
import models.Tag;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends Controller {
	
	@Before
	static void setConnectedUser() {
		if(Security.isConnected()){
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user.fullname);
		}
	}
	
	public static void index() {
		String user = Security.connected();
		List<Post> posts = Post.find("author.email", user).fetch();
		render(posts);
	}
	
	/**
	 * 转到新建或者编写页面
	 * @param id 当id为空到新建页面，当id不为空到编辑页面
	 */
	public static void form(Long id) {
		if(id != null){
			Post post = Post.findById(id);
			render(post);
		}
		render();
	}
	
	/**
	 * 新建文章或更新文章, 当id为空时新建文章, 当id不为空时新建文章
	 * @param id 文章id
	 * @param title 文章标题
	 * @param content 文章内容
	 * @param tags 文章标签
	 */
	public static void save(Long id, String title, String content, String tags) {
		Post post;
		if(id == null){
			//创建一个新的文章(post)
			User author = User.find("byEmail", Security.connected()).first();
			post = new Post(title, content, author);
		}else{
			// 查询文章
			post = Post.findById(id);
			// 更新文章
			post.title = title;
			post.content = content;
			post.tags.clear();
		}
		
		//设置文章的标签
		for(String tag : tags.split("\\s+")){
			if(tag.trim().length() > 0){
				post.tagItWith(tag);
			}
		}
		// 验证文章
		validation.valid(post);
		if(validation.hasErrors()){
			render("@form",post);
		}
		//保存文章
		post.save();
		index();
	}
	
	static void onDisconnected() {
		Application.index();
	}
	
	static void onAuthenticated() {
		Admin.index();
	}
}
