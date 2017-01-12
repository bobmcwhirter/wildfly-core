/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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
package org.jboss.as.controller.security;

import javax.security.auth.Destroyable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.AttributeMarshaller;
import org.jboss.as.controller.AttributeParser;
import org.jboss.as.controller.ObjectTypeAttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.as.controller.logging.ControllerLogger;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.value.InjectedValue;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.wildfly.common.function.ExceptionSupplier;
import org.wildfly.security.auth.SupportLevel;
import org.wildfly.security.credential.Credential;
import org.wildfly.security.credential.PasswordCredential;
import org.wildfly.security.credential.source.CommandCredentialSource;
import org.wildfly.security.credential.source.CredentialSource;
import org.wildfly.security.credential.source.CredentialStoreCredentialSource;
import org.wildfly.security.credential.store.CredentialStore;
import org.wildfly.security.password.interfaces.ClearPassword;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.StringTokenizer;

/**
 * Utility class holding attribute definitions for credential-reference attribute in the model.
 * The class is unifying access to credentials defined through {@link org.wildfly.security.credential.store.CredentialStore}.
 *
 * It defines credential-reference attribute that other subsystems can use to reference external credentials of various
 * types.
 *
 * @author <a href="mailto:pskopek@redhat.com">Peter Skopek</a>
 */
public final class CredentialReference implements Destroyable {

    /**
     * Required capability by credential-reference
     */
    public static final String CREDENTIAL_STORE_CAPABILITY = "org.wildfly.security.credential-store";
    /**
     * Definition of id used in model
     */
    public static final String CREDENTIAL_REFERENCE = "credential-reference";
    /**
     * Definition of id used in model
     */
    public static final String STORE = "store";
    /**
     * Definition of id used in model
     */
    public static final String ALIAS = "alias";
    /**
     * Definition of id used in model
     */
    public static final String TYPE = "type";
    /**
     * Definition of id used in model
     */
    public static final String CLEAR_TEXT = "clear-text";

    static final SimpleAttributeDefinition credentialStoreAttribute;
    static final SimpleAttributeDefinition credentialAliasAttribute;
    static final SimpleAttributeDefinition credentialTypeAttribute;
    static final SimpleAttributeDefinition clearTextAttribute;

    static final ObjectTypeAttributeDefinition credentialReferenceAttributeDefinition;

    static {
        credentialStoreAttribute = new SimpleAttributeDefinitionBuilder(STORE, ModelType.STRING, true)
                .setXmlName(STORE)
                .build();
        credentialAliasAttribute = new SimpleAttributeDefinitionBuilder(ALIAS, ModelType.STRING, true)
                .setXmlName(ALIAS)
                .build();
        credentialTypeAttribute = new SimpleAttributeDefinitionBuilder(TYPE, ModelType.STRING, true)
                .setXmlName(TYPE)
                .build();
        clearTextAttribute = new SimpleAttributeDefinitionBuilder(CLEAR_TEXT, ModelType.STRING, true)
                .setXmlName(CLEAR_TEXT)
                .build();
        credentialReferenceAttributeDefinition = getAttributeBuilder(CREDENTIAL_REFERENCE, CREDENTIAL_REFERENCE, false).build();
    }

    private CredentialReference() {
    }

    // utility static methods

    /**
     * Returns new definition for credential reference attribute.
     *
     * @return credential reference attribute definition
     */
    public static ObjectTypeAttributeDefinition getAttributeDefinition() {
        return credentialReferenceAttributeDefinition;
    }

    /**
     * Get the attribute builder for credential-reference attribute with specified characteristics.
     *
     * @param name name of attribute
     * @param xmlName name of xml element
     * @param allowNull whether the attribute is required
     * @return new {@link ObjectTypeAttributeDefinition.Builder} which can be used to build attribute definition
     */
    public static ObjectTypeAttributeDefinition.Builder getAttributeBuilder(String name, String xmlName, boolean allowNull) {
        return new ObjectTypeAttributeDefinition.Builder(name, credentialStoreAttribute, credentialAliasAttribute, credentialTypeAttribute, clearTextAttribute)
                .setXmlName(xmlName)
                .setAttributeMarshaller(credentialReferenceAttributeMarshaller())
                .setAttributeParser(credentialReferenceAttributeParser())
                .setAllowNull(allowNull);
    }

    /**
     * Utility method to return part of {@link ObjectTypeAttributeDefinition} for credential reference attribute.
     *
     * {@see CredentialReference#getAttributeDefinition}
     * @param context operational context
     * @param attributeDefinition attribute definition
     * @param model model
     * @param name name of part to return (supported names: {@link #STORE} {@link #ALIAS} {@link #TYPE}
     *    {@link #CLEAR_TEXT}
     * @return value of part as {@link String}
     * @throws OperationFailedException when something goes wrong
     */
    public static String credentialReferencePartAsStringIfDefined(OperationContext context, ObjectTypeAttributeDefinition attributeDefinition, ModelNode model, String name) throws OperationFailedException {
        ModelNode value = attributeDefinition.resolveModelAttribute(context, model);
        if (value.isDefined()) {
            ModelNode namedNode = value.get(name);
            if (namedNode != null && namedNode.isDefined()) {
                return namedNode.asString();
            }
            return null;
        }
        return null;
    }

    private static AttributeMarshaller credentialReferenceAttributeMarshaller() {
        return new AttributeMarshaller() {
            @Override
            public void marshallAsElement(AttributeDefinition attribute, ModelNode credentialReferenceModelNode, boolean marshallDefault, XMLStreamWriter writer) throws XMLStreamException {
                writer.writeStartElement(attribute.getXmlName());
                if (credentialReferenceModelNode.hasDefined(clearTextAttribute.getName())) {
                    clearTextAttribute.marshallAsAttribute(credentialReferenceModelNode, writer);
                } else {
                    credentialStoreAttribute.marshallAsAttribute(credentialReferenceModelNode, writer);
                    credentialAliasAttribute.marshallAsAttribute(credentialReferenceModelNode, writer);
                    credentialTypeAttribute.marshallAsAttribute(credentialReferenceModelNode, writer);
                }
                writer.writeEndElement();
            }

            @Override
            public boolean isMarshallableAsElement() {
                return true;
            }

        };
    }

    private static AttributeParser credentialReferenceAttributeParser() {
        return new AttributeParser() {
            @Override
            public void parseElement(AttributeDefinition attribute, XMLExtendedStreamReader reader, ModelNode operation) throws XMLStreamException {
                AttributeParser.OBJECT_PARSER.parseElement(attribute, reader, operation);
            }

            @Override
            public boolean isParseAsElement() {
                return true;
            }
        };
    }

    /**
     * Get the ExceptionSupplier of {@link CredentialSource} which might throw an Exception while getting it.
     * {@link CredentialSource} is used later to retrieve the credential requested by configuration.
     *
     * @param context operation context
     * @param credentialReferenceAttributeDefinition credential-reference attribute definition
     * @param model containing the actual values
     * @param serviceBuilder of service which needs the credential
     * @return ExceptionSupplier of CredentialSource
     * @throws OperationFailedException wrapping exception when something goes wrong
     */
    public static ExceptionSupplier<CredentialSource, Exception> getCredentialSourceSupplier(OperationContext context, ObjectTypeAttributeDefinition credentialReferenceAttributeDefinition, ModelNode model, ServiceBuilder<?> serviceBuilder) throws OperationFailedException {

        final String credentialStoreName = credentialReferencePartAsStringIfDefined(context, credentialReferenceAttributeDefinition, model, CredentialReference.STORE);
        final String credentialAlias = credentialReferencePartAsStringIfDefined(context, credentialReferenceAttributeDefinition, model, CredentialReference.ALIAS);
        final String credentialType = credentialReferencePartAsStringIfDefined(context, credentialReferenceAttributeDefinition, model, CredentialReference.TYPE);
        final String secret = credentialReferencePartAsStringIfDefined(context, credentialReferenceAttributeDefinition, model, CredentialReference.CLEAR_TEXT);

        final InjectedValue<CredentialStore> credentialStoreInjectedValue = new InjectedValue<>();
        if (credentialAlias != null) {
            // use credential store service
            String credentialStoreCapabilityName = RuntimeCapability.buildDynamicCapabilityName(CREDENTIAL_STORE_CAPABILITY, credentialStoreName);
            ServiceName credentialStoreServiceName = context.getCapabilityServiceName(credentialStoreCapabilityName, CredentialStore.class);
            serviceBuilder.addDependency(ServiceBuilder.DependencyType.REQUIRED, credentialStoreServiceName, CredentialStore.class, credentialStoreInjectedValue);
        }

        return new ExceptionSupplier<CredentialSource, Exception>() {

            private String[] parseCommand(String command) {
                // comma can be back slashed
                final String[] parsedCommand = command.split("(?<!\\\\),");
                for (int k = 0; k < parsedCommand.length; k++) {
                    if (parsedCommand[k].indexOf('\\') != -1)
                        parsedCommand[k] = parsedCommand[k].replaceAll("\\\\,", ",");
                }
                return parsedCommand;
            }

            private String stripType(String commandSpec) {
                StringTokenizer tokenizer = new StringTokenizer(commandSpec, "{}");
                tokenizer.nextToken();
                return tokenizer.nextToken();
            }

            /**
             * Gets a Credential Store Supplier.
             *
             * @return a supplier
             */
            @Override
            public CredentialSource get() throws Exception {
                if (credentialAlias != null) {
                    return new CredentialStoreCredentialSource(credentialStoreInjectedValue.getValue(), credentialAlias);
                } else if (credentialType != null && credentialType.equalsIgnoreCase("COMMAND")) {
                    // this is temporary solution until properly moved to WF-CORE
                    CommandCredentialSource.Builder command = CommandCredentialSource.builder();
                    String commandSpec = secret.trim();
                    if (commandSpec.startsWith("{EXT")) {
                        commandSpec = stripType(commandSpec);
                        command.addCommand(commandSpec);
                    } else if (commandSpec.startsWith("{CMD")) {
                        String[] parts = parseCommand(stripType(commandSpec));
                        for(String part: parts) {
                            command.addCommand(part);
                        }
                    }
                    return command.build();
                } else if (secret != null && secret.startsWith("MASK-")) {
                    if (credentialStoreName != null) {
                        return new CredentialStoreCredentialSource(credentialStoreInjectedValue.getValue(), secret.substring("MASK-".length()));
                    }
                    throw ControllerLogger.ROOT_LOGGER.nameOfCredentialStoreHasToBeSpecified();
                } else {
                    // clear text password
                    return new CredentialSource() {
                        @Override
                        public SupportLevel getCredentialAcquireSupport(Class<? extends Credential> credentialType, String algorithmName, AlgorithmParameterSpec parameterSpec) throws IOException {
                            return credentialType == PasswordCredential.class ? SupportLevel.SUPPORTED : SupportLevel.UNSUPPORTED;
                        }

                        @Override
                        public <C extends Credential> C getCredential(Class<C> credentialType, String algorithmName, AlgorithmParameterSpec parameterSpec) throws IOException {
                            return credentialType.cast(new PasswordCredential(ClearPassword.createRaw(ClearPassword.ALGORITHM_CLEAR, secret.toCharArray())));
                        }
                    };
                }
            }
        };
    }

}
