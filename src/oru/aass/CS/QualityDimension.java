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
public class QualityDimension extends Element{

    
    // attributes
    private boolean circular = false;

    // elements
    private Scale scale = new Scale();
    private Range range = new Range();
    private Units units = new Units();

    public QualityDimension(String id, String label, String description) {
        super(id, label, description);
    }

    public QualityDimension(String id) {
        super(id);
    }

    public boolean isCircular() { return circular; }

    public void setCircular(boolean circular) {
        this.circular = circular;
    }

    public void setScale(String s) {
        scale = new Scale(s);
    }

    public void setScale(int s) {
        scale = new Scale(s);
    }

    public int getScale() {
        return scale.getType();
    }

    public void setRange(double min, double max) {
        range = new Range(min, max);
    }

    public void setRange(Range r) {
        range = r;
    }

    public Range getRange() {
        return range;
    }

    public void setUnits(String u) {
        units = new Units(u);
    }

    public void setUnits(Units u) {
        units = new Units(u.getUnitsString());
    }

    public Units getUnits() {
        return units;
    }

       
    
}
