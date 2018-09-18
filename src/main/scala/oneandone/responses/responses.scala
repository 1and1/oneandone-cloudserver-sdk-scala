package oneandone

package object responses {

  private[oneandone] def seqToOption[T](s: Seq[T]): Option[Seq[T]] = {
    if (s.isEmpty)
      None
    else Some(s)
  }

  private[oneandone] case class Server(
      server: Server
  )

  private[oneandone] case class Servers(
      servers: Seq[Server],
  ) {}

}
