/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.jaliansystems.customiSE.example;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import com.jaliansystems.customiSE.server.IJavaElement;

public class CalcValueField implements IJavaElement {

    private BufferedReader reader;
    private PrintWriter writer;

    public CalcValueField(BufferedReader reader, PrintWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override public IJavaElement findElementByName(String name) {
        return null;
    }

    @Override public IJavaElement findElementByTagName(String tagName) {
        return null;
    }

    @Override public IJavaElement findElementByCssSelector(String cssSelector) {
        return null;
    }

    @Override public IJavaElement findElementByClassName(String className) {
        return null;
    }

    @Override public IJavaElement findElementById(String id) {
        return null;
    }

    @Override public List<IJavaElement> findElementsByName(String name) {
        return Collections.emptyList();
    }

    @Override public List<IJavaElement> findElementsByTagName(String tagName) {
        return Collections.emptyList();
    }

    @Override public List<IJavaElement> findElementsByCssSelector(String cssSelector) {
        return Collections.emptyList();
    }

    @Override public List<IJavaElement> findElementsByClassName(String className) {
        return Collections.emptyList();
    }

    @Override public List<IJavaElement> findElementsById(String id) {
        return Collections.emptyList();
    }

    @Override public String getHandle() {
        return "value";
    }

    @Override public String getTagName() {
        return null;
    }

    @Override public Point getLocation() {
        return null;
    }

    @Override public Dimension getSize() {
        return null;
    }

    @Override public String getAttribute(String name) {
        if(name.equals("value")) {
            writer.println("value");
            writer.flush();
            try {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override public void clear() {
    }

    @Override public boolean isSelected() {
        return false;
    }

    @Override public boolean isEnabled() {
        return false;
    }

    @Override public boolean isDisplayed() {
        return false;
    }

    @Override public Point getMidpoint() {
        return new Point();
    }

    @Override public void moveto(int xoffset, int yoffset) {
    }

    @Override public void click(int mouseButton, int clickCount, int x, int y) {
    }

    @Override public void submit() {
    }

    @Override public void sendKeys(String[] keys) {
    }

    @Override public void buttonDown(int button, int xoffset, int yoffset) {
    }

    @Override public void buttonUp(int button, int xoffset, int yoffset) {
    }

}
