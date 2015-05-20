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

import java.util.Hashtable;

/**
 *
 * @author Hadi Banaee <Örebro University, AASS>
 */
public class Domain extends Element{
    
    // elements
    private Hashtable<String, QualityDimension> qualityDimensions = new Hashtable<String, QualityDimension>();
	
    public Domain(String id, String label, String description) {
        super(id, label, description);
    }
	
    public Domain(String id) {
	super(id);
    }
    
    
    public void addQualityDimension(QualityDimension qd) {
	qualityDimensions.put(qd.getId(), qd);
    }

    public void removeQualityDimension(String id) {
	qualityDimensions.remove(id);
    }
	
    public void removeQualityDimension(QualityDimension q) {
  	qualityDimensions.remove(q.getId());
    }

    public void removeAllQualityDimensions() {
        qualityDimensions = new Hashtable<String, QualityDimension>();
    }
	
    public QualityDimension getQualityDimension(String id) {
	return qualityDimensions.get(id);
    }
	
    public int numberOfDimensions() {
	return qualityDimensions.size();
    }
	
    public String[] getQualityDimensionIds() {
	return qualityDimensions.keySet().toArray(new String[0]);
    }
    
    
    
    
    
    
    
}
