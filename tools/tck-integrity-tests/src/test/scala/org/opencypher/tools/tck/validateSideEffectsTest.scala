/*
 * Copyright (c) 2015-2021 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
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
 * Attribution Notice under the terms of the Apache License 2.0
 *
 * This work was created by the collective efforts of the openCypher community.
 * Without limiting the terms of Section 6, any Derivative Work that is not
 * approved by the public consensus process of the openCypher Implementers Group
 * should not be described as “Cypher” (and Cypher® is a registered trademark of
 * Neo4j Inc.) or as "openCypher". Extensions by implementers or prototypes or
 * proposals for change that have been documented or implemented should only be
 * described as "implementation extensions to Cypher" or as "proposed changes to
 * Cypher that are not yet approved by the openCypher community".
 */
package org.opencypher.tools.tck

class validateSideEffectsTest extends TckTestSupport {

  test("should accept valid side effects") {
    validateSideEffects(tableOf(Seq("+nodes", "1"), Seq("-relationships", "2"))) shouldBe None
  }

  test("should report invalid side effects") {
    val sideEffects = tableOf(Seq("+blargh", "1"), Seq("-relationships", "notanumber"))

    validateSideEffects(sideEffects) shouldBe Some(
        """Invalid side effect keys: +blargh
          |Invalid side effect values: notanumber""".stripMargin)
  }

  test("should allow all elements") {
    val allKeys = Seq("+nodes", "-nodes", "+relationships", "-relationships",
                      "+labels", "-labels", "+properties", "-properties")
    validateSideEffects.checkKeys(allKeys) shouldBe empty
  }

}
