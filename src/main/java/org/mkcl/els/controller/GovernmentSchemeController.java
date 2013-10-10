package org.mkcl.els.controller;

import org.mkcl.els.domain.GovernmentScheme;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/governmentscheme")
public class GovernmentSchemeController extends GenericController<GovernmentScheme>{

}
