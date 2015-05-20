/**
 * Copyright (c) 2009, Benjamin Adams
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Benjamin Adams nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY Benjamin Adams ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Benjamin Adams BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * @author Ben
 */

package oru.aass.CS;

public class Scale {
	public static final int RATIO = 0;
	public static final int INTERVAL = 1;
	public static final int ORDINAL = 2;
	public static final int NOMINAL = 3;
	
	private static String[] typeStrings = {"ratio", "interval", "ordinal", "nominal"};
	
	private int type;
	
	// default scale is interval
	public Scale() {
		this.type = INTERVAL;
	}
	
	public Scale(int type) {
		if ((type >= RATIO) && (type <= NOMINAL)) {
			this.type = type; 
		} else {
			this.type = INTERVAL;
		}
	}
	
	public Scale(String typeString) {
		String ts = typeString.toLowerCase();
		if (ts.equals("ratio")) {
			type = RATIO;
		} else if (ts.equals("interval")) {
			type = INTERVAL;
		} else if (ts.equals("ordinal")) {
			type = ORDINAL;
		} else if (ts.equals("nominal")) {
			type = NOMINAL;
		} else { type = INTERVAL; }
	}
	
	public String toString() {
		return typeStrings[type];
	}
	
	public int getType() {
		return type;
	}

	public String toCSML() {
		return "		<csml:Scale>"+typeStrings[type]+"</csml:Scale>\n";
	}
}
