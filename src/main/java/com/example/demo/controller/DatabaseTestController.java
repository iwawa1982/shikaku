package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.repository.DatabaseTestMapper;

@RestController
public class DatabaseTestController {

	
	private final DatabaseTestMapper databaseTestMapper;
	
	public DatabaseTestController(DatabaseTestMapper databaseTestMapper) {
		this.databaseTestMapper =databaseTestMapper;
		
	}
	@GetMapping("/db-test")
	public String test() {
		
		int count =databaseTestMapper.countQuestions();
		
		return "DB接続成功! 登録問題数:" + count;
		
	}
}
