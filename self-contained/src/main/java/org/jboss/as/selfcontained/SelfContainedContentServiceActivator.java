package org.jboss.as.selfcontained;

import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Bob McWhirter
 */
public class SelfContainedContentServiceActivator implements ServiceActivator {

    private final File content;

    public SelfContainedContentServiceActivator(File content) {
        this.content = content;
    }

    @Override
    public void activate(ServiceActivatorContext serviceActivatorContext) throws ServiceRegistryException {
        VirtualFile mountPoint = VFS.getRootVirtualFile().getChild( "ROOT.war" );
        try {
            VFS.mountReal( content, mountPoint );
            SelfContainedContentService service = new SelfContainedContentService( mountPoint );
            serviceActivatorContext.getServiceTarget().addService( SelfContainedContentService.NAME, service ).install();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceRegistryException(e);
        }

    }
}
