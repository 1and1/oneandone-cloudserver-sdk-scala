package oneandone
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JBool, JInt, JString}

case object BooleanCustomSerializer
    extends CustomSerializer[Boolean](ser = format =>
      ({
        case JInt(intValue) =>
          intValue match {
            case intValue: BigInt if intValue == 0 => false
            case intValue: BigInt if intValue == 1 => true
          }
        case JBool(intValue) =>
          intValue match {
            case false => false
            case true => true
          }
      }, {
        case intValue: Boolean => JString(intValue.toString)
      }))
