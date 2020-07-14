/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
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
 *
 */
package org.nuxeo.log4j;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy("org.nuxeo.log4j.core")
@Deploy("org.nuxeo.log4j.core.tests:OSGI-INF/log4j-contrib-test.xml")
public class TestLog4j {

    @Inject
    Log4jComponent log4jComponent;

    @Test
    public void shouldContributeToLog4j() {
        assertThat(log4jComponent.getLog4jConfigs().size()).isEqualTo(2);
        assertThat(log4jComponent.getLog4jConfigs().get("ai-core").getLoggers().size()).isEqualTo(1);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        assertThat(ctx.getLogger("org.nuxeo.ai.cloud")).isNotNull();
        assertThat(ctx.getLogger("org.nuxeo.ai.cloud").getLevel()).isEqualTo(Level.DEBUG);
        // Checking that this logger is not taken into account (because disabled)
        assertThat(ctx.getLogger("org.nuxeo.ai")).isNotNull();
        assertThat(ctx.getLogger("org.nuxeo.ai").getLevel()).isEqualTo(Level.INFO);
    }
}
