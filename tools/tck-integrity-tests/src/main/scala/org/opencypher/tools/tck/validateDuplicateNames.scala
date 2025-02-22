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

import java.net.URI

import scala.collection.JavaConverters._

/**
  * This function will validate whether a TCK scenario an unambiguous name. The scenario name is required to be unique within a feature file,
  * except for scenarios resulting from a Scenario Outline. If a scenario name is found to be ambiguous a message will be returned providing the
  * name and the scenarios causing it's ambiguity, otherwise None will be returned.
  */
object validateDuplicateNames extends (io.cucumber.scala.Scenario => Option[String]) {

  private val scenarioNamesByFeature = scala.collection.mutable.HashMap[URI, scala.collection.mutable.HashMap[String, List[Int]]]()

  override def apply(scenario: io.cucumber.scala.Scenario): Option[String] = {
    val scenarioNames = scenarioNamesByFeature.getOrElseUpdate(scenario.getUri, scala.collection.mutable.HashMap[String, List[Int]]())
    val lineNumbers = scenarioNames.getOrElseUpdate(scenario.getName, List[Int]())
    // this is not very stable, but this only indicator we currently have for scenario from scenario outlines
    if (lineNumbers.isEmpty || lineNumbers.exists(lineNumber => scenario.getLine != lineNumber && Math.abs(scenario.getLine - lineNumber) <= 3)) {
      scenarioNames.update(scenario.getName, scenario.getLine :: lineNumbers)
      None
    } else {
      Some(s"scenario name '${scenario.getName}' is a ambiguous for scenarios ${scenario.getUri}:${lineNumbers.mkString(",")}")
    }
  }
}
