/*
 * Copyright (c) 2016. Trevor Jones
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

package com.trevjonez.agrp.rx

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import rx.Observable

/**
 * @author TrevJonez
 */
class CartesianProductKtTest {

  @Test
  fun cartesianProduct() {
    val letters = Observable.just("A", "B", "C")
    val numbers = Observable.just("1", "2", "3")

    val cProduct = cartesianProduct(letters, numbers)
            .stringConcatMap()
            .toList()
            .toBlocking()
            .single()

    assertThat(cProduct).containsExactly("A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3")
  }

  @Test
  fun cartesianProduct1() {
    val letters = Observable.just("A", "B")
    val numbers = Observable.just("1", "2")
    val symbols = Observable.just("@", "#")

    val cProduct = cartesianProduct(Observable.just(letters, numbers, symbols))
            .stringConcatMap()
            .toList()
            .toBlocking()
            .single()

    assertThat(cProduct).containsExactly("A1@", "A1#", "A2@", "A2#", "B1@", "B1#", "B2@", "B2#")
  }

}