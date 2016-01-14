package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Post extends Model {
	
	public String title;
	public Date postedAt;
	@Lob
	public String content;
	@ManyToOne
	public User author;
	/**
	 * <p>
	 * Note how we have used the mappedBy attribute to tell JPA that the Comment class’s post field maintains the relationship. 
	 * When you define a bi-directional relation with JPA it is very important to tell it which side will maintain the relationship. 
	 * In this case, since the Comments belong to the Post, it’s better that the Comment class maintains the relationship.
	 * </p>
	 * <p>
	 * We have set the cascade property to tell JPA that we want Post deletion be cascaded to comments. 
	 * This way, if you delete a post, all related comments will be deleted as well.
	 * </p>
	 */
	@OneToMany(mappedBy="post", cascade=CascadeType.ALL)
	public List<Comment> comments;
	
	public Post(String title, String content, User author) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.postedAt = new Date();
		this.comments = new ArrayList<Comment>();
	}
	
	public Post addComment(String author, String content){
		Comment newComment = new Comment(this, author, content).save();
		this.comments.add(newComment);
		this.save();
		return this;
	}
	
}
