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
package com.jaliansystems.customiSE.server;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

public interface IJavaElement {

    IJavaElement findElementByName(String name);

    IJavaElement findElementByTagName(String tagName);

    IJavaElement findElementByCssSelector(String cssSelector);

    IJavaElement findElementByClassName(String className);

    IJavaElement findElementById(String id);

    List<IJavaElement> findElementsByName(String name);

    List<IJavaElement> findElementsByTagName(String tagName);

    List<IJavaElement> findElementsByCssSelector(String cssSelector);

    List<IJavaElement> findElementsByClassName(String className);

    List<IJavaElement> findElementsById(String id);

    String getHandle();

    String getTagName();

    Point getLocation();

    Dimension getSize();

    String getAttribute(String name);

    void clear();

    boolean isSelected();

    boolean isEnabled();

    boolean isDisplayed();

    Point getMidpoint();

    void moveto(int xoffset, int yoffset);

    void click(int mouseButton, int clickCount, int x, int y);

    void submit();

    void sendKeys(String[] keys);

    void buttonDown(int button, int xoffset, int yoffset);

    void buttonUp(int button, int xoffset, int yoffset);

}
