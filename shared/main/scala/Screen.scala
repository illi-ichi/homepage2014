package model

object Screen{
  sealed trait Horizontal
  case class Left(r: Double) extends Horizontal
  case class Center(r: Double) extends Horizontal
  case class Right(r: Double) extends Horizontal
  object Left{ def edge = Left(0.0) }
  object Center{ def point = Center(0.5) }
  object Right{ def edge = Right(1.0) }

  sealed trait VirtualPosition
  // 0 <= x, y <= 1
  case class Point(x: Horizontal, y: Double) extends VirtualPosition
  // 0 <= rate <= 1
  case class Between(from: Point, to: Point, rate: Double) extends VirtualPosition

  type VirtualSize = VirtualPosition
  val ratio = 1.0

  class Converter(width: Int, height: Int) {
    val (cw, ch) = {
      if(width < height) (width.toDouble, width * ratio)
      else               (height / ratio, height.toDouble)
    }
    val (cx, cy) = ((width - cw) / 2, (height - ch) / 2)

    def apply(v: VirtualPosition):(Int, Int) = {
      def floor(t:(Double, Double)) = (t._1.toInt, t._2.toInt)
      floor (
        v match {
          case Point(Left(x), y) => (cx * x, vertical(y))
          case Point(Center(x), y) => (cx + cw * x, vertical(y))
          case Point(Right(x), y) => (cw  + cx * (1 + x), vertical(y))
          case Between(from, to, r) => {
            def middle(x1: Double, x2: Double) =
              if(x1 < x2) (x2 - x1) * r + x1
              else        (x1 - x2) * (1.0 - r) + x2

            val (x1, y1) = apply(from)
            val (x2, y2) = apply(to)
            (middle(x1, x2), middle(y1, y2))
          }
        }
      )
    }

    def vertical(r: Double) = cy + ch * r
  }
}