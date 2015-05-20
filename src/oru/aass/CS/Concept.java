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
public class Concept extends Element{
    
    // attributes
	private String prototypeId = "";
	
	//elements
	// note: the key is domainID of region, NOT ID!
	private Hashtable<String, Region> regions = new Hashtable<String, Region>();
	
	public Concept(String id, String label, String description) {
		super(id, label, description);
	}
	
	public Concept(String id) {
		super(id);
	}
	
        public boolean isProperty() {
		return regions.size() == 1;
	}
	
	public void setPrototypeId(String pid) {
		prototypeId = new String(pid);
	}
	
	public String getPrototypeId() {
		return prototypeId;
	}
	
        
        public void addRegion(Region r) {
		regions.put(r.getDomainId(), r);
	}
	
	public void removeRegion(String domainId) {
		regions.remove(domainId);
	}
	
	public void removeRegion(Region r) {
		regions.remove(r.getDomainId());
	}
	
	public Region getRegionById(String rid) {
		for (Enumeration<Region> e = regions.elements(); e.hasMoreElements(); ) {
			Region r = e.nextElement();
			if (r.getId().equals(rid)) return r;
		}
		return null;
	}
	
	// get Region by domainId
	public Region getRegion(String domainId) {
		return regions.get(domainId);
	}

        public Region[] getRegions() {
            return regions.values().toArray(new Region[0]);
        }
	
	public String[] getDomainIds() {
		return regions.keySet().toArray(new String[0]);
	}
	
        public boolean hasRegionInDomain(String domainId) {
		return getRegion(domainId) != null;
	}
	
    
}
