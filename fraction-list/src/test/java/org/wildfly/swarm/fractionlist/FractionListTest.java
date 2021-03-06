/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.fractionlist;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.fest.assertions.ObjectAssert;
import org.junit.Test;
import org.wildfly.swarm.tools.FractionDescriptor;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Bob McWhirter
 */
public class FractionListTest {

    @Test
    public void testList() throws IOException {
        FractionList list = FractionList.get();

        Collection<FractionDescriptor> descriptors = list.getFractionDescriptors();

        FractionDescriptor logstash = descriptors.stream().filter(e -> e.getArtifactId().equals("logstash")).findFirst().get();

        assertThat(list.getFractionDescriptor("org.wildfly.swarm", "logstash")).isEqualTo(logstash);

        assertThat(logstash.getGroupId()).isEqualTo("org.wildfly.swarm");
        assertThat(logstash.getArtifactId()).isEqualTo("logstash");
        assertThat(logstash.getDependencies()).hasSize(2);

        assertThat(logstash.getDependencies().stream().filter(e -> e.getArtifactId().equals("container")).collect(Collectors.toList())).isNotEmpty();
        assertThat(logstash.getDependencies().stream().filter(e -> e.getArtifactId().equals("logging")).collect(Collectors.toList())).isNotEmpty();
    }

    @Test
    public void testMultipleGets() throws IOException {
        FractionList l1 = FractionList.get();
        FractionList l2 = FractionList.get();
        FractionList l3 = FractionList.get();

        assertThat(l1).isNotNull();
        assertThat(l1).isSameAs(l2);
        assertThat(l2).isSameAs(l3);
    }

    @Test
    public void testGroupIdAndArtifactIdAndNameAndDescriptionAreNeverNull() throws Exception {
        Collection<FractionDescriptor> descriptors = FractionList.get().getFractionDescriptors();
        assertThat(descriptors).onProperty("groupId").isNotNull();
        assertThat(descriptors).onProperty("artifactId").isNotNull();
        assertThat(descriptors).onProperty("version").isNotNull();
        assertThat(descriptors).onProperty("name").isNotNull();
        assertThat(descriptors).onProperty("description").isNotNull();
    }
    @Test
    public void testNameAndDescription() throws Exception {
        FractionDescriptor cdi = FractionList.get().getFractionDescriptor("org.wildfly.swarm", "cdi");
        assertThat(cdi.getName()).isEqualTo("CDI");
        assertThat(cdi.getDescription()).isEqualTo("CDI with Weld");
    }

    @Test
    public void testEEFractionDependsOnNamingAndContainer() {
        FractionDescriptor ee = FractionList.get().getFractionDescriptor("org.wildfly.swarm", "ee");
        FractionDescriptor naming = FractionList.get().getFractionDescriptor("org.wildfly.swarm", "naming");
        FractionDescriptor container = FractionList.get().getFractionDescriptor("org.wildfly.swarm", "container");
        Set<FractionDescriptor> dependencies = ee.getDependencies();
        assertThat(dependencies).contains(naming, container);
    }

}
