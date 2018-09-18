package oneandone

import java.util.concurrent.TimeoutException

import org.json4s._
import org.json4s.native._
import com.softwaremill.sttp._
import scala.concurrent.{ExecutionContext, Future}
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import org.json4s.CustomSerializer

/**
 *
 * @param token Your API token.
 */
case class OneandoneClient(
    private val token: String
) {
  def createRequest[U, T, S](
      path: Seq[String],
      queryParameters: Map[String, Seq[String]] = Map.empty
  ) = {}

//  def delete(
//      path: Seq[String],
//      maybeMessage: Option[JValue] = None,
//      queryParameters: Map[String, Seq[String]] = Map.empty
//  )(implicit ec: ExecutionContext): Future[Unit] = {
//    val request = createRequest(path, queryParameters).setMethod("DELETE")
//
//    for {
//      message <- maybeMessage
//    } {
//      val messageBody = JsonMethods.compact(JsonMethods.render(message.snakizeKeys))
//      request.setBody(messageBody)
//    }
//
//    for {
//      response <- client.executeRequest(request)
//    } yield {
//      if (response.getStatusCode >= 300) {
//        throw OneandoneClientException(response)
//      }
//    }
//  }

  implicit val backend = HttpURLConnectionBackend()
  val contentType      = "application/json; charset=utf-8"

  def get[T: Manifest](
      path: Seq[String],
      queryParameters: Map[String, Seq[String]] = Map.empty
  ): String = {
    var fullPath = OneandoneClient.host
    for (part <- path) fullPath += ("/" + part)
    val request = sttp
      .header("X-Token", token)
      .header("Content-Type", contentType)
      .get(uri"$fullPath") //client.executeRequest(createRequest(path, queryParameters).setMethod("GET"))
    val response = request.send()
    if (response.isSuccess) {
      response.body.right.get
    } else {
      //error handling
      throw new Exception(response.body.left.get)
    }
  }

  def post[T: Manifest](
      path: Seq[String],
      message: JValue,
      queryParameters: Map[String, Seq[String]] = Map.empty
  ): String = {
    var fullPath = OneandoneClient.host
    for (part <- path) fullPath += ("/" + part)
    val messageBody = JsonMethods.compact(JsonMethods.render(message.snakizeKeys))
    val request = sttp
      .post(uri"$fullPath")
      .body(messageBody)
    val response = request.send()
    if (response.isSuccess) {
      response.body.right.get
    } else {
      //error handling
      throw new Exception(response.body.left.get)
    }

  }
//
//  def postWithEmptyResponse(
//      path: Seq[String],
//      message: JValue,
//      queryParameters: Map[String, Seq[String]] = Map.empty
//  )(implicit ec: ExecutionContext): Future[Unit] = {
//    val messageBody = JsonMethods.compact(JsonMethods.render(message.snakizeKeys))
//    val request =
//      client.executeRequest(createRequest(path = path).setBody(messageBody).setMethod("POST"))
//
//    for {
//      response <- request
//    } yield {
//      if (response.getStatusCode != 204) {
//        throw OneandoneClientException(response)
//      }
//    }
//  }
//
//  def put[T: Manifest](
//      path: Seq[String],
//      message: JValue,
//      queryParameters: Map[String, Seq[String]] = Map.empty
//  )(implicit ec: ExecutionContext): Future[T] = {
//    val messageBody = JsonMethods.compact(JsonMethods.render(message.snakizeKeys))
//    val request =
//      client.executeRequest(createRequest(path = path).setBody(messageBody).setMethod("PUT"))
//    parseResponse[T](request)
//  }

  //todo: implemt retry in case we reach the limit
  //  /**
  //    * Repeatedly execute a function until a predicate is satisfied, with a delay of
  //    * [[actionCheckInterval]] between executions, and a maximum of [[maxWaitPerRequest]]
  //    * to wait per execution.
  //    *
  //    * @param pollAction
  //    * @param predicate
  //    * @tparam T
  //    * @return
  //    */
  //  private[oneandone] def poll[T](
  //                                     pollAction: => Future[T],
  //                                     predicate: T => Boolean
  //                                   )(implicit ec: ExecutionContext
  //                                   ): Future[T] = {
  //    val whenTimeout = after(maxWaitPerRequest)(Future.failed(new TimeoutException()))
  //    val firstCompleted = Future.firstCompletedOf(Seq(pollAction, whenTimeout))
  //    for {
  //      result <- firstCompleted
  //      completeResult <-
  //        if (predicate(result)) Future.successful(result)
  //        else sleep(actionCheckInterval).flatMap(_ => poll(pollAction, predicate))
  //    } yield completeResult
  //  }
  //}

  object OneandoneClient {

    val host = "https://cloudpanel-api.1and1.com/v1"
  }
}
