package com.example.springboot;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import java.nio.file.Path;
import java.util.Iterator; //json parsing

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.Map;

@Controller
public class HelloController {
	private Indexer indexer;


	@Autowired
	HelloController()throws IOException {
		indexer = new Indexer();
	}

/* 	@RequestMapping("/")
	public String getHomePage()  {
		return "index.html";
	} */
	

	@Autowired
    @RequestMapping(value = "/query")
	@CrossOrigin(origins = "*")
    public ResponseEntity<String> execute(@RequestBody Map<String, Object> map) throws IOException
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        //headers.add("access-control-allow-origin", "*");
        headers.add("vary", "accept-encoding");

		String q = "";
		String c = "";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if(entry.getKey() == "query"){
				q = entry.getValue().toString();
			}
			if(entry.getKey() == "count"){
				c = entry.getValue().toString();
			}


		}
		System.out.println("Incoming Query: " + q + ':' + c);


		String result = "";
		try{
			result = indexer.search(q,Integer.parseInt(c));
		}
		catch(Exception e){
			System.out.println(e);
		}

 
        return ResponseEntity.ok()
                    .headers(headers)
                    .body(result);
    }

}
