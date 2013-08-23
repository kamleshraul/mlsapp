package org.mkcl.els.service;

import java.util.List;

public interface IWorkflowService {
	 public List<String> findSupportingMembers(final String strDeviceId,final String strDeviceType);
}
