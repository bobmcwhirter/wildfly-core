package org.jboss.as.selfcontained;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.persistence.ConfigurationPersistenceException;
import org.jboss.as.controller.persistence.ConfigurationPersister;
import org.jboss.as.controller.persistence.ExtensibleConfigurationPersister;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementWriter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Bob McWhirter
 */
public class SelfContainedConfigurationPersister implements ExtensibleConfigurationPersister {

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
        List<ModelNode> list = new ArrayList<>();

        /*
        ModelNode address = new ModelNode().setEmptyList();
        address.setEmptyList();

        ModelNode add = new ModelNode();
        add.get(OP_ADDR).set(address).add(EXTENSION, "org.jboss.as.logging");
        add.get(OP).set(ADD);
        list.add(add);

        PathAddress loggingAddress = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, "logging") );

        ModelNode subsys = new ModelNode();
        subsys.get( OP_ADDR ).set( loggingAddress.toModelNode() );
        subsys.get(OP).set(ADD);
        list.add( subsys );

        ModelNode pattern = new ModelNode();
        pattern.get( OP_ADDR ).set( loggingAddress.append( "pattern-formatter", "PATTERN" ).toModelNode() );
        pattern.get(OP).set(ADD);
        pattern.get( "pattern" ).set( "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n" );
        list.add( pattern );


        ModelNode console = new ModelNode();
        console.get( OP_ADDR ).set( loggingAddress.append( "console-handler", "CONSOLE" ).toModelNode() );
        console.get( OP ).set( ADD );
        console.get( "level" ).set( "INFO" );
        console.get( "named-formatter" ).set( "PATTERN" );
        list.add(console);

        ModelNode root = new ModelNode();
        root.get( OP_ADDR ).set( loggingAddress.append( "root-logger", "ROOT" ).toModelNode() );
        root.get( OP ).set(ADD );
        root.get( "handlers" ).add("CONSOLE" );
        list.add( root );

        System.err.println( "OPS: : : " + list );

        //profile.get(OP).setEmptyList();

        //address.add(profile);

        final PathAddress loggingSubsystem = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, "logging") );

        ModelNode console = Util.createAddOperation();
        console.get(OP_ADDR).set( loggingSubsystem.append( "console-handler", "console" ).toModelNode() );
        console.get( "enabled" ).set( true );

        profile.add( console );

        //list.add( profile );
        */

        return list;
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
