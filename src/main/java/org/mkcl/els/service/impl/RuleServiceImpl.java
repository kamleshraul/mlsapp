package org.mkcl.els.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mkcl.els.service.IRuleService;
import org.springframework.stereotype.Service;

/**
 * Drools specific implementation of the IRuleService.
 */
@Service("ruleService")
public class RuleServiceImpl implements IRuleService {

	// TODO
	@Override
	public List<String> fireStateLessRules(Map<String, String> properties) {
		return new ArrayList<String>();
	}

}
