import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

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

}
