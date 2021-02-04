package code.restService

import net.liftweb.http.rest.RestHelper
import net.liftweb.http._
import net.liftweb.json.JsonDSL._

import code.lib.AppAux._
import code.model.Entities._
import code.restService.RestClient.{contributorsByRepo, reposByOrganization}

import java.time.{Duration, Instant}
import java.util.Date

object RestServer extends RestHelper {

  serve({
    case Req("org" :: organization :: "contributors" :: Nil, _, GetRequest) => buildRestResponse(organization)
  })

  private def buildRestResponse(organization: String): LiftResponse = {
    val contributors: List[Contributor] = contributorsByOrganization(organization)
    JsonResponse (contributors.map(_.asJson))
  }

  private def contributorsByOrganization(organization: Organization): List[Contributor] = {
    val sdf = new java.text.SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
    val initialInstant = Instant.now
    logger.info(s"Starting ContribsGH-S REST API call at ${sdf.format(Date.from(initialInstant))} - organization='$organization'")

    val repos = reposByOrganization(organization)

    // sequential retrieval of contributors by repo
    val contributorsDetailed: List[Contributor] = RestServerAux.contributorsDetailedSeq(organization, repos)

    // grouping, sorting
    val contributorsGrouped = contributorsDetailed.
      groupBy(_.name).
      mapValues(_.foldLeft(0)((acc, elt) => acc + elt.contributions)).
      toList.
      map(p => Contributor(p._1, p._2)).
      sortBy(c => (- c.contributions, c.name))

    val finalInstant = Instant.now
    logger.info(s"Finished ContribsGH-S REST API call at ${sdf.format(Date.from(finalInstant))} - organization='$organization'")
    logger.info(f"Time elapsed from start to finish: ${Duration.between(initialInstant, finalInstant).toMillis/1000.0}%3.2f seconds")

    contributorsGrouped
  }

}

object RestServerAux {

  def contributorsDetailedSeq(organization: Organization, repos: List[Repository]): List[Contributor] =
    repos.flatMap(contributorsByRepo(organization, _))

}