/*
******************************************************************
File: org.mkcl.els.service.IAssemblyService.java
Copyright (c) 2011, sandeeps, ${company}
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */
package org.mkcl.els.service;

import java.util.List;

import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyNumber;

// TODO: Auto-generated Javadoc
/**
 * The Interface IAssemblyService.
 *
 * @author sandeeps
 * @version v1.0.0
 */
public interface IAssemblyService extends IGenericService<Assembly ,Long>{

/**
 * Find by assembly.
 *
 * @param assembly the assembly
 * @param locale the locale
 * @return the assembly
 */
public Assembly findByAssembly(String assembly);
public Assembly findCurrentAssembly();
public List<Assembly> findAllSorted(String locale);
}
