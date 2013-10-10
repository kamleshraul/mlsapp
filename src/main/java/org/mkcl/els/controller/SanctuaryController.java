package org.mkcl.els.controller;

import org.mkcl.els.domain.Sanctuary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/sanctuary")
public class SanctuaryController extends GenericController<Sanctuary>{

}
