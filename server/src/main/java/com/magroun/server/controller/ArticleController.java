package com.magroun.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article-controller")
public class ArticleController {
	
	  @GetMapping
	  public ResponseEntity<String> test() {
	    return ResponseEntity.ok(" from secured endpoint");
	  }

}
