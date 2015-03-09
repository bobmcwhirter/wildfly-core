/*
* JBoss, Home of Professional Open Source.
* Copyright 2012, Red Hat Middleware LLC, and individual contributors
* as indicated by the @author tags. See the copyright.txt file in the
* distribution for a full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.as.server;

import org.jboss.as.controller.services.path.PathManagerService;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceTarget;

import java.io.File;

/**
 * Service containing the paths for a server
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class ServerPathManagerService extends PathManagerService {

    public static ServiceController<?> addService(ServiceTarget serviceTarget, ServerPathManagerService service, ServerEnvironment serverEnvironment) {
        ServiceBuilder<?> serviceBuilder = serviceTarget.addService(SERVICE_NAME, service);

        // Add environment paths
        addAbsolutePath(service, serviceTarget, ServerEnvironment.HOME_DIR, serverEnvironment.getHomeDir());
        addAbsolutePath(service, serviceTarget, ServerEnvironment.SERVER_BASE_DIR, serverEnvironment.getServerBaseDir());
        addAbsolutePath(service, serviceTarget, ServerEnvironment.SERVER_CONFIG_DIR, serverEnvironment.getServerConfigurationDir());
        addAbsolutePath(service, serviceTarget, ServerEnvironment.SERVER_DATA_DIR, serverEnvironment.getServerDataDir());
        addAbsolutePath(service, serviceTarget, ServerEnvironment.SERVER_LOG_DIR, serverEnvironment.getServerLogDir());
        addAbsolutePath(service, serviceTarget, ServerEnvironment.SERVER_TEMP_DIR, serverEnvironment.getServerTempDir());
        addAbsolutePath(service, serviceTarget, ServerEnvironment.CONTROLLER_TEMP_DIR, serverEnvironment.getControllerTempDir());

        // Add system paths
        service.addHardcodedAbsolutePath(serviceTarget, "user.dir", System.getProperty("user.dir"));
        service.addHardcodedAbsolutePath(serviceTarget, "user.home", System.getProperty("user.home"));
        service.addHardcodedAbsolutePath(serviceTarget, "java.home", System.getProperty("java.home"));

        // In the domain mode add a few more paths
        if (serverEnvironment.getLaunchType() == ServerEnvironment.LaunchType.DOMAIN) {
            addAbsolutePath(service, serviceTarget, ServerEnvironment.DOMAIN_BASE_DIR, serverEnvironment.getDomainBaseDir());
            addAbsolutePath(service, serviceTarget, ServerEnvironment.DOMAIN_CONFIG_DIR, serverEnvironment.getDomainConfigurationDir());
        }

        return serviceBuilder.install();
    }

    private static void addAbsolutePath(ServerPathManagerService service, ServiceTarget serviceTarget, String name, File path) {
        if (path == null) {
            return;
        }

        service.addHardcodedAbsolutePath(serviceTarget, name, path.getAbsolutePath());
    }

}
