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
    
    @Test
    public void useTheCommentRelation(){
    	// 创建并保存用户
    	User bob = new User("bob@gmail.com", "secret", "Bob").save();
    	// 创建并保存文章
    	Post bobPost = new Post("my first post", "hello world", bob).save();
    	// 给文章添加评论
    	bobPost.addComment("Jeff", "Nice post");
    	bobPost.addComment("Tom", "I knew that!");
    	
    	// 计数
    	assertEquals(1, User.count());
    	assertEquals(1, Post.count());
    	assertEquals(2, Comment.count());
    	
    	// 查询 Bob的文章
    	bobPost = Post.find("byAuthor", bob).first();
    	assertNotNull(bobPost);
    	
    	// 测试文章的评论
    	assertEquals(2, bobPost.comments.size());
    	assertEquals("Jeff", bobPost.comments.get(0).author);
    	
    	// 删除文章,测试文章下的评论是否删除
    	bobPost.delete();
    	assertEquals(1, User.count());
    	assertEquals(0, Post.count());
    	assertEquals(0, Comment.count());
    }
    
    @Test
    public void fullTestUseDatatestyml(){
    	Fixtures.loadModels("data-test.yml");
    	
    	// 测试相关数据个数
    	assertEquals(2, User.count());
    	assertEquals(3, Post.count());
    	assertEquals(3, Comment.count());
    	
    	// 尝试使用用户登录
    	assertNotNull(User.connect("bob@gmail.com", "secret"));
    	assertNotNull(User.connect("jeff@gmail.com", "secret"));
    	assertNull(User.connect("jeff@gmail.com", "bad password"));
    	assertNull(User.connect("tom@gmail.com","secret"));
    	
    	// 查询Bob的所有文章
    	List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
    	assertEquals(2, bobPosts.size());
    	
    	// 查询bob文章的所有评论
    	List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
    	assertEquals(3, bobComments.size());
    	
    	// 查询最新的文章
    	Post frontPost = Post.find("order by postedAt desc").first();
    	assertNotNull(frontPost);
    	assertEquals("About the model layer", frontPost.title);
    	// 检查这篇文章的评论个数
    	assertEquals(2, frontPost.comments.size());
    	
    	// 给最新的文章添加新的评论
    	frontPost.addComment("Jim", "Hello guys");
    	assertEquals(3, frontPost.comments.size());
    	assertEquals(4, Comment.count());
    	
    }
    
    @Test
    public void testTags() {
    	// 创建并保存用户
    	User savag = new User("demo@oopsplay.org", "123", "savag").save();
    	// 发表文章
    	Post savagPost = new Post("My First post", "Hello Play", savag).save();
    	Post anotherSavagPost = new Post("My second post", "Hello Play2", savag).save();
    	
    	// 测试
    	assertEquals(0, Post.findTaggedWith("Red").size());
    	
    	// 为文章添加标签
    	savagPost.tagItWith("Red").tagItWith("Blue").save();
    	anotherSavagPost.tagItWith("Red").tagItWith("Green").save();
    	
    	// 测试
    	assertEquals(2, Post.findTaggedWith("Red").size());
    	assertEquals(1, Post.findTaggedWith("Green").size());
    	assertEquals(1, Post.findTaggedWith("Blue").size());
    }

}
