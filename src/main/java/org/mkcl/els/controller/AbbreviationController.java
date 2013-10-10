package org.mkcl.els.controller;

import org.mkcl.els.domain.Abbreviation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping(value="/abbreviation")
public class AbbreviationController extends GenericController<Abbreviation>{

}
