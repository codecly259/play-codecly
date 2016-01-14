import java.util.List;

import org.junit.Before;
import org.junit.Test;

import models.Comment;
import models.Post;
import models.User;
import play.test.Fixtures;
import play.test.UnitTest;

public class BasicTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
    @Test
    public void createAndRetrieveUser() {
    	// 创建一个新的用户并保存
    	new User("bob@gmail.com", "secret", "Bob").save();
    	
    	// 通过email地址获取用户
    	User bob = User.find("byEmail", "bob@gmail.com").first();
    	
    	// 测试
    	assertNotNull(bob);
    	assertEquals("Bob", bob.fullname);
    }
    
    @Test
    public void tryConnectAsUser() {
    	// 创建一个新的用户并保存
    	new User("bob@gmail.com", "secret", "Bob").save();
    	
    	// 测试
    	assertNotNull(User.connect("bob@gmail.com", "secret"));
    	assertNull(User.connect("bob@gmail.com", "bad password"));
    	assertNull(User.connect("tom@gmail.com", "secret"));
    }
    
    @Test
    public void createPost(){
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	new Post("my first post", "hello world", bob).save();
    	
    	// 测试 文章 已经保存
    	assertEquals(1, Post.count());
    	
    	// 查询Bob创建的所有文章
    	List<Post> bobPosts = Post.find("byAuthor", bob).fetch();
    	
    	// 测试
    	assertEquals(1, bobPosts.size());
    	Post firstPost = bobPosts.get(0);
    	assertNotNull(firstPost);
    	assertEquals(bob, firstPost.author);
    	assertEquals("my first post", firstPost.title);
    	assertEquals("hello world", firstPost.content);
    	assertNotNull(firstPost.postedAt);
    }
    
    @Test
    public void postComments() {
    	// 创建并保存用户
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	// 创建并保存文章
    	Post bobPost = new Post("my first post", "hello world", bob).save();
    	
    	// 添加评论
    	new Comment(bobPost, "Jeff", "Nice post").save();
    	new Comment(bobPost, "Tom", "I knew that!").save();
    	
    	// 查询所有评论
    	List<Comment> bobPostComments = Comment.find("byPost", bobPost).fetch();
    	
    	// 测试
    	assertEquals(2, bobPostComments.size());
    	
    	Comment firstComment = bobPostComments.get(0);
    	assertNotNull(firstComment);
    	assertEquals("Jeff", firstComment.author);
    	assertEquals("Nice post", firstComment.content);
    	assertNotNull(firstComment.postedAt);
    	
    	Comment secondComment = bobPostComments.get(1);
    	assertNotNull(secondComment);
    	assertEquals("Tom", secondComment.author);
    	assertEquals("I knew that!", secondComment.content);
    	assertNotNull(secondComment.postedAt);
    	
    }

}
