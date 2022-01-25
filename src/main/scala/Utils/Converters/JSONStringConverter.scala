package Utils.Converters

trait JSONStringConverter[T] {
  def convert(data: T): String
}

