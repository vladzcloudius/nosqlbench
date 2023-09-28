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

package io.nosqlbench.virtdata.library.basics.tests.long_double;

import io.nosqlbench.virtdata.library.basics.shared.from_long.to_double.HashRange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HashedDoubleRangeTest {

    @Test
    public void testBasicRanges() {
        HashRange r = new HashRange(0.0D, 100.0D);
        for(long i=1;i<1000;i++) {
            assertThat(r.applyAsDouble(i)).isBetween(0.0D,100.0D);
        }
    }

    @Test
    public void testNegativeAndPositive() {
        HashRange r = new HashRange(-1.0D, 1.0D);
        for (long i = 1; i<1000; i++) {
            double result = r.applyAsDouble(i);
//            System.out.println(result);
            assertThat(result).isBetween(-1.0d, 1.0d);
        }
    }


}
