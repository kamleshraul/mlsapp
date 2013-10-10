package org.mkcl.els.controller;

import org.mkcl.els.domain.GovernmentProgram;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/governmentprogram")
public class GovernmentProgramController extends GenericController<GovernmentProgram>{

}
