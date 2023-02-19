package com.example.demo.controller;

import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.example.demo.memo.*;

@RestController
public class testController {
	
	@Autowired
	memoRepository memoRepository;
	
	@RequestMapping("/login")
	public ModelAndView manage() {
		 ModelAndView mav = new ModelAndView("login");
		return mav;
	}
	
	@RequestMapping("/insert")
	public void InsertDummies() {		 
        IntStream.rangeClosed(1, 10).forEach(i -> {
            memo m = memo.builder()
                    .memoText("Sample..." + i)
                    .build();
            //Create!
            memoRepository.save(m);
        });
    }
	
	@RequestMapping("/select")
	public void SelectDummies() {
		 
        Long id = 10L;
 
        Optional<memo> result = memoRepository.findById(id);
 
        System.out.println("=============================");
 
        if(result.isPresent()) {
            memo memo = result.get();
            System.out.println(memo);
        }
    }
	
	@RequestMapping("/update")
	public void UpdateDummies() {
        memo m = memo.builder()
                .id(10L)
                .memoText("Update Text")
                .build();
 
        memoRepository.save(m);
    }
	
	@RequestMapping("/delete")
	public void DeleteDummies() {
        Long id = 10L;
 
        memoRepository.deleteById(id);
    }
}
