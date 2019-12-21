package com.crawlerse.serchengine;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SearchController {

	
	@Autowired
	BasicWebCrawler basicWebCrawler;

    @PostMapping("/api/search")
    public Set<String> getSearchResultViaAjax(@RequestBody Search searchKey) {
    	
    	try {
    		System.out.println("POST-- get the data");
        	Set<String> res = basicWebCrawler.getArticles2(searchKey.getSearchkey());
        	
        	if(res.size()<=0)
        	{
        		Set<String> s = new HashSet<>();
        		s.add("No data found");
        		res = s;
        	}
        	return res;
    		
    	}catch(Exception e) {
    		throw new RuntimeException("No datA found..");
    		
    	}
    	
  
    }
    
    @GetMapping("/api/post")
    public void posturl() {
    	
    	System.out.println("GET-- feed the data");
 
    	basicWebCrawler.getPageLinks("https://www.geeksforgeeks.org/");
  
    }
    
    
   
    

}
