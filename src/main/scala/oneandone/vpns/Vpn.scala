package oneandone.vpns
import java.io._
import java.net.MalformedURLException
import java.util.Base64

import oneandone.OneandoneClient
import oneandone.datacenters.Datacenter
import org.json4s.Extraction
import org.json4s.native.JsonMethods.parse

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
  ): ConfigResponse = {
    val response = client.get(path :+ id :+ "configuration_file")
    val json = parse(response).camelizeKeys
    var objectResponse = json.extract[ConfigResponse]
    downloadVpnConfigurationZIP(objectResponse.configZipFile, filePath)
  }

  def downloadVpnConfigurationString(id: String, filePath: String)(
      implicit client: OneandoneClient
  ): ConfigResponse = {
    val response = client.get(path :+ id :+ "configuration_file")
    val json = parse(response).camelizeKeys
    json.extract[ConfigResponse]
  }

  @throws[FileNotFoundException]
  @throws[MalformedURLException]
  @throws[IOException]
  private def DownloadConfigurationFile(codedFile: String, fileName: String): Unit = {
    val _fileName = fileName //The file that will be saved on your computer
    //Code to download
    val in = new ByteArrayInputStream(codedFile.getBytes("UTF-8"))
    val out = new ByteArrayOutputStream
    val buf = new Array[Byte](1024)
    var n = 0
    while ({ -1 != (n = in.read(buf)) }) out.write(buf, 0, n)
    out.close()
    in.close()
    val response = Base64.getDecoder.decode(out.toByteArray)
    val fos = new FileOutputStream(_fileName)
    fos.write(response)
    fos.close()
    //End download code
    System.out.println("Finished")
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
