package Utils.Serializers


object JSONStringSerializer {
  trait JSONStringSerializable[T] {
    def toJSONString(data: T): String
  }

  implicit class JSONStringEnrichment[T](value: T) {
    def toJSONString(implicit converter: JSONStringSerializable[T]): String = converter.toJSONString(value)
  }
}

