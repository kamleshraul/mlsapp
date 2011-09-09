/*
******************************************************************
File: org.mkcl.els.common.editors.AssemblyNumberEditor.java
Copyright (c) 2011, amitd, ${company}
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
package org.mkcl.els.common.editors;

import java.beans.PropertyEditorSupport;

import org.mkcl.els.domain.AssemblyNumber;
import org.mkcl.els.service.IAssemblyNumberService;

/**
 * The Class AssemblyNumberEditor.
 *
 * @author amitd
 * @version v1.0.0
 */
public class AssemblyNumberEditor extends PropertyEditorSupport {

	/** The service. */
	private IAssemblyNumberService service;

	/**
	 * Instantiates a new assembly number editor.
	 *
	 * @param service the service
	 */
	public AssemblyNumberEditor(IAssemblyNumberService service) {
		super();
		this.service = service;
	}
	
	/** 
	 * Map the id of an object to the object
	 */
	public void setAsText(String text) {
		if(! text.equals("")) {
			AssemblyNumber assemblyNo = 
				(AssemblyNumber) this.service.findById(Long.parseLong(text));
			
			this.setValue(assemblyNo);
		}
	}
}
