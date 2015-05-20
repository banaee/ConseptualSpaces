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
public class Element {
    
    
      
    // attributes
    private String id; // absolute or fragment URI

    // elements
    private String label;
    private String description;
    
    public Element(String id, String label, String description) {
        this.id = new String(id);
        this.label = new String(label);
        this.description = new String(description);
    }
    
    public Element(String id) {
        this.id = new String(id);
        label = "";
        description = "";
    }

    public void setId(String id) {
        this.id = new String(id);
    }

    public String getId() {
        return id;
    }
    
    public void setLabel(String label) {
        this.label = new String(label);
    }

    public String getLabel() {
        return label;
    }
    
    public void setDescription(String description) {
        this.description = new String(description);
    }

    public String getDescription() {
        return description;
    }
    
    
    
    
}
