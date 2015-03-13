package org.jboss.as.selfcontained;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.persistence.ConfigurationPersistenceException;
import org.jboss.as.controller.persistence.ConfigurationPersister;
import org.jboss.as.controller.persistence.ExtensibleConfigurationPersister;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementWriter;

import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * @author Bob McWhirter
 */
public class SelfContainedConfigurationPersister implements ExtensibleConfigurationPersister {

    private final List<ModelNode> containerDefinition;

    public SelfContainedConfigurationPersister(List<ModelNode> containerDefinition) {
        this.containerDefinition = containerDefinition;
    }

    @Override
    public ConfigurationPersister.PersistenceResource store(ModelNode model, Set<PathAddress> affectedAddresses) throws ConfigurationPersistenceException {
        return new ConfigurationPersister.PersistenceResource() {
            @Override
            public void commit() {
            }

            @Override
            public void rollback() {
            }
        };
    }

    @Override
    public void marshallAsXml(ModelNode model, OutputStream output) throws ConfigurationPersistenceException {
    }

    @Override
    public List<ModelNode> load() throws ConfigurationPersistenceException {
        return this.containerDefinition;
    }

    @Override
    public void successfulBoot() throws ConfigurationPersistenceException {
    }

    @Override
    public String snapshot() throws ConfigurationPersistenceException {
        return null;
    }

    @Override
    public SnapshotInfo listSnapshots() {
        return null;
    }

    @Override
    public void deleteSnapshot(String name) {
    }

    @Override
    public void registerSubsystemWriter(String name, XMLElementWriter<SubsystemMarshallingContext> writer) {
    }

    @Override
    public void unregisterSubsystemWriter(String name) {
    }
}
