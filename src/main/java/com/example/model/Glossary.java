package com.example.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Glossary {
	private int id;
	private String vietnamese;
	private String english;
	private String definition;
	private String image;
	private String note;
	private Date updateAt;
}
