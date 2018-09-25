package oneandone.vpns
import java.util.Base64

import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import org.json4s.Extraction
import org.json4s.native.JsonMethods.parse
import java.io.FileOutputStream

case class Vpn(
    id: String,
    description: String,
    name: String,
    state: String,
    `type`: String,
    creationDate: String,
    datacenter: Datacenter,
    ips: List[String]
) {}

object Vpn extends oneandone.Path {
  override val path: Seq[String] = Seq("vpns")

  def list(
      queryParameters: Map[String, String] = Map.empty
  )(implicit client: OneandoneClient): Seq[Vpn] = {
    val response = client.get(path, queryParameters)
    val json = parse(response).camelizeKeys
    json.extract[Seq[Vpn]]
  }

  def get(id: String)(implicit client: OneandoneClient): Vpn = {
    val response = client.get(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Vpn]
  }

  def downloadVpnConfigurationZIP(id: String, filePath: String)(
      implicit client: OneandoneClient
  ): Unit = {
    val response = client.get(path :+ id :+ "configuration_file")
    val json = parse(response).camelizeKeys
    var objectResponse = json.extract[ConfigResponse]
    downloadConfigFile(objectResponse.configZipFile, filePath)
  }

  def downloadVpnConfigurationString(id: String, filePath: String)(
      implicit client: OneandoneClient
  ): ConfigResponse = {
    val response = client.get(path :+ id :+ "configuration_file")
    val json = parse(response).camelizeKeys
    json.extract[ConfigResponse]
  }

  private def downloadConfigFile(codedFile: String, fileName: String): Unit = {
    val data = Base64.getDecoder.decode(codedFile)
    try {
      val stream = new FileOutputStream(fileName)
      try stream.write(data)
      finally if (stream != null) stream.close()
    }
  }

  def createVpn(request: VpnRequest)(implicit client: OneandoneClient): Vpn = {

    val response = client.post(path, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Vpn]
  }

  def updateVpn(id: String, request: UpdateVpnRequest)(
      implicit client: OneandoneClient
  ): Vpn = {

    val response = client.put(path :+ id, Extraction.decompose(request).snakizeKeys)
    val json = parse(response).camelizeKeys
    json.extract[Vpn]
  }

  def delete(id: String)(implicit client: OneandoneClient): Vpn = {
    val response = client.delete(path :+ id)
    val json = parse(response).camelizeKeys
    json.extract[Vpn]
  }

  def waitVpnStatus(id: String, status: String)(implicit client: OneandoneClient): Boolean = {
    var response = client.get(path :+ id)
    var json = parse(response).camelizeKeys
    var img = json.extract[Vpn]
    while (img.state != status) {
      Thread.sleep(8000)
      response = client.get(path :+ id)
      json = parse(response).camelizeKeys
      img = json.extract[Vpn]
    }
    true
  }
}
