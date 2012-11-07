/**
 * Copyright 2012 Anjuke Inc.
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
 */
package com.anjuke.romar.http.jetty;

import org.eclipse.jetty.server.Server;

import com.anjuke.romar.core.RomarCore;
import com.anjuke.romar.core.RomarPathProcessFactory;

public final class RomarMain {
    private RomarMain() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage :  java classname $port");
            return;
        }
        RomarCore core = RomarPathProcessFactory.createCore();
        Server server = new Server(Integer.parseInt(args[0]));
        server.setHandler(new JettyRomarHandler(core));
        server.start();
        server.join();
    }
}
