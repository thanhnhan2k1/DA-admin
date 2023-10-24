package com.example.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Blog {
	private int id;
	private String title;
	private String content;
	private String author;
	private Date dateUpdate;
	private String image;
	private String precontent;
	private CategoryBlog categoryBlog;
	private String status;
}
