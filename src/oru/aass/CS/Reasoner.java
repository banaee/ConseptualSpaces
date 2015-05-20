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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import oru.aass.CS.utils.MathFunc;
import oru.aass.CS.utils.Pair;

/**
 *
 * @author Hadi Banaee <Örebro University, AASS>
 */
public class Reasoner {
    
//    ??????
    public static final int SIMILARITY_EXPONENTIAL = 0;
    public static final int SIMILARITY_GAUSSIAN = 1;
    public static final int SIMILARITY_LINEAR = 2;

    private Model model;
    private double c = 1.0; // similarity sensitivity parameter

//    ??????
    private String qHullPath = "/opt/local/bin/qhull";
    private String tempDir = "temp/";
    private long qHullTimeout = 30000; // 30 sec
//    private double roundPrecision = Globals.roundPrecision; // rounding error buffer for identifying if values are equal after qhull, qhalf, etc.
    private double roundPrecision = 0.00000001;

//    ????????
    private String glpsolPath = "/usr/local/bin/glpsol";
    private long glpsolTimeout = 30000; // 30 sec

    
    public Reasoner() {
    }

    public Reasoner(Model model) {
        this.model = model;
    }

    // set the default similarity sensitivity parameter
    public void setC(double c) {
        this.c = c;
    }

    // get the default similarity sensitivity parameter
    public double getC() {
        return c;
    }

    
    // returns a list of all domains shared by the given list of instances
    public ArrayList<Domain> sharedDomains(ArrayList<Instance> instances) {
        ArrayList<Domain> domains = new ArrayList<Domain>();
        if (instances == null || instances.size() == 0) return domains;

        ArrayList<String> domainIds = new ArrayList<String>(Arrays.asList(instances.get(0).getDomainIds()));
        for (int i = domainIds.size()-1; i >= 0; i--) {
            String dId = domainIds.get(i);
            for (int j = 1; j < instances.size(); j++) {
                Instance curI = instances.get(j);
                String[] dIds = curI.getDomainIds();
                if (!Arrays.asList(dIds).contains(dId)) {
                    domainIds.remove(i);
                    break;
                }
            }
        }

        for (String domainId : domainIds) {
            Domain d = model.getDomain(domainId);
            if (d != null)
                domains.add(d);
        }

        return domains;
    }

    public ArrayList<Domain> sharedDomains(Instance i1, Instance i2) {
        ArrayList<Instance> iList = new ArrayList<Instance>();
        iList.add(i1);
        iList.add(i2);
        return sharedDomains(iList);
    }

    public ArrayList<Domain> sharedDomains(Concept concept, Instance instance) {
        ArrayList<Domain> domains = new ArrayList<Domain>();
        String[] domainIds = concept.getDomainIds();
        for (int i = 0; i < domainIds.length; i++) {
            Domain d = model.getDomain(domainIds[i]);
            if ((instance.getPoint(domainIds[i]) != null) && (d != null))
                domains.add(d);
        }

        return domains;
    }

    public ArrayList<Domain> sharedDomains(Instance instance, Concept concept) {
        return sharedDomains(concept, instance);
    }

    public ArrayList<Domain> sharedDomains(Concept c1, Concept c2) {
        ArrayList<Domain> domains = new ArrayList<Domain>();
        String[] domainIds = c1.getDomainIds();
        for (int i = 0; i < domainIds.length; i++) {
            Domain d = model.getDomain(domainIds[i]);
            if ((c2.getRegion(domainIds[i]) != null) && (d != null))
                domains.add(d);
        }
        return domains;
    }

//    -----------------------------------------------------------------------
    
   // returns weighted distance between two points in a domain
//    public double distance(Point p1, Point p2, QualityDimensionTypeContext context) {
      public double distance(Point p1, Point p2, Context context) {
        if (p1 == null || p2 == null) return -1.0;
        if (context == null) {
//            context = new QualityDimensionTypeContext("__TEMP", p1.getDomainId());
            context = new Context("__TEMP", p1.getDomainId());
            Domain dd = model.getDomain(context.getDomainId());
            String[] qdIds = dd.getQualityDimensionIds();
            for (int i = 0; i < qdIds.length; i++) {
                double value = 1.0 / qdIds.length;
                context.addWeight(qdIds[i], value);
            }
        }
        if (!p1.getDomainId().equals(context.getDomainId())) return -1.0;
        if (!p2.getDomainId().equals(context.getDomainId())) return -1.0;

        Domain d = model.getDomain(context.getDomainId());
        if (d == null) return -1.0;

        double dist = 0.0;

        String[] qdIds = d.getQualityDimensionIds();

        for (int i = 0; i < qdIds.length; i++) {
            QualityDimension qDim = d.getQualityDimension(qdIds[i]);
            double weight = context.getWeight(qdIds[i]);
            double diff = Math.abs(p2.getValue(qdIds[i]) - p1.getValue(qdIds[i]));
            // modify difference if the dimension is circular
            if (qDim.isCircular()) {
                Range r = qDim.getRange();
                double maxDelta = r.getMagnitude() / 2.0;
                if (diff > maxDelta) {
                    diff -= r.getMagnitude();
                }
            }
            dist += weight * diff * diff;
        }

        return Math.sqrt(dist);
    }

    // returns semantic similarity of two instances given domain type and quality dimension type contexts
    // simFunction is one of exponential, gaussian, or linear:
    // 		SIMILARITY_EXPONENTIAL, SIMILARITY_GAUSSIAN, SIMILARITY_LINEAR
    public double semanticSimilarity(String instanceId1, String instanceId2, String domainTypeContextId,
                    String[] qualityDimensionTypeContextIds, int simFunction) //throws InvalidContextTypeException,InvalidInstanceException, InvalidContextException 
    {
        Instance i1 = model.getInstance(instanceId1);
        if (i1 == null) throw null;//new InvalidInstanceException();
        Instance i2 = model.getInstance(instanceId2);
        if (i2 == null) throw null;//new InvalidInstanceException();
        Context dContext;
        try {
            dContext = model.getContext(domainTypeContextId);
        } catch (Exception e) {
            dContext = null;
        }
        //if (dContext == null) throw new InvalidContextException();
        if (dContext == null) {
//            dContext = new DomainTypeContext("__TEMP_DC");
            dContext = new Context("__TEMP_DC");
            ArrayList<Domain> domains = sharedDomains(i1, i2);
            for (Domain dd : domains) {
                dContext.addWeight(dd.getId(), 1.0 / domains.size());
            }
        }
        if (dContext.getType() != Context.DOMAIN_TYPE) throw null;//new InvalidContextTypeException();
        ArrayList<Context> qdContexts = new ArrayList<Context>();
        if (qualityDimensionTypeContextIds != null) {
            for (int i = 0; i < qualityDimensionTypeContextIds.length; i++) {
               Context qdContext = model.getContext(qualityDimensionTypeContextIds[i]);
    //           if (qdContext == null) throw new InvalidContextException();
    //           if (qdContext.getType() != Context.QUALITY_DIMENSION_TYPE) throw new InvalidContextTypeException();
                if (qdContext == null) qdContexts.add(null);
                 if (qdContext.getType() != Context.QUALITY_DIMENSION_TYPE) qdContexts.add(null);
                qdContexts.add(qdContext);
             }
        }

        if (i1.equals(i2)) return 0.0;
        ArrayList<Domain> domains = sharedDomains(i1, i2);

        double dist = 0.0;
        for (int i = 0; i < domains.size(); i++) {
            Domain d = domains.get(i);
            double dWeight = dContext.getWeight(d.getId());
            if (dWeight <= 0.0) break;  // don't bother calculating distance if zero, negative weights are invalid
            Point p1 = i1.getPoint(d.getId());
            Point p2 = i2.getPoint(d.getId());
//            QualityDimensionTypeContext qdC = null;
            Context qdC = null;
            for (int q = 0; q < qdContexts.size(); q++) {
//                QualityDimensionTypeContext qdCTest = (QualityDimensionTypeContext) qdContexts.get(q);
                Context qdCTest = (Context) qdContexts.get(q);
                if (qdCTest.getDomainId().equals(d.getId())) {
                    qdC = qdCTest;
                    break;
                }
            }
            if (qdC != null) {
                //double qdNNorm = Math.sqrt(d.numberOfDimensions());
                //double qdDist = distance(p1, p2, qdC);
                //System.out.println("dWeight: "+dWeight);
                //System.out.println("qdNNorm: "+qdNNorm);
                //System.out.println("qdDist : "+qdDist);
                //System.out.println("qdNNorm*qdDist = "+(qdNNorm*qdDist));

                dist += dWeight * Math.sqrt(d.numberOfDimensions()) * distance(p1, p2, qdC);
            } else {
                dist += dWeight * Math.sqrt(d.numberOfDimensions()) * distance(p1, p2, null);
            }
        }

        double similarity = 0.0;
        if (simFunction == SIMILARITY_GAUSSIAN) {
            similarity = Math.exp(-c*dist*dist);
        } else if (simFunction == SIMILARITY_EXPONENTIAL) {
            similarity = Math.exp(-c*dist);
        } else if (simFunction == SIMILARITY_LINEAR) {
            if (dist == 0)
                similarity = Double.POSITIVE_INFINITY;
            else
                similarity = 1.0 / (c * dist);
        }
        return similarity;
    }

    public double semanticSimilarity(String instanceId1, String instanceId2, String domainTypeContextId, String[] qualityDimensionTypeContextIds) 
            //throws InvalidContextTypeException, InvalidInstanceException, InvalidContextException 
    {
        return semanticSimilarity(instanceId1, instanceId2, domainTypeContextId, qualityDimensionTypeContextIds,
                        SIMILARITY_EXPONENTIAL);
    }
 
    
     public Concept learnConceptFromExemplars(String conceptId, ArrayList<Instance> exemplars, ArrayList<Domain> domains) {
        if (domains == null)
            domains = sharedDomains(exemplars);
        Concept concept = new Concept(conceptId);
        for (int i = 0; i < domains.size(); i++) {
            Domain d = domains.get(i);
            ArrayList<Point> points = new ArrayList<Point>();
            for (int j = 0; j < exemplars.size(); j++) {
                Instance ex = exemplars.get(j);
                Point p = ex.getPoint(d.getId());
                if (p != null)
                    points.add(p);
            }
            Region newRegion = convexHull(points, conceptId+"."+d.getId()+"_REGION");
            if (newRegion != null)
                concept.addRegion(newRegion);
        }
        return concept;
    }

    public Concept learnConceptFromExemplars(String conceptId, ArrayList<Instance> exemplars) {
            return learnConceptFromExemplars(conceptId, exemplars, null);
    }

    // use Voronoi tessellation to get regions based on prototype instances for the given domains.
    // If domains is null, the domains are the intersecting domains for all prototypes
    public ArrayList<Concept> conceptsFromPrototypeVoronoi(String baseConceptId, ArrayList<Instance> prototypes,
                    ArrayList<Domain> domains) {
            ArrayList<Concept> concepts = new ArrayList<Concept>();
            if (prototypes == null || prototypes.size() < 2)
                    return concepts;

            // find domains that are specified for every prototype instance
            if (domains == null) {
                    domains = sharedDomains(prototypes);
            }
            // TODO
            return concepts;
    }

    // use Voronoi tessellation to get regions based on prototype instances
    public ArrayList<Concept> conceptsFromPrototypeVoronoi(String baseConceptId, ArrayList<Instance> prototypes) {
            return conceptsFromPrototypeVoronoi(baseConceptId, prototypes, null);
    }

    // returns a region that fills the entire domain
    public Region domainRegion(String domainId) {
        Domain d = model.getDomain(domainId);
        if (d == null) return null;

        Region dRegion = new Region(new String(domainId + ".REGION"), new String(domainId));

        int qdNum = d.numberOfDimensions();
        String[] qdOrder = d.getQualityDimensionIds();
        double[][] newA = new double[qdNum*2][qdNum];
        for (int i = 0; i < newA.length; i++) {
            Arrays.fill(newA[i], 0.0);
        }
        double[] newb = new double[qdNum*2];

        for (int i = 0; i < qdNum; i++) {
            QualityDimension qd = d.getQualityDimension(qdOrder[i]);
            double min = qd.getRange().getMin();
            double max = qd.getRange().getMax();
            newA[i*2][i] = -1.0;
            newA[i*2+1][i] = 1.0;
            newb[i*2] = -min;
            newb[i*2+1] = max;
        }
        dRegion.setRegion(newA, qdOrder, newb);

        return dRegion;
    }

    public Region domainRegion(Domain d) {
        return domainRegion(d.getId());
    }

    public ArrayList<Region> voronoiTessellation(ArrayList<Point> prototypes) {
        if (prototypes == null) return null;
        ArrayList<Region> newRegions = new ArrayList<Region>();
        if (prototypes.size() == 0) return newRegions;

        String domainId = prototypes.get(0).getDomainId();
        Domain domain = model.getDomain(domainId);
        if (domain == null) return null;
        // check that prototype points are consistent with domain
        for (int i = 1; i < prototypes.size(); i++) {
            Point p = prototypes.get(i);
            if (!p.getDomainId().equals(domainId))
                return null;
            else if (!p.domainOK(domain))
                return null;
        }

        String[] qOrder = domain.getQualityDimensionIds();

        if (prototypes.size() == 1) {
            newRegions.add(domainRegion(domainId));
            return newRegions;
        }

        // generate input for qvoronoi
        String qVoronoiInput = "";
        qVoronoiInput += domain.numberOfDimensions() + "\n";
        qVoronoiInput += prototypes.size() + "\n";
        for (int i = 0; i < prototypes.size(); i++) {
            Point p = prototypes.get(i);
            for (int j = 0; j < domain.numberOfDimensions(); j++) {
                double val = p.getValue(qOrder[j]);
                qVoronoiInput += "  " + val;
            }
            qVoronoiInput += "\n";
        }

        String rand;
        String qVoronoiOutputFN;
        File f;

        do {  // find unique random filename
            rand = Long.toHexString(new Random().nextLong());
            qVoronoiOutputFN = new String(tempDir + rand + ".tmp");
            f = new File(qVoronoiOutputFN);
        } while (f.exists());

        try {
            // run qhull
            String[] qVoronoiCmd = {qHullPath, "v", "Qbb", "Qx", "Fs", "Fi", "Fo", "Fv", "TO", qVoronoiOutputFN};
            Process p = Runtime.getRuntime().exec(qVoronoiCmd);

            PrintStream ps = new PrintStream(new BufferedOutputStream(p.getOutputStream()));
            // write H-polytope to qvoronoi
            //System.out.println(qVoronoiInput);
            ps.print(qVoronoiInput);
            ps.close();

            // Set a timer to interrupt the process if it does not return within the timeout period
            Timer timer = new Timer();
            timer.schedule(new InterruptScheduler(Thread.currentThread()), qHullTimeout);
            try {
                    p.waitFor();
            } catch (InterruptedException e) {
                    // Stop the process from running
                    p.destroy();
                    return null;
            }
            finally {
                    // Stop the timer
                    timer.cancel();
            }

            BufferedReader qVoronoiResultFile = new BufferedReader(new FileReader(qVoronoiOutputFN));
            ArrayList<String> qHullResult = new ArrayList<String>();
            String inString;
            while ((inString = qVoronoiResultFile.readLine()) != null) {
                    qHullResult.add(inString);
                    //System.out.println(inString);
            }
            qVoronoiResultFile.close();

            if (qHullResult.size() < 3) return null;

            int numRegions = Integer.parseInt(qHullResult.get(0).trim().split("\\s+")[5]);
            if (numRegions != prototypes.size()) {
                    // TODO
            }
            int numClosedHalfplanes = Integer.parseInt(qHullResult.get(2));
            //System.out.println("numClosed:  "+numClosedHalfplanes);
            int numOpenHalfplanes = Integer.parseInt(qHullResult.get(numClosedHalfplanes+3));
            //System.out.println("numOpen: "+ numOpenHalfplanes);

            String[] newQ = new String[qOrder.length];
            for (int i = 0; i < qOrder.length; i++) {
                    newQ[i] = new String(qOrder[i]);
            }

            for (int i = 0; i < numRegions; i++) {
                // number of regions should equal number of prototypes
                Region newRegion = new Region(prototypes.get(i).getId()+".VORONOI_REGION", domainId);

                ArrayList<double[]> tempA = new ArrayList<double[]>();
                ArrayList<Double> tempb = new ArrayList<Double>();

                for (int j = 3; j < numClosedHalfplanes + 3; j++) {
                    String[] resultLine = qHullResult.get(j).trim().split("\\s+");
                    if (Integer.parseInt(resultLine[1]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length-1; n++) {
                            aRow[n-3] = Double.parseDouble(resultLine[n]);
                        }
                        bVal = -Double.parseDouble(resultLine[resultLine.length-1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    } else if (Integer.parseInt(resultLine[2]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length-1; n++) {
                            aRow[n-3] = -Double.parseDouble(resultLine[n]);
                        }
                        bVal = Double.parseDouble(resultLine[resultLine.length-1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    }
                }

                for (int j = 4 + numClosedHalfplanes; j < numClosedHalfplanes + 4 + numOpenHalfplanes; j++) {
                    String[] resultLine = qHullResult.get(j).trim().split("\\s+");
                    if (Integer.parseInt(resultLine[1]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length-1; n++) {
                            aRow[n-3] = Double.parseDouble(resultLine[n]);
                        }
                        bVal = -Double.parseDouble(resultLine[resultLine.length-1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    } else if (Integer.parseInt(resultLine[2]) == i) {
                        double[] aRow = new double[newQ.length];
                        double bVal;
                        for (int n = 3; n < resultLine.length-1; n++) {
                            aRow[n-3] = -Double.parseDouble(resultLine[n]);
                        }
                        bVal = Double.parseDouble(resultLine[resultLine.length-1]);
                        tempA.add(aRow);
                        tempb.add(bVal);
                    }
                }

                double[][] newA = new double[tempA.size()][newQ.length];
                double[] newB = new double[tempb.size()];
                for (int j = 0; j < newA.length; j++) {
                    newA[j] = tempA.get(j);
                    newB[j] = tempb.get(j);
                }

                newRegion.setq(newQ);
                newRegion.setA(newA);
                newRegion.setb(newB);

                newRegions.add(newRegion);
            }
        } catch (IOException e) {
            // TODO
            return null;
        }

        // delete qvoronoi temp file
        if (f.exists())
            f.delete();

        return newRegions;
    }

    // return convex region from convex hull of input points
    public Region convexHull(ArrayList<Point> points, String newRegionId) {
        Region newRegion = null;
        if (points == null || points.size() == 0) return null;

        // test that all points are from same domain
        String domainId = new String(points.get(0).getDomainId());
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (!p.getDomainId().equals(domainId)) return null;
        }

        Domain domain = model.getDomain(domainId);

        // number of points must be at least # of dimensions in domain + 1
        if (points.size() < domain.numberOfDimensions() + 1) return null;

        String[] qDims = domain.getQualityDimensionIds();
        double[][] pointValues = new double[points.size()][qDims.length];
        double[] midpoint = new double[qDims.length];

        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < qDims.length; j++) {
                pointValues[i][j] = points.get(i).getValue(qDims[j]);
            }
        }

        // if # dimensions == 1
        if (qDims.length == 1) {
            double[] sorted = new double[pointValues.length];
            for (int i = 0; i < pointValues.length; i++)
                    sorted[i] = pointValues[i][0];
            Arrays.sort(sorted);
            double min = sorted[0];
            double max = sorted[sorted.length-1];
            double sum = 0.0;
            for (int i = 0; i < sorted.length; i++) {
                sum += sorted[i];
            }
            midpoint[0] = sum / sorted.length;

            newRegion = new Region(newRegionId, domainId);
            newRegion.setq(qDims);
            double[][] aMatrix = { { 1.0 }, { -1.0 } };
            newRegion.setA(aMatrix);
            double[] bVector = { max, -min };
            newRegion.setb(bVector);
            newRegion.setCentroid(midpoint);
        } else {  // if # dimensions > 1 call qhull program
            String rand;
            String qHullOutput;
            File f;

            do {  // find unique random filename
                    rand = Long.toHexString(new Random().nextLong());
                    qHullOutput = new String(tempDir + rand + ".tmp");
                    f = new File(qHullOutput);
            } while (f.exists());

            try {
                // run qhull
                String[] qHull = {qHullPath, "FV", "n", "TO", qHullOutput};
                Process p = Runtime.getRuntime().exec(qHull);

                PrintStream ps = new PrintStream(new BufferedOutputStream(p.getOutputStream()));
                ps.println(qDims.length);
                ps.println(points.size());
                for (int i = 0; i < points.size(); i++) {
                    for (int j = 0; j < qDims.length; j++) {
                        ps.print(pointValues[i][j]);
                        if (j < qDims.length - 1)
                            ps.print(" ");
                    }
                    ps.println();
                }
                ps.close();

                // Set a timer to interrupt the process if it does not return within the timeout period
                Timer timer = new Timer();
                timer.schedule(new InterruptScheduler(Thread.currentThread()), qHullTimeout);
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    // Stop the process from running
                    p.destroy();
                    return null;
                }
                finally {
                    // Stop the timer
                    timer.cancel();
                }

                BufferedReader qHullResultFile = new BufferedReader(new FileReader(qHullOutput));
                String inString;
                ArrayList<String> qHullResult = new ArrayList<String>();
                while ((inString = qHullResultFile.readLine()) != null) {
                    qHullResult.add(inString);
                }
                qHullResultFile.close();

                if (qHullResult.size() < 5) return null;
                String[] midpointStrs = qHullResult.get(1).trim().split("\\s+");
                for (int i = 0; i < midpoint.length; i++) {
                    midpoint[i] = Double.parseDouble(midpointStrs[i]);
                }
                int cols = Integer.parseInt(qHullResult.get(2).trim());
                int halfplaneNum = Integer.parseInt(qHullResult.get(3).trim());
                double[][] aMatrix = new double[halfplaneNum][cols-1];
                double[] bVector = new double[halfplaneNum];
                for (int i = 4; i < halfplaneNum + 4; i++) {
                    String[] rowVals = qHullResult.get(i).trim().split("\\s+");
                    for (int j = 0; j < rowVals.length - 1; j++) {
                        aMatrix[i-4][j] = Double.parseDouble(rowVals[j]);
                    }
                    bVector[i-4] = -Double.parseDouble(rowVals[rowVals.length - 1]);
                }
                newRegion = new Region(newRegionId, domainId);
                newRegion.setq(qDims);
                newRegion.setA(aMatrix);
                newRegion.setb(bVector);
                newRegion.setCentroid(midpoint);
            } catch (IOException e) {
                return null;
            }

            // delete the qhull temp file
            if (f.exists())
                f.delete();
        }

        return newRegion;
    }

     public boolean samePoint(Point p1, Point p2) {
        if (p1 == p2) return true;
        if (p1 == null || p2 == null) return false;
        if (p1.equals(p2)) return true;
        String domainId = p1.getDomainId();
        if (domainId == null) return false;
        Domain d = model.getDomain(domainId);
        if (d == null) return false;
        if (!p1.getDomainId().equals(p2.getDomainId())) return false;

        String[] qdIds = d.getQualityDimensionIds();
        for (int i = 0; i < qdIds.length; i++) {
            if (!equalWithinPrecision(p1.getValue(qdIds[i]), p2.getValue(qdIds[i])))
                return false;
        }
        return true;
    }

    // returns true if the two instances have the same points for each of the given domains
    public boolean sameInstance(Instance i1, Instance i2, ArrayList<String> domainIds) {
        for (int i = 0; i < domainIds.size(); i++) {
            Point p1 = i1.getPoint(domainIds.get(i));
            Point p2 = i2.getPoint(domainIds.get(i));
            if (!samePoint(p1, p2))
                return false;
        }
        return true;
    }

    // returns true if the two instances have the same points for all shared domains
    public boolean sameInstance(Instance i1, Instance i2) {
        ArrayList<Domain> domains = sharedDomains(i1, i2);
        ArrayList<String> domainIds = new ArrayList<String>();
        for (int i = 0; i < domains.size(); i++) {
            domainIds.add(domains.get(i).getId());
        }
        return sameInstance(i1, i2, domainIds);
    }

    // returns true if region r contains point p
    public boolean contains(Region r, Point p) {
        if (r == null || p == null) return false;
        String domainId = r.getDomainId();
        // point and region must be in same domain
        if (domainId == null || !r.getDomainId().equals(p.getDomainId())) return false;
        Domain d = model.getDomain(domainId);
        // domain must exist in model
        if (d == null) return false;
        // region and point must be consistent with domain
        if (!r.domainOK(d) || !p.domainOK(d)) return false;

        return r.contains(p, d, roundPrecision);
    }

    // returns true if region r1 contains region r2 (i.e. r2's convex hull points) completely
    public boolean contains(Region r1, Region r2) {
        Point[] hullPoints = r2.getVPolytope();
        if (hullPoints == null) hullPoints = vPolytope(r2);
        if (hullPoints == null) return false;
        for (int i = 0; i < hullPoints.length; i++) {
            if (!contains(r1, hullPoints[i])) return false;
        }
        return true;
    }

    // returns true if concept c contains instance i for the given domains
    public boolean contains(Concept concept, Instance instance, ArrayList<Domain> domains) {
        return concept.contains(instance, domains.toArray(new Domain[0]));
    }

    // returns true if concept c contains instance i for all shared domains
    public boolean contains(Concept concept, Instance instance) {
        return contains(concept, instance, sharedDomains(concept, instance));
    }

    // gets the V-polytope representation of a region as a set of points
    // also sets the vPolytope in the region data structure, so that it does not need to be calculated again
    public Point[] vPolytope(Region r) {
        Point[] points = null;

        if (r.getCentroidAsPoint() != null) {
            String qHullInput = r.qHullFormat();

            String rand;
            String qHullOutputFN;
            File f;

            do {  // find unique random filename
                rand = Long.toHexString(new Random().nextLong());
                qHullOutputFN = new String(tempDir + rand + ".tmp");
                f = new File(qHullOutputFN);
            } while (f.exists());

            try {
                // run qhull
                String[] qHull = {qHullPath, "H", "Fp", "TO", qHullOutputFN};
                Process p = Runtime.getRuntime().exec(qHull);

                PrintStream ps = new PrintStream(new BufferedOutputStream(p.getOutputStream()));
                // write H-polytope to qhull
                ps.print(qHullInput);
                ps.close();

                // Set a timer to interrupt the process if it does not return within the timeout period
                Timer timer = new Timer();
                timer.schedule(new InterruptScheduler(Thread.currentThread()), qHullTimeout);
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    // Stop the process from running
                    p.destroy();
                    return null;
                }
                finally {
                    // Stop the timer
                    timer.cancel();
                }

                BufferedReader qHullResultFile = new BufferedReader(new FileReader(qHullOutputFN));
                ArrayList<String> qHullResult = new ArrayList<String>();
                String inString;
                while ((inString = qHullResultFile.readLine()) != null) {
                    qHullResult.add(inString);
                }
                qHullResultFile.close();

                if (qHullResult.size() < 3) return null;
                int qLength = Integer.parseInt(qHullResult.get(0));
                if (qLength != r.getq().length) return null;

                int numPoints = Integer.parseInt(qHullResult.get(1));
                points = new Point[numPoints];
                for (int i = 0; i < numPoints; i++) {
                    String row = qHullResult.get(i+2);
                    String[] rowVals = row.trim().split("\\s+");
                    points[i] = new Point(new String(r.getId()+".VPOLYTOPE_"+i), new String(r.getDomainId()));
                    String[] pointQ = new String[qLength];
                    for (int j = 0; j < qLength; j++) {
                        pointQ[j] = new String(r.getq()[j]);
                    }
                    double[] pointV = new double[qLength];
                    for (int j = 0; j < rowVals.length; j++) {
                        pointV[j] = Double.parseDouble(rowVals[j]);
                    }
                    points[i].setPoint(pointQ, pointV);
                }
            } catch (IOException e) {
                // TODO
                return null;
            }

            // delete qhull temp file
            if (f.exists())
                f.delete();
        }

        r.setVPolytope(points);
        return points;
    }

    // calculates v polytope for region r and stores the result in the r object
    // returns true if successful
    public boolean setVPolytope(Region r) {
        Point[] pts = vPolytope(r);
        if (pts == null) return false;
        else return true;
    }

    public boolean standardizeInstancesInDomain(String domainId){
        Domain d = model.getDomain(domainId);
        String[] qIds = d.getQualityDimensionIds();
        Instance[] instances = model.getInstances();

        ArrayList<ArrayList<Pair<String,Double>>> pointValues = new ArrayList<ArrayList<Pair<String,Double>>>();

        // create a table column for each quality dimension
        for (String qId : qIds) {
            pointValues.add(new ArrayList<Pair<String,Double>>());
        }

        for (Instance i : instances) {
            if (i.hasPointInDomain(domainId)) {
                for (int q = 0; q < qIds.length; q++) {
                    // add the quality dimension value for the point
                    pointValues.get(q).add(new Pair<String,Double>(i.getId(),i.getPoint(domainId).getValue(qIds[q])));
                }
            }
        }
        for (int q = 0; q < qIds.length; q++) {
            ArrayList<Pair<String,Double>> row = pointValues.get(q);
            ArrayList<Double> rowVals = new ArrayList<Double>();
            for (int i = 0; i < row.size(); i++) {
                rowVals.add(row.get(i).getSecond());
            }
            Pair<Double,Double> meanSD = MathFunc.meanStddev(rowVals);
            for (int i = 0; i < row.size(); i++) {
                double score = MathFunc.standardScore(rowVals, i, meanSD);
                Instance inst = model.getInstance(row.get(i).getFirst());
                Point p = inst.getPoint(domainId);
                // update the value of the point with the standard score value
                p.updateValue(qIds[q], score);
            }
        }

        return true;
    }

    public boolean standardizeInstancesInAllDomains() {
        Domain[] domains = model.getDomains();
        for (Domain d : domains) {
            standardizeInstancesInDomain(d.getId());
        }
        return true;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    private boolean equalWithinPrecision(double x, double y) {
        if (Math.abs(x - y) < roundPrecision)
            return true;
        else
            return false;
    }

        
    
    private class InterruptScheduler extends TimerTask
    {
        Thread target = null;
        public InterruptScheduler(Thread target) {
            this.target = target;
        }

        @Override
        public void run()
        {
            target.interrupt();
        }
    }
    
    
    
    
}
