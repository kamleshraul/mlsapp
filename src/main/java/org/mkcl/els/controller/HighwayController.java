package org.mkcl.els.controller;

import org.mkcl.els.domain.Highway;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/highway")
public class HighwayController extends GenericController<Highway> {

}
