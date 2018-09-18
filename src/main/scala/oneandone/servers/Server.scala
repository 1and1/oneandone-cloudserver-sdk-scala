package oneandone.servers

import oneandone.OneandoneClient
import org.json4s.{DefaultFormats, Extraction, Formats}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

import scala.concurrent.ExecutionContext

case class Server(
    id: String,
    name: String,
    description: String,
    serverType: String,
    datacenter: Datacenter,
    creationDate: String,
    firstPassword: String,
    status: Status,
    hardware: Hardware,
    image: Image,
    dvd: String,
    snapshot: String,
    ips: Option[List[Ips]] = None,
    alerts: Option[String] = None,
    monitoringPolicy: Option[String] = None
) {}

object Server extends oneandone.Path {
  override val path: Seq[String]               = Seq("servers")
  val fixedInstancesPath                       = "fixed_instance_sizes"
  val baremetalModelPath                       = "baremetal_models"
  implicit lazy val serializerFormats: Formats = DefaultFormats

  def list(queryParameters: Map[String, String] = Map.empty)(
      implicit client: OneandoneClient): Seq[Server] = {
    val response = client.get(path, queryParameters)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[Server]]
  }

  def get(id: String)(implicit client: OneandoneClient): Server = {
    val response = client.get(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def listFixedInstances(queryParameters: Map[String, String] = Map.empty)(
      implicit client: OneandoneClient): Seq[FixedInstance] = {
    val response = client.get(path :+ fixedInstancesPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[FixedInstance]]
  }

  def getFixedInstance(id: String)(implicit client: OneandoneClient): FixedInstance = {
    val response = client.get(path :+ fixedInstancesPath :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[FixedInstance]
  }

  def listBaremetalModels(queryParameters: Map[String, String] = Map.empty)(
      implicit client: OneandoneClient): Seq[BaremetalModel] = {
    val response = client.get(path :+ baremetalModelPath)
    val json     = parse(response).camelizeKeys
    json.extract[Seq[BaremetalModel]]
  }

  def getBaremetalModel(id: String)(implicit client: OneandoneClient): BaremetalModel = {
    val response = client.get(path :+ baremetalModelPath :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[BaremetalModel]
  }

  def createCloud(request: ServerRequest)(implicit client: OneandoneClient): Server = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def apply(id: BigInt)(implicit client: OneandoneClient, ec: ExecutionContext) = {
    val path = this.path :+ id.toString
    for {
      response <- client.get[Server](path)
    } yield {
      response
    }
  }

  def delete(id: String)(implicit client: OneandoneClient): Server = {
    val response = client.delete(path :+ id)
    val json     = parse(response).camelizeKeys
    json.extract[Server]
  }

  def waitServerStatus(id: String, status: String)(implicit client: OneandoneClient): Boolean = {
    var response = client.get(path :+ id)
    var json     = parse(response).camelizeKeys
    var srvr     = json.extract[Server]
    while (srvr.status.state != status) {
      Thread.sleep(5000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      srvr = json.extract[Server]
    }
    true
  }

//  def exists(
//      id: BigInt)(implicit client: OneandoneClient, ec: ExecutionContext): Future[Boolean] = {
//    val path = this.path :+ id.toString
//    client.exists(path)
//  }

//  def isDeleted(
//      id: BigInt)(implicit client: OneandoneClient, ec: ExecutionContext): Future[Boolean] = {
//    for {
//      exists <- exists(id)
//    } yield { !exists }
//  }

}
