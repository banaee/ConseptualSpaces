/**
 * The MIT License
 *
 * Copyright 2015 Hadi Banaee <Örebro University, AASS>.
 * 
 * Copyright (c) 2009, Benjamin Adams
 * All rights reserved.
 *------------------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package oru.aass.CS;

/**
 *
 * @author Hadi Banaee <Örebro University, AASS>
 */
public class Point extends Element{
    
    // attributes
	private String domainId;
	
	// elements
	private String[] q = new String[0];	// Make Hashtable ??!!
	private double[] v = new double[0];
	
	public Point(String id, String domainId, String label, String description) {
		super(id, label, description);
		this.domainId = new String(domainId);
	}

	public Point(String id, String domainId) {
		super(id);
		this.domainId = new String(domainId);
	}

	public boolean setPoint(String[] q, double[] v) {
		if (q == null || v == null) return false;
		if (q.length != v.length) return false;
		
		this.q = q;
		this.v = v;
		
		return true;
	}
	
	public boolean isOK() {
		if (q == null || v == null) return false;
		if (q.length != v.length) return false;
		
		return true;
	}
	
	public boolean setq(String[] q) {
		if (q == null) return false;
		
		this.q = q;
		return true;
	}
	
	public String[] getq() {
		return q;
	}
	
	public boolean setv(double[] v) {
		if (v == null) return false;
		
		this.v = v;
		return true;
	}
	
	public double[] getv() {
		return v;
	}

        public Double[] getV() {
            Double[] newV = new Double[v.length];
            for (int i = 0; i < v.length; i++) {
                newV[i] = v[i];
            }
            return newV;
        }
	
	public String getDomainId() {
		return domainId;
	}
	
	public void setDomainId(String dId) {
		domainId = new String(dId);
	}
	
	public Double getValue(String qDimId) {
		for (int i = 0; i < q.length; i++) {
			if (q[i].equals(qDimId)) return v[i];
		}
		
		return null;
	}

        public boolean updateValue(String qDimId, double newValue) {
            for (int i = 0; i < q.length; i++) {
                if (qDimId.equals(q[i])) {
                    v[i] = newValue;
                    return true;
                }
            }
            return false;
        }
	
	public boolean domainOK(Domain d) {
		if (!d.getId().equals(domainId)) return false;
		for (int i = 0; i < q.length; i++) {
			if (d.getQualityDimension(q[i]) == null) return false; // quality dimension not in domain
		}
		
		return true;
	}

    
}
