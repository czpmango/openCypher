#
# Copyright (c) 2015-2020 "Neo Technology,"
# Network Engine for Objects in Lund AB [http://neotechnology.com]
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Attribution Notice under the terms of the Apache License 2.0
#
# This work was created by the collective efforts of the openCypher community.
# Without limiting the terms of Section 6, any Derivative Work that is not
# approved by the public consensus process of the openCypher Implementers Group
# should not be described as “Cypher” (and Cypher® is a registered trademark of
# Neo4j Inc.) or as "openCypher". Extensions by implementers or prototypes or
# proposals for change that have been documented or implemented should only be
# described as "implementation extensions to Cypher" or as "proposed changes to
# Cypher that are not yet approved by the openCypher community".
#

#encoding: utf-8

Feature: Match6-3 - Match named paths WITH clause scenarios

  Scenario: Named path with WITH
    Given an empty graph
    And having executed:
      """
      CREATE ()
      """
    When executing query:
      """
      MATCH p = (a)
      WITH p
      RETURN p
      """
    Then the result should be, in any order:
      | p    |
      | <()> |
    And no side effects

  Scenario: Aggregation with named paths
    Given an empty graph
    And having executed:
      """
      CREATE (n1 {num: 1}), (n2 {num: 2}),
             (n3 {num: 3}), (n4 {num: 4})
      CREATE (n1)-[:T]->(n2),
             (n3)-[:T]->(n4)
      """
    When executing query:
      """
      MATCH p = ()-[*]->()
      WITH count(*) AS count, p AS p
      WITH nodes(p) AS nodes
      RETURN *
      """
    Then the result should be, in any order:
      | nodes                    |
      | [({num: 1}), ({num: 2})] |
      | [({num: 3}), ({num: 4})] |
    And no side effects