package org.jboss.as.selfcontained;

import org.jboss.as.repository.ContentReference;
import org.jboss.as.repository.ContentRepository;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * @author Bob McWhirter
 */
public class SelfContainedContentRepository implements ContentRepository, Service<ContentRepository> {

    public static void addService(ServiceTarget serviceTarget) {
        SelfContainedContentRepository contentRepository = new SelfContainedContentRepository();
        serviceTarget.addService(SERVICE_NAME, contentRepository).install();
    }

    @Override
    public byte[] addContent(InputStream stream) throws IOException {
        System.err.println( "addContent" );
        return new byte[0];
    }

    @Override
    public void addContentReference(ContentReference reference) {
        System.err.println( "addContentReference: " + reference );
    }

    @Override
    public VirtualFile getContent(byte[] hash) {
        System.err.println( "getContent: " + hash );
        return null;
    }

    @Override
    public boolean hasContent(byte[] hash) {
        System.err.println( "hasContent: " + hash );
        return false;
    }

    @Override
    public boolean syncContent(ContentReference reference) {
        System.err.println( "syncContent: " + reference );
        return false;
    }

    @Override
    public void removeContent(ContentReference reference) {
        System.err.println( "removeContent: " + reference );

    }

    @Override
    public Map<String, Set<String>> cleanObsoleteContent() {
        System.err.println( "cleanObsoleteContent" );
        return null;
    }

    @Override
    public void start(StartContext startContext) throws StartException {

    }

    @Override
    public void stop(StopContext stopContext) {

    }

    @Override
    public ContentRepository getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

}
