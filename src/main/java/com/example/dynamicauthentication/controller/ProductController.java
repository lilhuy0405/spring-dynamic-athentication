package com.example.dynamicauthentication.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productsUpdate")
public class ProductController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getAllProducts() {
        return "all products";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String productDetailUpdated(@PathVariable(name = "id") int id) {
        return "product Detail ".concat(String.valueOf(id));
    }
}
