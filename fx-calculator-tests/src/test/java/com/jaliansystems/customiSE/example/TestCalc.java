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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.jaliansystems.customiSE.driver.CustomiSEDriver;
import com.jaliansystems.customiSE.driver.CustomiSEDriverService;
import com.jaliansystems.customiSE.example.Calc;
import com.jaliansystems.customiSE.example.CalculatorAgent;

public class TestCalc {

	private WebDriver driver;
	private HashMap<String, WebElement> elements;

	@Before
	public void setup() throws IOException {
		String javaExe = findJavaExe();
		if (javaExe != null)
			System.setProperty(CustomiSEDriverService.JAVA_EXE_PROPERTY, javaExe);
		System.out.println("TestCalc.setup(" + javaExe + ")");
		CustomiSEDriverService service = new CustomiSEDriverService.Builder()
				.withAgentImplementation(CalculatorAgent.class.getName())
				.addClasspathEntry(new File(findClassPathFor(CalculatorAgent.class)))
				.addClasspathEntry(new File(findClassPathFor(Calc.class)))
				.addJreProperty(CustomiSEDriverService.JAVA_EXE_PROPERTY, javaExe)
				.build();
		driver = new CustomiSEDriver(service);

		// @formatter:off
		String[] ops = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "c", "=", "/", "*", "-", "+",
				"value" };
		// @formatter:on
		elements = new HashMap<>();
		for (String op : ops) {
			elements.put(op, driver.findElement(By.name(op)));
		}
	}

	private String findJavaExe() {
		String exe = "java";
		if (Platform.getCurrent().is(Platform.WINDOWS))
			exe = "java.exe";
		File binDir = new File(System.getProperty("java.home"), "bin");
		if (binDir.isDirectory()) {
			File exeFile = new File(binDir, exe);
			if (exeFile.exists())
				try {
					return exeFile.getCanonicalPath();
				} catch (IOException e) {
				}
		}
		return null;
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
	public void testAdd() {
		click("2");
		click("+");
		click("2");
		click("=");
		assertEquals(4 + "", getValue());
	}

	@Test
	public void testSubstract() {
		click("2");
		click("-");
		click("2");
		click("=");
		assertEquals(0 + "", getValue());
	}

	@Test
	public void testMultiply() {
		click("2");
		click("*");
		click("2");
		click("=");
		assertEquals(4 + "", getValue());
	}

	@Test
	public void testDevide() {
		click("2");
		click("/");
		click("2");
		click("=");
		assertEquals(1 + "", getValue());
	}

	private String findClassPathFor(Class<?> class1) {
		String resourceName = findResource(class1.getName());
		URL resource = class1.getResource(resourceName);
		String path = resource.getPath();
		String cp = path.substring(0, path.length() - resourceName.length());
		if (cp.startsWith("file:"))
			cp = cp.substring("file:".length());
		if (cp.endsWith("!"))
			cp = cp.substring(0, cp.length() - 1);
		return cp;
	}

	private String findResource(String name) {
		return "/" + name.replace('.', '/') + ".class";
	}

	private String getValue() {
		return elements.get("value").getAttribute("value");
	}

	private void click(String op) {
		elements.get(op).click();
	}

}
