package com.nikhil.app.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Document(	collection = "users")
public class User {
	@Transient
    public static final String SEQUENCE_NAME = "users_sequence";
	
	@Id
	private Long id;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;
	
	@NotBlank
	private long ticket;
	
	@NotBlank
	private long score;
	
	@NotBlank
	private long time;

	public User() {
	}

	public User(String username, String email, String password,long time) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.ticket=20;
		this.score=0;
		this.time=time;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getTicket() {
		return ticket;
	}

	public void setTicket(long ticket) {
		this.ticket = ticket;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}	
	
	
}
