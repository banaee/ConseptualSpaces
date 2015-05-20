
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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Hadi Banaee <Örebro University, AASS>
 */
public class Instance extends Element{
    
    // elements
	// note the key is domainID of point, NOT ID!
	private Hashtable<String, Point> points = new Hashtable<String, Point>();
	
	public Instance(String id, String label, String description) {
		super(id, label, description);
	}
	
	public Instance(String id) {
		super(id);
	}
	
	public void addPoint(Point p) {
		points.put(p.getDomainId(), p);
	}
	
	// remove point by domainId
	public void removePoint(String domainId) {
		points.remove(domainId);
	}
	
	// remove point by domainId
	public void removePoint(Point p) {
		points.remove(p.getDomainId());
	}
	
	public Point getPointById(String pid) {
		for (Enumeration<Point> e = points.elements(); e.hasMoreElements(); ) {
			Point p = e.nextElement();
			if (p.getId().equals(pid)) return p;
		}
		return null;		
	}
	
	// get point by domainId
	public Point getPoint(String domainId) {
		return points.get(domainId);
	}
	
	public String[] getDomainIds() {
		return points.keySet().toArray(new String[0]);
	}
	
	public boolean hasPointInDomain(String domainId) {
		return points.containsKey(domainId);
	}
	
    
    
    
}
