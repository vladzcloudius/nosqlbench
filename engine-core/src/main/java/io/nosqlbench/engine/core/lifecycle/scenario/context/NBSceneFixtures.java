/*
 * Copyright (c) 2023 nosqlbench
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

package io.nosqlbench.engine.core.lifecycle.scenario.context;

import io.nosqlbench.components.NBComponent;
import io.nosqlbench.engine.core.lifecycle.scenario.execution.Extensions;

import java.io.PrintWriter;
import java.io.Reader;

public interface NBSceneFixtures {
    ScriptParams params();

    NBComponent component();

    ActivitiesController controller();

    Extensions extensions();

    PrintWriter out();

    PrintWriter err();

    Reader in();

    public static NBSceneFixtures NEW(String sceneName) {
        return NBDefaultSceneFixtures.ofDefault(sceneName);
    }

}
