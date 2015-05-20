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

public class Range {
	public static final double DEFAULT_MIN = Long.MIN_VALUE;
	public static final double DEFAULT_MAX = Long.MAX_VALUE;
	
	private double min;
	private double max;
	
	public Range() {
		min = DEFAULT_MIN;
		max = DEFAULT_MAX;
	}
	
	public Range(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public void setMin(double min) {
		this.min = min;
	}
	
	public double getMin() {
		return min;
	}
	
	public void setMax(double max) {
		this.max = max;
	}
	
	public double getMax() {
		return max;
	}

        public double getMagnitude() {
            return Math.abs(max - min);
        }
	
	public boolean inRange(double value) {
		return value >= min && value <= max;
	}
	
	public boolean isDefaultRange() {
		return (min == DEFAULT_MIN && max == DEFAULT_MAX);
	}

	public String toCSML() {
		String csml = "";
		csml += "		<csml:Range>\n";
		if (min != DEFAULT_MIN)
			csml += "			<csml:Min>"+min+"</csml:Min>\n";
		if (max != DEFAULT_MAX)
			csml += "			<csml:Max>"+max+"</csml:Max>\n";
		csml += "		</csml:Range>\n";
		return csml;
	}
}
