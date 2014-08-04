package com.edcanhack.spinal.commons.state

import com.edcanhack.spinal.commons.SpinalException
import com.typesafe.scalalogging.slf4j.StrictLogging
import play.api.libs.json._
import play.api.libs.functional.syntax._



class JSONException(message: String, throwable: Throwable = null) extends SpinalException(message, throwable)

object JSON extends StrictLogging {
  object Writes {
    implicit val routableWrites = new Writes[Routable] {
      override def writes(o: Routable): JsValue = o match {
        case r: HTTPRoutable => httpRoutableWrites.writes(r)
        case r: Routable => catchallRoutableWrites.writes(r)
      }
    }

    implicit val httpRoutableWrites: Writes[HTTPRoutable] = new Writes[HTTPRoutable] {
      override def writes(o: HTTPRoutable): JsValue = Json.obj(
        "type" -> "http",
        "host" -> o.host,
        "sourcePort" -> o.sourcePort,
        "endpoints" -> o.endpoints.map(e => endpointWrites.writes(e))
      )
    }

    implicit val catchallRoutableWrites: Writes[Routable] = new Writes[Routable] {
      override def writes(o: Routable): JsValue = Json.obj(
        "type" -> o.routableType,
        "sourcePort" -> o.sourcePort,
        "endpoints" -> o.endpoints.map(e => endpointWrites.writes(e))
      )
    }

    implicit val endpointWrites = new Writes[Endpoint] {
      override def writes(o: Endpoint): JsValue = Json.obj(
        "address" -> o.address,
        "port" -> o.port
      )
    }

    implicit val universeWrites = new Writes[Universe] {
      override def writes(o: Universe): JsValue = ???
    }
  }

  object Reads {
    implicit val routableReads: Reads[Routable] = new Reads[Routable] {
      override def reads(json: JsValue): JsResult[Routable] = ((json \ "type").as[String] match {
        case "http" => httpRoutableReads
        case "tcp" => tcpRoutableReads
      }).reads(json)
    }

    implicit val httpRoutableReads: Reads[HTTPRoutable] = (
      (JsPath \ "host").read[String] and
      (JsPath \ "sourcePort").read[Int] and
      (JsPath \ "endpoints").read[Seq[Endpoint]]
    )(HTTPRoutable.apply _)

    implicit val tcpRoutableReads: Reads[TCPRoutable] = (
      (JsPath \ "sourcePort").read[Int] and
      (JsPath \ "endpoints").read[Seq[Endpoint]]
    )(TCPRoutable.apply _)
    implicit val udpRoutableReads: Reads[UDPRoutable] = (
      (JsPath \ "sourcePort").read[Int] and
      (JsPath \ "endpoints").read[Seq[Endpoint]]
    )(UDPRoutable.apply _)

    implicit val endpointReads: Reads[Endpoint] = (
      (JsPath \ "address").read[String] and
      (JsPath \ "port").read[Int]
    )(Endpoint.apply _)

    implicit val universeReads: Reads[Universe] = ???
  }
}
