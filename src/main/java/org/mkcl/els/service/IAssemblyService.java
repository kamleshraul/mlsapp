package org.mkcl.els.service;

import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyNumber;

public interface IAssemblyService extends IGenericService<Assembly ,Long>{

public Assembly findByAssemblyNumber(AssemblyNumber assemblyNumber);
}
