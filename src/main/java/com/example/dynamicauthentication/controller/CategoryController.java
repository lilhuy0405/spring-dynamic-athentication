package com.example.dynamicauthentication.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String createCategory() {
        return "create cate";
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public String updateCategory(@PathVariable(name = "id") int id) {
        return "update cate".concat(String.valueOf(id));
    }


    @RequestMapping(value = "/new", method = RequestMethod.PUT)
    public String newCateMethod(@PathVariable(name = "id") int id) {
        return "update cate".concat(String.valueOf(id));
    }
}
