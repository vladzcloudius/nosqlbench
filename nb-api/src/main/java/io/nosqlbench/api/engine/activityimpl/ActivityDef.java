/*
 * Copyright (c) 2022-2023 nosqlbench
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

package io.nosqlbench.api.engine.activityimpl;

import io.nosqlbench.components.NBNamedElement;
import io.nosqlbench.api.errors.BasicError;
import io.nosqlbench.api.labels.NBLabelSpec;
import io.nosqlbench.api.labels.NBLabels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>A runtime definition for an activity.</p>
 * <p>Instances of ActivityDef hold control values for the execution of a single activity.
 * Each thread of the related activity is initialized with the associated ActivityDef.
 * When the ActivityDef is modified, interested activity threads are notified so that
 * they can dynamically adjust.</p>
 * <p>The canonical values for all parameters are kept internally in the parameter map.
 * Essentially, ActivityDef is just a type-aware wrapper around a thread-safe parameter map,
 * with an atomic change counter which can be used to signal changes to observers.</p>
 */
public class ActivityDef implements NBNamedElement {

    // milliseconds between cycles per thread, for slow tests only
    public static final String DEFAULT_ALIAS = "UNNAMEDACTIVITY";
    public static final String DEFAULT_ATYPE = "stdout";
    public static final String DEFAULT_CYCLES = "0";
    public static final String DEFAULT_RECYCLES = "1";
    public static final int DEFAULT_THREADS = 1;
    public static final Logger logger = LogManager.getLogger(ActivityDef.class);
    // an alias with which to control the activity while it is running
    public static final String FIELD_ALIAS = "alias";
    // a file or URL containing the activity: op templates, generator bindings, ...
    public static final String FIELD_ATYPE = "type";
    // cycles for this activity in either "M" or "N..M" form. "M" form implies "0..M"
    public static final String FIELD_CYCLES = "cycles";
    public static final String FIELD_RECYCLES = "recycles";
    // initial thread concurrency for this activity
    public static final String FIELD_THREADS = "threads";
    public static final String[] field_list = {
            FIELD_ALIAS, FIELD_ATYPE, FIELD_CYCLES, FIELD_THREADS, FIELD_RECYCLES
    };
    // parameter map has its own internal atomic map
    private final ParameterMap parameterMap;
    private CyclesSpec cyclesSpec;
    private CyclesSpec reCyclesSpec;

    public ActivityDef(ParameterMap parameterMap) {
        this.parameterMap = parameterMap;
    }

    public static Optional<ActivityDef> parseActivityDefOptionally(String namedActivitySpec) {
        try {
            ActivityDef activityDef = parseActivityDef(namedActivitySpec);
            return Optional.of(activityDef);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static ActivityDef parseActivityDef(String namedActivitySpec) {
        Optional<ParameterMap> activityParameterMap = ParameterMap.parseParams(namedActivitySpec);
        ActivityDef activityDef = new ActivityDef(activityParameterMap.orElseThrow(
                () -> new RuntimeException("Unable to parse:" + namedActivitySpec)
        ));
        logger.info("parsed activityDef {} to-> {}", namedActivitySpec, activityDef);

        return activityDef;
    }

    public String toString() {
        return "ActivityDef:" + parameterMap.toString();
    }

    /**
     * The alias that the associated activity instance is known by.
     *
     * @return the alias
     */
    public String getAlias() {
        return parameterMap.getOptionalString("alias").orElse(DEFAULT_ALIAS);
    }

    public String getActivityType() {
        return parameterMap.getOptionalString("type", "driver").orElse(DEFAULT_ATYPE);
    }

    /**
     * The first cycle that will be used for execution of this activity, inclusive.
     * If the value is provided as a range as in 0..10, then the first number is the start cycle
     * and the second number is the end cycle +1. Effectively, cycle ranges
     * are [closed,open) intervals, as in [min..max)
     *
     * @return the long start cycle
     */
    public long getStartCycle() {
        return getCyclesSpec().first_inclusive();
    }

    public void setStartCycle(long firstCycleInclusive) {
        cyclesSpec=getCyclesSpec().withFirst(firstCycleInclusive);
    }

    public void setStartCycle(String firstCycleInclusive) {
        cyclesSpec=getCyclesSpec().withFirst(firstCycleInclusive);
    }

    public void setEndCycle(long lastCycleExclusive) {
        cyclesSpec=getCyclesSpec().withLast(lastCycleExclusive);
    }
    public void setEndCycle(String lastCycleExclusive) {
        cyclesSpec=getCyclesSpec().withLast(lastCycleExclusive);
    }

    /**
     * The last cycle that will be used for execution of this activity, inclusive.
     *
     * @return the long end cycle
     */
    public long getEndCycle() {
        return getCyclesSpec().last_exclusive();
    }

    /**
     * The number of threads (AKA slots) that the associated activity should currently be using.
     *
     * @return target thread count
     */
    public int getThreads() {
        return parameterMap.getOptionalInteger(FIELD_THREADS).orElse(DEFAULT_THREADS);
    }

    public void setThreads(int threads) {
        parameterMap.set(FIELD_THREADS, threads);
    }

    /**
     * Get the parameter map, which is the backing-store for all data within an ActivityDef.
     *
     * @return the parameter map
     */
    public ParameterMap getParams() {
        return parameterMap;
    }

    public AtomicLong getChangeCounter() {
        return parameterMap.getChangeCounter();
    }

    public void setCycles(String cycles) {
        parameterMap.set(FIELD_CYCLES, cycles);
        this.cyclesSpec=CyclesSpec.parse(cycles);
        checkInvariants();
    }

    public String getCycleSummary() {
        return getCyclesSpec().summary();
    }

    public synchronized long getCycleCount() {
        return getCyclesSpec().cycle_count();
    }

    public synchronized CyclesSpec getCyclesSpec() {
        if (this.cyclesSpec==null) {
            this.cyclesSpec = CyclesSpec.parse(parameterMap.getOptionalString(FIELD_CYCLES).orElse(DEFAULT_CYCLES));
        }
        return this.cyclesSpec;
    }
    public synchronized CyclesSpec getRecyclesSpec() {
        if (this.reCyclesSpec==null) {
            this.reCyclesSpec = CyclesSpec.parse(parameterMap.getOptionalString(FIELD_RECYCLES).orElse(DEFAULT_RECYCLES));
        }
        return this.reCyclesSpec;

    }


    private void checkInvariants() {
        if (getStartCycle() >= getEndCycle()) {
            throw new InvalidParameterException("Start cycle must be strictly less than end cycle, but they are [" + getStartCycle() + ',' + getEndCycle() + ')');
        }
    }

    @Override
    public String getName() {
        return getAlias();
    }

    public ActivityDef deprecate(String deprecatedName, String newName) {
        Object deprecatedParam = this.parameterMap.get(deprecatedName);
        if (null == deprecatedParam) {
            return this;
        }
        if (deprecatedParam instanceof CharSequence chars) {
            if (this.parameterMap.containsKey(newName)) {
                throw new BasicError("You have specified activity param '" + deprecatedName + "' in addition to the valid name '" + newName + "'. Remove '" + deprecatedName + "'.");
            }
            logger.warn("Auto replacing deprecated activity param '{}={}' with new '{}={}'.", deprecatedName, chars, newName, chars);
            parameterMap.put(newName, parameterMap.remove(deprecatedName));
        } else {
            throw new BasicError("Can't replace deprecated name with value of type " + deprecatedName.getClass().getCanonicalName());
        }
        return this;
    }

    public NBLabels auxLabels() {
        Optional<String> auxLabelSpec = getParams().getOptionalString("labels");
        if (auxLabelSpec.isPresent()) {
            return NBLabelSpec.parseLabels(auxLabelSpec.get());
        }
        return NBLabels.forKV();

    }
}
