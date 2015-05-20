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

package oru.aass.CS.utils;

import java.util.ArrayList;

public class MathFunc {
    ArrayList<String> s = null;

    // returns the standard deviation of an ArrayList of doubles
    public static double stddev(ArrayList<Double> data) {
        double sigma = 0.0;
        int ds = data.size();

        if (ds > 1) {
            double mk_ = data.get(0);
            double mk;
            double sk_ = 0.0;
            double sk = 0.0;

            for (int k = 1; k < ds; k++) {
                mk = mk_ + (data.get(k) - mk_) / (k + 1);
                sk = sk_ + (data.get(k) - mk_) * (data.get(k) - mk);

                mk_ = mk;
                sk_ = sk;
            }

            sigma = Math.sqrt(sk / ds);
        }

        return sigma;
    }

    public static Pair<Double,Double> meanStddev(ArrayList<Double> data) {
        Pair<Double,Double> returnVals;
        double sigma = 0.0;
        int ds = data.size();

        if (ds > 1) {
            double mk_ = data.get(0);
            double mk = 0.0;
            double sk_ = 0.0;
            double sk = 0.0;

            for (int k = 1; k < ds; k++) {
                mk = mk_ + (data.get(k) - mk_) / (k + 1);
                sk = sk_ + (data.get(k) - mk_) * (data.get(k) - mk);

                mk_ = mk;
                sk_ = sk;
            }

            sigma = Math.sqrt(sk / ds);
            returnVals = new Pair<Double,Double>(mk, sigma);
        } else if (ds == 1) {
            returnVals = new Pair<Double,Double>(data.get(0), 0.0);
        } else {
            returnVals = new Pair<Double,Double>(0.0, 0.0);
        }

        return returnVals;
    }

    // returns the mean of an ArrayList of doubles
    public static double mean(ArrayList<Double> data) {
        int ds = data.size();

        if (ds == 0)
            return 0.0;
        else if (ds == 1)
            return data.get(0);
        else {
            double mk = data.get(0);

            for (int k = 1; k < ds; k++) {
                mk = mk + (data.get(k) - mk) / (k + 1);
            }
            return mk;
        }
    }

    // returns the z-score of an observation in a data-set
    public static double zscore(ArrayList<Double> data, int i) {
        if (i >= data.size())
            throw new IndexOutOfBoundsException();
        else {
            Pair<Double,Double> ms = meanStddev(data);
            return (data.get(i) - ms.getFirst()) / ms.getSecond();
        }
    }

    // returns the z-score of an observation in a data-set with
    //  pre-calculated mean and standard deviation
    public static double zscore(ArrayList<Double> data, int i, Pair<Double,Double> ms) {
        if (i >= data.size())
            throw new IndexOutOfBoundsException();
        else {
            return (data.get(i) - ms.getFirst()) / ms.getSecond();
        }
    }

    public static double standardScore(ArrayList<Double> data, int i) {
        return zscore(data, i);
    }

    public static double standardScore(ArrayList<Double> data, int i, Pair<Double,Double> ms) {
        return zscore(data, i, ms);
    }

    public static double distance(ArrayList<Double> data1, ArrayList<Double> data2) {
        double answer = 0.0;
        if (data1.size() < data2.size()) {
            for (int i = 0; i < data1.size(); i++) {
                answer += ((data2.get(i) - data1.get(i)) * ((data2.get(i)) - data1.get(i)));
            }
            for (int i = data1.size(); i < data2.size(); i++) {
                answer += ((data2.get(i) - 0) * ((data2.get(i)) - 0));
            }
        } else if (data1.size() > data2.size()) {
            for (int i = 0; i < data2.size(); i++) {
                answer += ((data2.get(i) - data1.get(i)) * ((data2.get(i)) - data1.get(i)));
            }
            for (int i = data2.size(); i < data1.size(); i++) {
                answer += ((data1.get(i) - 0) * ((data1.get(i)) - 0));
            }
        } else if (data1.size() == data2.size()) {
            for (int i = 0; i < data1.size(); i++) {
                answer += ((data2.get(i) - data1.get(i)) * ((data2.get(i)) - data1.get(i)));
            }
        }
        return Math.sqrt(answer);
    }
}
