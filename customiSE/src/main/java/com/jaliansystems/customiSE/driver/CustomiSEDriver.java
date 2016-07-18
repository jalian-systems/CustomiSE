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

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverCommandExecutor;

import com.google.common.base.Throwables;

public class CustomiSEDriver extends RemoteWebDriver {

    /**
     * Port which is used by default.
     */
    private final static int DEFAULT_PORT = 0;

    public CustomiSEDriver(CustomiSEDriverService service) {
      this(service, null, DEFAULT_PORT);
    }

    public CustomiSEDriver(CustomiSEDriverService service, Capabilities capabilities) {
      this(service, capabilities, DEFAULT_PORT);
    }

    public CustomiSEDriver(CustomiSEDriverService service, Capabilities capabilities,
        int port) {
      if (capabilities == null) {
        capabilities = DesiredCapabilities.internetExplorer();
      }

      if (service == null) {
        service = setupService(capabilities, port);
      }
      run(service, capabilities);
    }

    private void run(CustomiSEDriverService service, Capabilities capabilities) {
      setCommandExecutor(new DriverCommandExecutor(service));

      startSession(capabilities);
    }

    @Override
    public void setFileDetector(FileDetector detector) {
      throw new WebDriverException(
          "Setting the file detector only works on remote webdriver instances obtained " +
          "via RemoteWebDriver");
    }

    public <X> X getScreenshotAs(OutputType<X> target) {
      // Get the screenshot as base64.
      String base64 = execute(DriverCommand.SCREENSHOT).getValue().toString();

      // ... and convert it.
      return target.convertFromBase64Png(base64);
    }

    private CustomiSEDriverService setupService(Capabilities caps, int port) {
      try {
        CustomiSEDriverService.Builder builder = new CustomiSEDriverService.Builder();
        builder.usingPort(port);

        return builder.build();

      } catch (IllegalStateException ex) {
        throw Throwables.propagate(ex);
      }
    }
}
