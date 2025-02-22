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

import io.cucumber.datatable.DataTable
import org.opencypher.tools.tck.values.{CypherNode, CypherPath, CypherRelationship, CypherValue}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
  * This function will validate that a given DataTable from a TCK scenario contains parseable parameter representations.
  * If there are invalid parameter values in the table, a message describing them will be returned, otherwise None is
  * returned.
  */
object validateParameters extends (DataTable => Option[String]) {

  override def apply(table: DataTable): Option[String] = {
    // TODO: Specify constraints for parameter keys, and enforce these here
    val values = table.transpose().row(1).asScala

    val badValues = values.filterNot(this (_))
    if (badValues.isEmpty) None
    else Some(s"${badValues.size} parameters had invalid format: ${badValues.mkString(", ")}")
  }

  def apply(value: String): Boolean = {
    Try(CypherValue(value)) match {
      case Success(v) => v match {
        // graph elements are not valid parameters
        case _: CypherNode | _: CypherRelationship | _: CypherPath => false
        case _                                                     => true
      }
      case Failure(_) => false
    }
  }
}
