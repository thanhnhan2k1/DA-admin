package com.example.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeographicalArea {
	private int id;
	private String vietnamese;
	private String english;
	private Date updateAt;
}
