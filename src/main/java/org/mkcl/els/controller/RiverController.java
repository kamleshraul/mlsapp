package org.mkcl.els.controller;

import org.mkcl.els.domain.River;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/river")
public class RiverController extends GenericController<River>{

}
