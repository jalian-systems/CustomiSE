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
package com.jaliansystems.customiSE;

import java.io.IOException;
import java.net.ServerSocket;

import com.jaliansystems.customiSE.server.JavaServer;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port;

        if (args.length >= 1)
            port = Integer.parseInt(args[0]);
        else
            port = findPort();

        System.err.println("Listening on port " + port + " for connections.");
        JavaServer javaServer = new JavaServer(port);
        javaServer.start();
        while (true)
            ;
    }

    private static int findPort() {
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

}
