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
public class Region extends Element{
    
    // attributes
	private String domainId;
	private double[][] A = new double[0][0];
	private String[] q = new String[0];
	private double[] b = new double[0];
        
        private double[] centroid;
	
	// minimum axis-aligned bounding box
	private double[] mbbMins;
	private double[] mbbMaxs;
	
	private Point[] vPolytope;
        
        public Region(String id, String domainId, String label, String description) {
		super(id, label, description);
		this.domainId = domainId;
	}

	public Region(String id, String domainId) {
		super(id);
		this.domainId = domainId;
	}

        public void setDomainId(String dId) {
            domainId = dId;
        }
	
	public String getDomainId() {
		return domainId;
	}
	
        public boolean setRegion(double[][] A, String[] q, double[] b) {
		if (A == null || q == null || b == null) return false;
		if (A.length == 0) return false;
		if (A[0].length != q.length) return false;
		if (A.length != b.length) return false;
		if (!isRectangular(A)) return false;
			
		this.A = A;
		this.q = q;
		this.b = b;
		vPolytope = null;
		return true;
	}
	
       

        //----------------------------------------------------------------------
        public boolean setA(double[][] A) {
		if (A == null) return false;
		if (A.length == 0) return false;
		if (!isRectangular(A)) return false;
		
		this.A = A;
		vPolytope = null;
		return true;
	}
	
	public double[][] getA() {
		return A;
	}
	

        public boolean setq(String[] q) {
		if (q == null) return false;
		
		this.q = new String[q.length];
		for (int i = 0; i < q.length; i++) {
			this.q[i] = new String(q[i]);
		}
		vPolytope = null;
		return true;
	}
	
	public String[] getq() {
		if (q == null) return null;
		String[] newQ = new String[q.length];
		for (int i = 0; i < q.length; i++) {
			newQ[i] = new String(q[i]);
		}
		return newQ;
	}
	
	public boolean setb(double[] b) {
		if (b == null) return false;
		
		this.b = b;
		vPolytope = null;
		return true;
	}
	
	public double[] getb() {
		return b;
	}
	
        private boolean isRectangular(double[][] m) {
		if (m.length > 0) {
			int colnum = m[0].length;
			for (int i = 1; i < m.length; i++) {
				if (m[i].length != colnum) return false;
			}
			return true;
		}
		return false;
	}

        public boolean isOK() {
		if (A == null || q == null || b == null) return false;
		if (A.length == 0) return false;
		if (A[0].length != q.length) return false;
		if (A.length != b.length) return false;
		if (!isRectangular(A)) return false;

		return true;
	}
        //----------------------------------------------------------------------
        public boolean setCentroid(double[] centroid) {
		if (centroid == null || q == null) return false;
		if (centroid.length != q.length) return false;
		this.centroid = centroid;
		return true;
	}
	
	public double[] getCentroid() {
		return centroid;
	}
	
	public Point getCentroidAsPoint(String pId) {
		if (!isOK() || centroid == null || centroid.length != q.length) return null;
		Point p = new Point(new String(pId), new String(domainId));
		
		String[] qCopy = new String[q.length];
		double[] centroidCopy = new double[centroid.length];
		for (int i = 0; i < q.length; i++) {
			qCopy[i] = new String(q[i]);
			centroidCopy[i] = centroid[i];
		}
		p.setPoint(qCopy, centroidCopy);
		
		return p;
	}
	
	public Point getCentroidAsPoint() {
		return getCentroidAsPoint(new String(getId()+".CENTROID"));
	}
	
        public boolean isProperty() {
		return q.length == 1;
	}
	
        
        
	private boolean qMatch(String[] otherQ) {
		if (q == null || otherQ == null) return false;
		if (q.length != otherQ.length) return false;
		for (int i = 0; i < q.length; i++) {
			for (int j = 0; j < otherQ.length; j++) {
				if (q[i].equals(otherQ[j])) {
					break;
				}
				if (j == otherQ.length - 1) return false;
			}
		}
		return true;
	}
	
	private int[] qIdxOrder(String[] otherQ) {
		if (!qMatch(otherQ)) return null;
		int[] idxOrder = new int[q.length];
		
		for (int i = 0; i < q.length; i++) {
			for (int j = 0; j < otherQ.length; j++) {
				if (q[i].equals(otherQ[j])) {
					idxOrder[j] = i;
					break;
				}
			}
		}
		
		return idxOrder;
	}
	
	// get qhull format in the given quality dimension order
	public String qHullFormat(String[] qDimOrder) {
		String s = "";
		if (!isOK()) return s;
		
		int[] idxOrder = qIdxOrder(qDimOrder);
		if (idxOrder == null) return s;
		
		// write centroid
		if (centroid != null) {
			s += q.length + " 1\n";
			for (int i = 0; i < idxOrder.length; i++) {
				int curIdx = idxOrder[i];
				s += "  ";
				s += centroid[curIdx];
			}
			s += "\n";
		}
		
		s += (q.length + 1) + "\n";
		s += (A.length) + "\n";
		
		for (int m = 0; m < A.length; m++) {
			for (int n = 0; n < idxOrder.length; n++) {
				int curIdx = idxOrder[n];
				s += "  ";
				s += A[m][curIdx];
			}
			s += "  " + (-b[m]) + "\n";
		}
		
		return s;
	}
	
	// get qhull format
	public String qHullFormat() {
		return qHullFormat(q);
	}
	
	public boolean setBoundingBox(double[] mins, double[] maxs) {
		if (mins == null || maxs == null || isOK()) return false;
		if (mins.length != q.length || maxs.length != q.length) return false;
		mbbMins = mins.clone();
		mbbMaxs = maxs.clone();
		return true;
	}
	
	public double[][] getBoundingBox() {
		if (mbbMins == null || mbbMaxs == null) return null;
		
		double[][] bb = new double[2][mbbMins.length];
		for (int i = 0; i < mbbMins.length; i++) {
			bb[0][i] = mbbMins[i];
			bb[1][i] = mbbMaxs[i];
		}
		return bb;
	}
	
	public void setVPolytope(Point[] pts) {
		vPolytope = pts;
		// TODO error checking
	}
	
	public Point[] getVPolytope() {
		return vPolytope;
	}

    
    
    
}
