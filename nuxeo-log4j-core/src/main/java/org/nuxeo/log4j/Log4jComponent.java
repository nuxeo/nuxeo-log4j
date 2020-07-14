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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Log4j registrations component
 */
public class Log4jComponent extends DefaultComponent {

    public static final String LOG4J_XP = "log4j";

    private static final Log log = LogFactory.getLog(Log4jComponent.class);

    public Map<String, Log4jDescriptor> getLog4jConfigs() {
        return log4jConfigs;
    }

    protected final Map<String, Log4jDescriptor> log4jConfigs = new HashMap<>();

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (LOG4J_XP.equals(extensionPoint)) {
            Log4jDescriptor descriptor = (Log4jDescriptor) contribution;
            log4jConfigs.put(descriptor.getName(), descriptor);
        }
    }

    @Override
    public void start(ComponentContext context) {
        super.start(context);
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        log4jConfigs.values().stream().filter(Log4jDescriptor::isEnabled).forEach(descriptor -> {
            descriptor.getLoggers().forEach(customLogger -> {
                LoggerConfig loggerConfig = LoggerConfig.createLogger(true, Level.getLevel(customLogger.getLevel()),
                        customLogger.getName(), null, new AppenderRef[] {}, null, config, null);
                config.addLogger(customLogger.getName(), loggerConfig);
            });
        });
        ctx.updateLoggers();
    }
}
