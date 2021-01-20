#
# Copyright (c) 2015-2021 "Neo Technology,"
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

// unsupported: exists function
Feature: Map4 - Field existence check

  Scenario Outline: [1] `exists()` with literal maps
    Given any graph
    When executing query:
      """
      WITH <map> AS map
      RETURN exists(map.<key>) AS result
      """
    Then the result should be, in any order:
      | result   |
      | <result> |
    And no side effects

    Examples:
      | map                                 | key     | result |
      | {name: 'Mats', name2: 'Pontus'}     | name    | true   |
      | {name: 'Mats', name2: 'Pontus'}     | name2   | true   |
      | {name: null}                        | name    | true   |
      | {name: null, name2: 'Pontus'}       | name    | true   |
      | {name: null, name2: null}           | name    | true   |
      | {name: null, name2: null}           | name2   | true   |
      | {name: 'Pontus', name2: null}       | name2   | true   |
      | {name: 'Pontus', notName2: null}    | name    | true   |
      | {notName: null, notName2: null}     | name    | false  |
      | {notName: 0, notName2: null}        | name    | false  |
      | {notName: 0}                        | name    | false  |
      | {}                                  | name    | false  |

  Scenario: [2] Using `exists()` on null map
    Given any graph
    When executing query:
      """
      WITH null AS m, { prop: 3 } AS n
      RETURN exists(m.prop), exists((null).prop)
      """
    Then the result should be, in any order:
      | exists(m.prop) | exists((null).prop) |
      | null           | null                |
    And no side effects
