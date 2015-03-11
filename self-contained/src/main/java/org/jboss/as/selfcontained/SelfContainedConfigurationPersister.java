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
                System.err.println("embedded persist commit");
            }

            @Override
            public void rollback() {
                System.err.println("embedded persist rollback");
            }
        };
    }

    @Override
    public void marshallAsXml(ModelNode model, OutputStream output) throws ConfigurationPersistenceException {
        System.err.println( "marshall as xml: " + model );

    }

    @Override
    public List<ModelNode> load() throws ConfigurationPersistenceException {
        return this.containerDefinition;
    }

    @Override
    public void successfulBoot() throws ConfigurationPersistenceException {
        System.err.println("successful boot");

    }

    @Override
    public String snapshot() throws ConfigurationPersistenceException {
        System.err.println( "snapshot" );
        return null;
    }

    @Override
    public SnapshotInfo listSnapshots() {
        System.err.println( "list snapshots" );
        return null;
    }

    @Override
    public void deleteSnapshot(String name) {
        System.err.println( "delete snapshot: " + name );
    }

    @Override
    public void registerSubsystemWriter(String name, XMLElementWriter<SubsystemMarshallingContext> writer) {
        System.err.println("register subsystem writer: " + name);
    }

    @Override
    public void unregisterSubsystemWriter(String name) {
        System.err.println("unregister subsystem writer: " + name);
    }
}
