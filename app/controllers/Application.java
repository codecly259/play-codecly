package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {
	
	/**
	 * 运行Action前设置默认配置
	 */
	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
	}

	/**
	 * home主页action
	 */
	public static void index() {
		Post frontPost = Post.find("order by postedAt desc").first();
		List<Post> olderPosts = Post.find("order by postedAt desc").from(1).fetch(10);
		render(frontPost, olderPosts);
	}
	
	/**
	 * 显示文章action
	 * @param id 文章(post)标志id
	 */
	public static void show(Long id) {
		Post post = Post.findById(id);
		String randomID = Codec.UUID();
		render(post, randomID);
	}
	
	/**
	 * 发表评论action
	 * @param postId 文章标志ID
	 * @param author 评论人
	 * @param content 评论内容
	 * @param code 验证码
	 * @param randomID 验证码的唯一ID
	 */
	public static void postComment(Long postId, 
			@Required(message = "请输入你的姓名") String author, 
			@Required(message = "请输入评论内容") String content,
			@Required(message = "请输入验证码") String code, String randomID) {
		Post post = Post.findById(postId);
		System.out.println("填写的验证码："+code);
		System.out.println("取出randomID:"+randomID);
		System.out.println();
		validation.equals(code, Cache.get(randomID)).message("验证码不正确，请重试");
		if (validation.hasErrors()) {
			render("Application/show.html", post, randomID);
		}
		post.addComment(author, content);
		flash.success("评论成功。感谢 %s 的评论", author);
		Cache.delete(randomID);
		show(postId);
	}
	
	/**
	 * 获取图片验证码
	 * @param id 验证码唯一ID
	 */
	public static void captcha(String id) {
		Images.Captcha captcha = Images.captcha();
		String code = captcha.getText("#E4EAFD");
		System.out.println("设置randomID:"+id);
		Cache.set(id, code, "10mn");
		System.out.println("验证码："+code);
		renderBinary(captcha);
	}
	
	public static void listTagged(String tag) {
		List<Post> posts = Post.findTaggedWith(tag);
		render(tag, posts);
	}

}