/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.alert.metric;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.eagle.alert.metric.sink.MetricSink;
import org.apache.eagle.alert.metric.sink.MetricSinkRepository;
import org.apache.eagle.alert.metric.source.MetricSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MetricSystem implements IMetricSystem {
    private final Config config;
    private Map<MetricSink, Config> sinks = new HashMap<>();
    private MetricRegistry registry = new MetricRegistry();
    private volatile boolean running;
    private volatile boolean initialized;
    private static final Logger LOG = LoggerFactory.getLogger(MetricSystem.class);
    private final Map<String, Object> metricTags = new HashMap<>();
    private int scheduleDurationSeconds = 10;

    public MetricSystem(Config config) {
        this.config = config;
        if (this.config.hasPath(MetricConfigs.DURATION_SECONDS_CONF)) {
            this.scheduleDurationSeconds = this.config.getInt(MetricConfigs.DURATION_SECONDS_CONF);
            LOG.info("Override {}: {}",MetricConfigs.DURATION_SECONDS_CONF, this.scheduleDurationSeconds);
        }
    }

    public static MetricSystem load(Config config) {
        MetricSystem instance = new MetricSystem(config);
        instance.loadFromConfig();
        return instance;
    }

    /**
     * Add additional tags.
     */
    @Override
    public void tags(Map<String, Object> metricTags) {
        this.metricTags.putAll(metricTags);
    }

    @Override
    public void start() {
        if (initialized) {
            throw new IllegalStateException("Attempting to initialize a MetricsSystem that is already initialized");
        }
        sinks.forEach((sink, conf) -> {
            sink.prepare(conf.withValue(MetricConfigs.TAGS_FIELD_NAME, ConfigFactory.parseMap(metricTags).root()), registry);
        });
        initialized = true;
    }

    @Override
    public void schedule() {
        if (running) {
            throw new IllegalStateException("Attempting to start a MetricsSystem that is already running");
        }
        sinks.keySet().forEach((sink) -> sink.start(this.scheduleDurationSeconds, TimeUnit.SECONDS));
        running = true;
    }

    public void loadFromConfig() {
        loadSinksFromConfig();
    }

    private void loadSinksFromConfig() {
        Config sinkCls = config.hasPath(MetricConfigs.METRIC_SINK_CONF) ? config.getConfig(MetricConfigs.METRIC_SINK_CONF) : null;
        if (sinkCls == null) {
            // do nothing
        } else {
            for (String sinkType : sinkCls.root().unwrapped().keySet()) {
                register(MetricSinkRepository.createSink(sinkType), config.getConfig(MetricConfigs.METRIC_SINK_CONF + "." + sinkType));
            }
        }
    }

    @Override
    public void stop() {
        sinks.keySet().forEach(MetricSink::stop);
    }

    @Override
    public void report() {
        sinks.keySet().forEach(MetricSink::report);
    }

    @Override
    public void register(MetricSink sink, Config config) {
        sinks.put(sink, config);
        LOG.info("Registered {}", sink);
    }

    @Override
    public void register(MetricSource source) {
        registry().registerAll(source.registry());
    }

    @Override
    public MetricRegistry registry() {
        return registry;
    }
}