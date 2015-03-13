package org.jboss.as.selfcontained;

import org.jboss.as.repository.ContentReference;
import org.jboss.as.repository.ContentRepository;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;
import org.jboss.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Bob McWhirter
 */
public class SelfContainedContentRepository implements ContentRepository, Service<ContentRepository> {

    public static void addService(ServiceTarget serviceTarget) {
        SelfContainedContentRepository contentRepository = new SelfContainedContentRepository();
        serviceTarget.addService(SERVICE_NAME, contentRepository)
                .addDependency( SelfContainedContentService.NAME, VirtualFile.class, contentRepository.getContentInjector() )
                .install();
    }

    private InjectedValue<VirtualFile> contentInjector = new InjectedValue<>();

    public Injector<VirtualFile> getContentInjector() {
        return this.contentInjector;
    }

    @Override
    public byte[] addContent(InputStream stream) throws IOException {
        return new byte[0];
    }

    @Override
    public void addContentReference(ContentReference reference) {
    }

    @Override
    public VirtualFile getContent(byte[] hash) {
        if ( hash.length == 1 && hash[0] == 0 ) {
            return this.contentInjector.getValue();
        }
        return null;
    }

    @Override
    public boolean hasContent(byte[] hash) {
        return false;
    }

    @Override
    public boolean syncContent(ContentReference reference) {
        return true;
    }

    @Override
    public void removeContent(ContentReference reference) {

    }

    @Override
    public Map<String, Set<String>> cleanObsoleteContent() {
        HashMap<String, Set<String>> result = new HashMap<>();
        result.put( ContentRepository.MARKED_CONTENT, Collections.<String>emptySet());
        result.put( ContentRepository.DELETED_CONTENT, Collections.<String>emptySet());
        return result;
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
