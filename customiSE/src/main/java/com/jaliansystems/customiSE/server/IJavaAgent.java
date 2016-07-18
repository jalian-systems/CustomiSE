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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IJavaAgent {

    Collection<String> getWindowHandles();

    void deleteWindow();

    void switchToWindow(String name);

    void manageTimeoutsImplicitlyWait(long millis, TimeUnit milliseconds);

    String getWindowHandle();

    String getTitle();

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

    IJavaElement findElement(String id);

    IJavaElement getActiveElement();

    void quit();

    IWindow getCurrentWindow();

    IWindow getWindow(String windowHandle);

    JSONObject getWindowProperties();

    byte[] getScreenShot();

    String execute(String script, JSONArray params, ExecuteMode mode);

}
