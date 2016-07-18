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
package com.jaliansystems.customiSE.driver;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Beta;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.service.DriverService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CustomiSEDriverService extends DriverService {

    /**
     * System property that defines the location of the Java executable
     * executable that will be used by the {@link #createDefaultService()
     * default service}.
     */
    public static final String JAVA_EXE_PROPERTY = "webdriver.customise.java.exe";

    /**
     * System property that defines the implementation of the driver engine to
     * use.
     */
    public static final String AGENT_IMPLEMENTATION_PROPERTY = "webdriver.ie.driver.engine";

    /**
     *
     * @param executable The IEDriverServer executable.
     * @param port Which port to start the IEDriverServer on.
     * @param args The arguments to the launched server.
     * @param environment The environment for the launched server.
     * @throws IOException If an I/O error occurs.
     */
    private CustomiSEDriverService(File executable, int port, ImmutableList<String> args,
                                          ImmutableMap<String, String> environment) throws IOException {
      super(executable, port, args, environment);
    }

    /**
     * Configures and returns a new {@link InternetExplorerDriverService} using
     * the default configuration. In this configuration, the service will use
     * the IEDriverServer executable identified by the
     * {@link #JAVA_EXE_PROPERTY} system property. Each service created by
     * this method will be configured to use a free port on the current system.
     *
     * @return A new InternetExplorerDriverService using the default
     *         configuration.
     */
    public static CustomiSEDriverService createDefaultService() {
        return new Builder().usingAnyFreePort().build();
    }

    /**
     * Builder used to configure new {@link InternetExplorerDriverService}
     * instances.
     */
    public static class Builder {

        private int port = 0;
        private File exe = null;
        private ImmutableMap<String, String> environment = ImmutableMap.of();
        private String agentImplementation;
        private List<String> jreArgs = new ArrayList<>();
        private List<String> classpath = new ArrayList<>();

        /**
         * Sets which driver executable the builder will use.
         *
         * @param file
         *            The executable to use.
         * @return A self reference.
         */
        public Builder usingDriverExecutable(File file) {
            checkNotNull(file);
            checkExecutable(file);
            this.exe = file;
            return this;
        }

        /**
         * Sets which port the driver server should be started on. A value of 0
         * indicates that any free port may be used.
         *
         * @param port
         *            The port to use; must be non-negative.
         * @return A self reference.
         */
        public Builder usingPort(int port) {
            checkArgument(port >= 0, "Invalid port number: %d", port);
            this.port = port;
            return this;
        }

        /**
         * Configures the driver server to start on any available port.
         *
         * @return A self reference.
         */
        public Builder usingAnyFreePort() {
            this.port = 0;
            return this;
        }

        /**
         * Defines the environment for the launched driver server. These
         * settings will be inherited by every browser session launched by the
         * server.
         *
         * @param environment
         *            A map of the environment variables to launch the server
         *            with.
         * @return A self reference.
         */
        @Beta public Builder withEnvironment(Map<String, String> environment) {
            this.environment = ImmutableMap.copyOf(environment);
            return this;
        }

        /**
         * Configures the Java executable to use for the driver.
         *
         * @param agentImplementation
         *            The agent implementation to be used.
         * @return A self reference.
         */
        public Builder withAgentImplementation(String agentImplementation) {
            this.agentImplementation = agentImplementation;
            return this;
        }

        /**
         * Adds a JRE property to the driver.
         * 
         * @param property
         *           The java property
         * @param value
         *           The property value 
         */
        public Builder addJreProperty(String property, String value) {
            this.jreArgs.add(String.format("-D%s=%s", property, value));
            return this;
        }
        
        /**
         * Adds a class path entry to the driver.
         * 
         * @param entry
         *          The folder/jar file
         * @return A self reference
         * @throws IOException 
         */
        public Builder addClasspathEntry(File entry) throws IOException {
            this.classpath.add(entry.getCanonicalPath());
            return this;
        }
        
        /**
         * Adds a JRE argument.
         * 
         * @param argument
         *          The java argument
         * @return A self reference
         */
        public Builder addJreArgument(String argument) {
            this.jreArgs.add(argument);
            return this;
        }
        
        /**
         * Creates a new service to manage the driver server. Before creating a
         * new service, the builder will find a port for the server to listen
         * to.
         *
         * @return The new service object.
         */
        public CustomiSEDriverService build() {
            if (port == 0) {
                port = PortProber.findFreePort();
            }
            if (exe == null) {
                exe = findExecutable("java", JAVA_EXE_PROPERTY,
                        "http://code.google.com/p/selenium/wiki",
                        "http://selenium-release.storage.googleapis.com/index.html");
            }
            if (agentImplementation == null) {
                String agentToUse = System.getProperty(AGENT_IMPLEMENTATION_PROPERTY);
                if (agentToUse != null) {
                    agentImplementation = agentToUse;
                }
            }
            try {
                ImmutableList.Builder<String> argsBuilder = ImmutableList.builder();
                if (agentImplementation != null) {
                    argsBuilder.add(String.format("-Dcustom.selenium.driver.agent=%s", agentImplementation));
                } else {
                    throw new RuntimeException("You should provide an agent implementation.");
                }
                StringBuilder sb = new StringBuilder();
                sb.append(System.getProperty("java.class.path"));
                for (String cp : classpath) {
                    sb.append(File.pathSeparatorChar);
                    sb.append(cp);
                }
                argsBuilder.add("-classpath");
                argsBuilder.add(sb.toString());
                for (String jreArg : jreArgs) {
                    argsBuilder.add(jreArg);
                }
                argsBuilder.add("com.jaliansystems.customiSE.Main");
                argsBuilder.add(String.format("%d", port));
                return new CustomiSEDriverService(exe, port, argsBuilder.build(), environment);

            } catch (IOException e) {
                throw new WebDriverException(e);
            }
        }
    }
}
