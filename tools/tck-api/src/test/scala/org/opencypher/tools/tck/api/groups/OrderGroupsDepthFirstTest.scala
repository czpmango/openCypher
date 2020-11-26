/*
 * Copyright (c) 2015-2020 "Neo Technology,"
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
package org.opencypher.tools.tck.api.groups

import java.net.URI
import java.util

import io.cucumber.core.gherkin.Pickle
import org.opencypher.tools.tck.api.Dummy
import org.opencypher.tools.tck.api.Measure
import org.opencypher.tools.tck.api.Scenario
import org.opencypher.tools.tck.api.Step
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.Inside
import org.scalatest.Inspectors
import org.scalatest.OptionValues

class OrderGroupsDepthFirstTest extends AnyFunSpec with Matchers with Inspectors with Inside with OptionValues {

  def invariants(groupSequence: Seq[Group], groups: Set[Group]){
    describe("should yield a group sequence that") {

      it("contains all original groups in the same cardinality") {
        groupSequence.toSet should equal(groups)
        groupSequence.size should equal(groups.size)
      }
      it("has Total group as the first group") {
        groupSequence.head shouldBe Total
      }
      it("has Total group no where else") {
        forAll(groupSequence.tail) { _ should not be Total }
      }
      describe("has every group after their parent group so that") {
        val groupsWithIndex = groupSequence.zipWithIndex
        groupsWithIndex.tail.foreach {
          case (g, gi) =>
            it(s"group $g is after ${g.parent}") {
              forExactly(1, groupsWithIndex) {
              x => inside(x) { case (p, pi) =>
                p shouldBe g.parent.value
                pi should be < gi
              }
            }
          }
        }
      }
    }
  }

  describe("The given list of four scenarios") {
    val scrA = createScenario(List[String](), "ftr5", 1, "scrA", None, Set[String]())
    val scrB = createScenario(List[String](), "ftr1", 1, "scrB", None, Set[String]("A"))
    val scrC = createScenario(List[String]("b"), "ftr11", 1, "scrC", None, Set[String]("A"))
    val scrD = createScenario(List[String]("b"), "ftr3", 1, "scrD", None, Set[String]("B"))

    val scenarios = List(scrA, scrB, scrC, scrD)
    val groupedScenarios = GroupScenarios(scenarios)
    val groupSequence = OrderGroupsDepthFirst(groupedScenarios.keySet)

    invariants(groupSequence, groupedScenarios.keySet)

    it("should yield the given group sequence") {
      val scB = ScenarioCategory("b", Total)
      val ftr3 = Feature("ftr3", scB)
      val ftr11 = Feature("ftr11", scB)
      val ftr1 = Feature("ftr1", Total)
      val ftr5 = Feature("ftr5", Total)
      val expected = Seq(
        Total,
        scB,
        ftr3,
        ScenarioItem(scrD, ftr3),
        ftr11,
        ScenarioItem(scrC, ftr11),
        ftr1,
        ScenarioItem(scrB, ftr1),
        ftr5,
        ScenarioItem(scrA, ftr5),
        Tag("A"),
        Tag("B")
      )

      groupSequence should equal(expected)
    }
  }

  describe("The given list of ten scenarios") {
    val scrA = createScenario(List[String](), "ftr5 - a", 1, "1", None, Set[String]())
    val scrB = createScenario(List[String](), "ftr1 - b", 1, "1", None, Set[String]("A"))
    val scrC = createScenario(List[String](), "ftr1 - b", 2, "2", None, Set[String]("A"))
    val scrD = createScenario(List[String]("b"), "ftr11 - c", 1, "1", None, Set[String]("A", "C"))
    val scrE = createScenario(List[String]("b"), "ftr11", 1, "1", None, Set[String]("A", "C"))
    val scrF1 = createScenario(List[String]("a", "b"), "ftr2", 1, "1", Some(1), Set[String]("C"))
    val scrF2 = createScenario(List[String]("a", "b"), "ftr2", 1, "1", Some(2), Set[String]("C"))
    val scrG = createScenario(List[String]("a", "b"), "ftr", 1, "1", None, Set[String]("D"))
    val scrH = createScenario(List[String]("b"), "ftr11 - b", 1, "1", None, Set[String]("D", "2"))
    val scrI = createScenario(List[String]("b"), "ftr3", 1, "1", None, Set[String]("B"))
    val scrJ = createScenario(List[String]("a", "b"), "ftrX", 1, "1", None, Set[String]("11"))

    val scenarios = List(scrA, scrB, scrC, scrD, scrE, scrF1, scrF2, scrG, scrH, scrI, scrJ)
    val groupedScenarios = GroupScenarios(scenarios)
    val groupSequence = OrderGroupsDepthFirst(groupedScenarios.keySet)

    invariants(groupSequence, groupedScenarios.keySet)

    it("should yield the given group sequence") {
      val scB = ScenarioCategory("b", Total)
      val scA = ScenarioCategory("a", Total)
      val scAB = ScenarioCategory("b", scA)
      val ftr = Feature("ftr", scAB)
      val ftr2 = Feature("ftr2", scAB)
      val scrOutF = ScenarioOutline(Some(1), "1", ftr2)
      val ftrX = Feature("ftrX", scAB)
      val ftr3 = Feature("ftr3", scB)
      val ftr11 = Feature("ftr11", scB)
      val ftr11b = Feature("ftr11 - b", scB)
      val ftr11c = Feature("ftr11 - c", scB)
      val ftr1b = Feature("ftr1 - b", Total)
      val ftr5a = Feature("ftr5 - a", Total)
      val expected = Seq(
        Total,
        scA,
        scAB,
        ftr,
        ScenarioItem(scrG, ftr),
        ftr2,
        scrOutF,
        ExampleItem(1, scrF1, scrOutF),
        ExampleItem(2, scrF2, scrOutF),
        ftrX,
        ScenarioItem(scrJ, ftrX),
        scB,
        ftr3,
        ScenarioItem(scrI, ftr3),
        ftr11,
        ScenarioItem(scrE, ftr11),
        ftr11b,
        ScenarioItem(scrH, ftr11b),
        ftr11c,
        ScenarioItem(scrD, ftr11c),
        ftr1b,
        ScenarioItem(scrB, ftr1b),
        ScenarioItem(scrC, ftr1b),
        ftr5a,
        ScenarioItem(scrA, ftr5a),
        Tag("11"),
        Tag("2"),
        Tag("A"),
        Tag("B"),
        Tag("C"),
        Tag("D")
      )

      groupSequence should equal(expected)
    }
  }

  private def createScenario(categories: List[String], featureName: String, number: Int, name: String, index: Option[Int], tags: Set[String]) = {
    val dummyPickle: Pickle = new io.cucumber.core.gherkin.Pickle() {
      override val getKeyword: String = ""

      override val getLanguage: String = "EN"

      override val getName: String = "name"

      override val getLocation: io.cucumber.core.gherkin.Location = new io.cucumber.core.gherkin.Location() {
        override val getLine: Int = 1

        override val getColumn: Int = 1
      }

      override val getScenarioLocation: io.cucumber.core.gherkin.Location = new io.cucumber.core.gherkin.Location() {
        override val getLine: Int = 1

        override val getColumn: Int = 1
      }

      override val getSteps: util.List[io.cucumber.core.gherkin.Step] = new util.ArrayList[io.cucumber.core.gherkin.Step]()

      override val getTags: util.List[String] = new util.ArrayList[String]()

      override val getUri: URI = new URI("http://www.opencypher.org/")

      override val getId: String = "id"
    }

    val dummyPickleStep: io.cucumber.core.gherkin.Step = new io.cucumber.core.gherkin.Step() {
      override val getLine: Int = 1

      override val getArgument: io.cucumber.core.gherkin.Argument = new io.cucumber.core.gherkin.DocStringArgument() {
        override val getContent: String = "text"

        override val getContentType: String = ""

        override val getLine: Int = 1
      }

      override val getKeyWord: String = "keyWord"

      override val getType: io.cucumber.core.gherkin.StepType = io.cucumber.core.gherkin.StepType.GIVEN

      override val getPreviousGivenWhenThenKeyWord: String = ""

      override val getText: String = "xyz"

      override val getId: String = "id"
    }

    val dummySteps: List[Step] = List[Step](Dummy(dummyPickleStep), Measure(dummyPickleStep))
    val dummyPath: java.nio.file.Path = new java.io.File("ftr1.feature").toPath

    Scenario(categories, featureName, Some(number), name, index, tags, dummySteps, dummyPickle, dummyPath)
  }
}