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
public class Context extends Element{
    
    public static final int QUALITY_DIMENSION_TYPE = 0;
	public static final int DOMAIN_TYPE = 1;
	public static final int CONCEPT_TYPE = 2;
	public static final int INSTANCE_TYPE = 3;
	public static final int CONTRAST_CLASS_TYPE = 4;
	
	public static final String[] typeStrings = {"qualitydimension", "domain", "concept", "instance", "contrastclass"};
	
	// attributes
	private int type;
	
//      Copied from 	QualityDimensionTypeContext
        private String domainId;

                
	// elements cId -> weight value
	protected Hashtable<String,Double> weights = new Hashtable<String, Double>();

	public Context(String id, int type, String label, String description) {
		super(id, label, description);
		this.type = type;
	}

	public Context(String id, int type) {
		super(id);
		this.type = type;
	}
        
//        Copied and changed from 	QualityDimensionTypeContext
        public Context(String id, String domainId) {
//              super(id, Context.QUALITY_DIMENSION_TYPE, label, description);
                super(id);
		this.type = type;
                this.domainId = new String(domainId);
	}
        
//        Copied and changed from 	DomainTypeContext
        public Context(String id) {
//		super(id, Context.DOMAIN_TYPE);
                super(id);
                this.type = type;
            
	}
	
        
        
	public int getType() {
		return type;
	}
	
	private double sumOfWeights() {
		double returnval = 0.0;
		for (Enumeration<Double> e = weights.elements(); e.hasMoreElements(); ) {
			returnval += e.nextElement();
		}
		return returnval;
	}
	
	public void normalizeWeights() {
		if (weights == null) return;
		double sum = sumOfWeights();
		if (sum != 1.0 && sum != 0.0) {
			Hashtable<String,Double> newWeights = new Hashtable<String,Double>();
			for (Enumeration<String> e = weights.keys(); e.hasMoreElements(); ) {
				String key = e.nextElement();
				newWeights.put(key, weights.get(key) / sum);
			}
			weights = newWeights;
		}
	}
	
	public void addWeight(String elementId, double weight) {
		double newWeight = weight;
		// limit weights to be in range [0,1]
		if (weight < 0.0) weight = 0.0;
		if (weight > 1.0) weight = 1.0;
		weights.put(elementId, newWeight);
	}
	
	public void removeWeight(String elementId) {
		weights.remove(elementId);
	}
	
	public Double getWeight(String element) {
		return weights.get(element);
	}
	
	public String[] getcIds() {
		return weights.keySet().toArray(new String[0]);
	}
	
    
        
        
                //        Copied and changed from 	QualityDimensionTypeContext
        public void setDomainId(String dId) {
		domainId = new String(dId);
	}
	
	public String getDomainId() {
		return domainId;
	}
}
