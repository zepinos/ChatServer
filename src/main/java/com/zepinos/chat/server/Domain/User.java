package com.zepinos.chat.server.Domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
public class User implements Serializable {

	@Id
	@Column(length = 20)
	private String userId;
	@Column(nullable = false, length = 30)
	private String userName;
	@Column(nullable = false, length = 32)
	private String password;

}
