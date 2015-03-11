package org.jboss.as.selfcontained;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.vfs.VirtualFile;

/**
 * @author Bob McWhirter
 */
public class SelfContainedContentService implements Service<VirtualFile> {

    public static final ServiceName NAME = ServiceName.JBOSS.append( "self-contained", "content" );
    private final VirtualFile content;

    public SelfContainedContentService(VirtualFile content) {
        System.err.println( "CONTENT IS: " + content + " // " + content.getPathName() );
        this.content = content;
    }

    @Override
    public void start(StartContext startContext) throws StartException {

    }

    @Override
    public void stop(StopContext stopContext) {

    }

    @Override
    public VirtualFile getValue() throws IllegalStateException, IllegalArgumentException {
        return this.content;
    }
}
