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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

/**
 *
 * @author Hadi Banaee <Örebro University, AASS>
 */
public class Model {
    
    
        public static final int MODEL_OK = 0;

        // 5-tuple from Conceptual Space Algebra
    private Hashtable<String,Domain> domains = new Hashtable<String,Domain>();
    private Hashtable<String,Concept> concepts = new Hashtable<String,Concept>();
//    private Hashtable<String,ContrastClass> contrastClasses = new Hashtable<String,ContrastClass>();
    private Hashtable<String,Instance> instances = new Hashtable<String,Instance>();
    private Hashtable<String,Context> contexts = new Hashtable<String,Context>();

//    private HashSet<String> allURIs = new HashSet<String>();
//
//    private String csmlBase = "";
	
    
    public Model() {
    }
    
//  Domain----------------------------------------------------------------------
    public void addDomain(String domainId, Domain domain) {
        domains.put(domainId, domain);
//        allURIs.add(domainId);
    }
	
    public void addDomain(Domain domain) {
        addDomain(domain.getId(), domain);
    }
	
    public void removeDomain(String domainId) {
        domains.remove(domainId);
//        allURIs.remove(domainId);
    }
	
    public void removeDomain(Domain d) {
        domains.remove(d.getId());
//        allURIs.remove(d.getId());
    }
	
    public Domain getDomain(String domainId) {
        return domains.get(domainId);
    }
	
    public Domain[] getDomains() {
        return domains.values().toArray(new Domain[0]);
    }

    public int getNumberOfDomains() {
        return domains.size();
    }

        
//  Concept---------------------------------------------------------------------
    public void addConcept(String conceptId, Concept concept) {
        concepts.put(conceptId, concept);
//        allURIs.add(conceptId);
    }
	
    public void addConcept(Concept concept) {
        addConcept(concept.getId(), concept);
    }
	
    public void removeConcept(String conceptId) {
        concepts.remove(conceptId);
//        allURIs.remove(conceptId);
    }
	
    public void removeConcept(Concept concept) {
        concepts.remove(concept.getId());
//        allURIs.remove(concept.getId());
    }
	
    public Concept getConcept(String conceptId) {
        return concepts.get(conceptId);
    }
	
    public Concept[] getConcepts() {
        return concepts.values().toArray(new Concept[0]);
    }

    public int getNumberOfConcepts() {
        return concepts.size();
    }
	
        
//  Instance--------------------------------------------------------------------
    public void addInstance(String instanceId, Instance instance) {
        instances.put(instanceId, instance);
//        allURIs.add(instanceId);
    }
	
    public void addInstance(Instance instance) {
        addInstance(instance.getId(), instance);
    }
	
    public void removeInstance(String instanceId) {
        instances.remove(instanceId);
//        allURIs.remove(instanceId);
    }
	
    public void removeInstance(Instance instance) {
        instances.remove(instance.getId());
//        allURIs.remove(instance.getId());
    }
	
    public Instance getInstance(String instanceId) {
        return instances.get(instanceId);
    }
	
    public Instance[] getInstances() {
        return instances.values().toArray(new Instance[0]);
    }

    public Instance[] getInstancesWithPointsInDomain(String domainId) {
        Domain d = getDomain(domainId);
        if (d == null) return null;
        ArrayList<Instance> iList = new ArrayList<Instance>();
        for (Instance i : getInstances()) {
            if (i.hasPointInDomain(domainId))
                iList.add(i);
        }
        return iList.toArray(new Instance[0]);
    }

    public int getNumberOfInstances() {
        return instances.size();
    }

    
//  Context--------------------------------------------------------------------
    public void addContext(String contextId, Context context) {
        contexts.put(contextId, context);
//        allURIs.add(contextId);
    }
	
    public void addContext(Context context) {
        addContext(context.getId(), context);
    }
	
    public void removeContext(String contextId) {
        contexts.remove(contextId);
//        allURIs.remove(contextId);
    }
	
    public void removeContext(Context context) {
        contexts.remove(context.getId());
//        allURIs.remove(context.getId());
    }
	
    public Context getContext(String contextId) {
        return contexts.get(contextId);
    }
	
    public Context[] getContexts() {
        return contexts.values().toArray(new Context[0]);
    }

    
//    public ArrayList<Domain> getIntersectionOfDomains            
//    public ArrayList<Domain> getUnionOfDomains
//    public Domain getDomainOfQDim
//    public ArrayList<Domain> getDomainsOfQDim
      
    public int compileModel() {
        return MODEL_OK;
    }
    
    public void refactorQDimId(String origId, String newId) {
        for (Enumeration<Domain> e = domains.elements(); e.hasMoreElements(); ) {
            Domain d = e.nextElement();
            QualityDimension qDim = d.getQualityDimension(origId);
            if (qDim != null) {
                d.removeQualityDimension(qDim);
                qDim.setId(newId);
                d.addQualityDimension(qDim);
            }
        }

        // refactor quality dimension-type contexts
//        for (Enumeration<Context> e = contexts.elements(); e.hasMoreElements(); ) {
//            Context c = e.nextElement();
//            if (c instanceof QualityDimensionTypeContext) {
//                QualityDimensionTypeContext dc = (QualityDimensionTypeContext) c;
//                Double w = dc.getWeight(origId);
//                if (w != null) {
//                    dc.removeWeight(origId);
//                    dc.addWeight(newId, w);
//                }
//            }
//        }
    }

    public void refactorDomainId(String origId, String newId) {
        Domain d = getDomain(origId);
        if (d != null) {
            removeDomain(d);
            d.setId(newId);
            addDomain(d);
        }

        // refactor concepts
        for (Enumeration<Concept> e = concepts.elements(); e.hasMoreElements(); ) {
            Concept c = e.nextElement();
            Region r = c.getRegion(origId);
            if (r != null) {
                c.removeRegion(r);
                r.setDomainId(newId);
                c.addRegion(r);
            }
        }

        // refactor instances
        for (Enumeration<Instance> e = instances.elements(); e.hasMoreElements(); ) {
            Instance i = e.nextElement();
            Point p = i.getPoint(origId);
            if (p != null) {
                i.removePoint(p);
                p.setDomainId(newId);
                i.addPoint(p);
            }
        }

        // refactor domain-type contexts
//        for (Enumeration<Context> e = contexts.elements(); e.hasMoreElements(); ) {
//            Context c = e.nextElement();
//            if (c instanceof DomainTypeContext) {
//                DomainTypeContext dc = (DomainTypeContext) c;
//                Double w = dc.getWeight(origId);
//                if (w != null) {
//                    dc.removeWeight(origId);
//                    dc.addWeight(newId, w);
//                }
//            }
//        }
    }

    public void refactorInstanceId(String origId, String newId) {
        Instance i = getInstance(origId);
        if (i != null) {
            removeInstance(i);
            i.setId(newId);
            addInstance(i);
        }

        // refactor prototypes of concepts
        for (Enumeration<Concept> e = concepts.elements(); e.hasMoreElements(); ) {
            Concept c = e.nextElement();
            if (c.getPrototypeId().equals(origId))
                c.setPrototypeId(newId);
        }

        // refactor instance-type contexts
//        for (Enumeration<Context> e = contexts.elements(); e.hasMoreElements(); ) {
//            Context c = e.nextElement();
//            if (c instanceof InstanceTypeContext) {
//                InstanceTypeContext ic = (InstanceTypeContext) c;
//                Double w = ic.getWeight(origId);
//                if (w != null) {
//                    ic.removeWeight(origId);
//                    ic.addWeight(newId, w);
//                }
//            }
//        }
    }

                            
                    
}
