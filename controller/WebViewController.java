package com.sena.springecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping({"/", "/index", "/tienda"})
    public String home() {
        return "forward:/index.html";
    }
}
