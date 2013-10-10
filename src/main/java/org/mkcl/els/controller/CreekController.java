package org.mkcl.els.controller;

import org.mkcl.els.domain.Creek;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/creek")
public class CreekController extends GenericController<Creek> {

}
