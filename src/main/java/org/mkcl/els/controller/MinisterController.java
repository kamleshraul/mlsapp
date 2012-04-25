package org.mkcl.els.controller;

import org.mkcl.els.domain.Minister;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/minister")
public class MinisterController  extends GenericController<Minister> {

}
