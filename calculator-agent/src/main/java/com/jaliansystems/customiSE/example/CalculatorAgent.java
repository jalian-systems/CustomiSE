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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jaliansystems.customiSE.driver.CustomiSEDriverService;
import com.jaliansystems.customiSE.example.Calc;
import com.jaliansystems.customiSE.server.ExecuteMode;
import com.jaliansystems.customiSE.server.IJavaAgent;
import com.jaliansystems.customiSE.server.IJavaElement;
import com.jaliansystems.customiSE.server.IWindow;

public class CalculatorAgent implements IJavaAgent {

	private Process process;
	private BufferedReader reader;
	private PrintWriter writer;

	public CalculatorAgent() {
		int port = findPort();
		try {
			String javaExe = System.getProperty(CustomiSEDriverService.JAVA_EXE_PROPERTY, "java");
			String command = javaExe + " -classpath " + findClassPathFor(Calc.class) + " " + Calc.class.getName()
					+ " --port=" + port;
			System.out.println("CalculatorAgent.CalculatorAgent(" + command + ")");
			process = Runtime.getRuntime().exec(command);
			new Thread(new Runnable() {
				@Override
				public void run() {
					InputStream is = process.getInputStream();
					int b;
					try {
						while ((b = is.read()) != -1) {
							System.err.write(b);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			Socket connection = tryConnection(port);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static String findClassPathFor(Class<?> class1) {
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

	private static String findResource(String name) {
		return "/" + name.replace('.', '/') + ".class";
	}

	private Socket tryConnection(int port) throws InterruptedException, IOException {
		IOException e = null;
		for (int i = 0; i < 10; i++) {
			try {
				return new Socket("localhost", port);
			} catch (IOException e1) {
				e = e1;
				Thread.sleep(2000);
			}
		}
		throw e;
	}

	private int findPort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e1) {
			throw new RuntimeException("Could not allocate a port: " + e1.getMessage());
		} finally {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
				}
		}
	}

	@Override
	public Collection<String> getWindowHandles() {
		return Arrays.asList("MainWindow");
	}

	@Override
	public void deleteWindow() {
	}

	@Override
	public void switchToWindow(String name) {
	}

	@Override
	public void manageTimeoutsImplicitlyWait(long millis, TimeUnit milliseconds) {
	}

	@Override
	public String getWindowHandle() {
		return "MainWindow";
	}

	@Override
	public String getTitle() {
		return "Calculator";
	}

	@Override
	public IJavaElement findElementByName(String name) {
		if (name.length() > 1 && name.equals("value"))
			return new CalcValueField(reader, writer);
		else if (name.length() > 1)
			return null;
		char button = name.charAt(0);
		if ("0123456789c=/*-+".indexOf(button) != -1)
			return new CalcOperationField(reader, writer, button);
		return null;
	}

	@Override
	public IJavaElement findElementByTagName(String tagName) {
		return null;
	}

	@Override
	public IJavaElement findElementByCssSelector(String cssSelector) {
		return null;
	}

	@Override
	public IJavaElement findElementByClassName(String className) {
		return null;
	}

	@Override
	public IJavaElement findElementById(String id) {
		return findElementByName(id);
	}

	@Override
	public List<IJavaElement> findElementsByName(String name) {
		return Collections.emptyList();
	}

	@Override
	public List<IJavaElement> findElementsByTagName(String tagName) {
		return Collections.emptyList();
	}

	@Override
	public List<IJavaElement> findElementsByCssSelector(String cssSelector) {
		return Collections.emptyList();
	}

	@Override
	public List<IJavaElement> findElementsByClassName(String className) {
		return Collections.emptyList();
	}

	@Override
	public List<IJavaElement> findElementsById(String id) {
		return findElementsByName(id);
	}

	@Override
	public IJavaElement findElement(String name) {
		if (name.length() > 1 && name.equals("value"))
			return new CalcValueField(reader, writer);
		if (name.equals("div"))
			name = "/";
		if (name.length() > 1)
			return null;
		char button = name.charAt(0);
		if ("0123456789c=/*-+".indexOf(button) != -1)
			return new CalcOperationField(reader, writer, button);
		return null;
	}

	@Override
	public IJavaElement getActiveElement() {
		return null;
	}

	@Override
	public void quit() {
		writer.println("quit");
		writer.flush();
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IWindow getCurrentWindow() {
		return null;
	}

	@Override
	public IWindow getWindow(String windowHandle) {
		return null;
	}

	@Override
	public JSONObject getWindowProperties() {
		return new JSONObject();
	}

	@Override
	public byte[] getScreenShot() {
		return null;
	}

	@Override
	public String execute(String script, JSONArray params, ExecuteMode mode) {
		return null;
	}

}
