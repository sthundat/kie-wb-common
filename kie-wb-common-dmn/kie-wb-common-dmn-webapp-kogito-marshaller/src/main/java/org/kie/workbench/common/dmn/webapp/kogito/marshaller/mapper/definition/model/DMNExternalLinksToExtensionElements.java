/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinks;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITAttachment;

class DMNExternalLinksToExtensionElements {

    static void loadExternalLinksFromExtensionElements(final JSITDRGElement source,
                                                       final org.kie.workbench.common.dmn.api.definition.model.DRGElement target) {

        if (!Objects.isNull(source.getExtensionElements())) {
            final List<Object> extensions = source.getExtensionElements().getAny();
            if (!Objects.isNull(extensions)) {
                for (int i = 0; i < extensions.size(); i++) {
                    final Object extension = extensions.get(i);
                    if (JSITAttachment.instanceOf(extension)) {
                        final JSITAttachment jsiExtension = Js.uncheckedCast(extension);
                        final DMNExternalLink external = new DMNExternalLink();
                        external.setDescription(jsiExtension.getName());
                        external.setUrl(jsiExtension.getUrl());
                        target.getLinksHolder().getValue().addLink(external);
                    }
                }
            }
        }
    }

    static void loadExternalLinksIntoExtensionElements(final org.kie.workbench.common.dmn.api.definition.model.DRGElement source,
                                                       final JSITDRGElement target) {

        if (Objects.isNull(source.getLinksHolder()) || Objects.isNull(source.getLinksHolder().getValue())) {
            return;
        }

        final DocumentationLinks links = source.getLinksHolder().getValue();
        final JSITDMNElement.JSIExtensionElements elements = getOrCreateExtensionElements(target);

        removeAllExistingLinks(elements);

        for (final DMNExternalLink link : links.getLinks()) {
            final JSITAttachment attachment = new JSITAttachment();
            attachment.setName(link.getDescription());
            attachment.setUrl(link.getUrl());
            elements.getAny().add(attachment);
        }
        target.setExtensionElements(elements);
    }

    private static void removeAllExistingLinks(final JSITDMNElement.JSIExtensionElements elements) {
        final JSITDMNElement.JSIExtensionElements others = new JSITDMNElement.JSIExtensionElements();
        // TODO {gcardosi} add because present in original json
        others.setAny(new ArrayList<>());
        final List<Object> any = elements.getAny();
        for (int i = 0; i < any.size(); i++) {
            final Object extension = any.get(i);
            if (!JSITAttachment.instanceOf(extension)) {
                others.getAny().add(extension);
            }
        }
        elements.setAny(others.getAny());
    }

    private static JSITDMNElement.JSIExtensionElements getOrCreateExtensionElements(final JSITDRGElement target) {
        // TODO {gcardosi} add because  present in original json
        JSITDMNElement.JSIExtensionElements toReturn = target.getExtensionElements() == null
                ? new JSITDMNElement.JSIExtensionElements()
                : target.getExtensionElements();
        if (!Objects.isNull(toReturn) && Objects.isNull(toReturn.getAny())) {
            toReturn.setAny(new ArrayList<>());
        }
        return toReturn;
    }
}
