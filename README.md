# Spinal #

`spinal` is a framework for observing the state of a networked computing environment and taking action based on that state—typically for building out load balancing solutions. It grew out of a need for something more generic and scalable than my previous [docker-web-proxy]() project for load balancing and cloud routing management--being able to route Docker containers is cool, but being able to route to Docker containers, Mesos tasks, AWS instances, Heroku applications, and anything you might have hanging off a DNS record somewhere? That's a lot cooler.

`spinal` is built in Scala and uses Akka for actor-y things and concurrency.

For more information on the general architecture of `spinal`, consult the [overview]().

## Use Cases ##
- Effortlessly scale your web applications. In environments with service location (including EC2 with tags), `spinal` shall require no configuration to keep up with changes in your application layer.
- Build VPC translation layer within AWS: HTTP load balancing and TCP proxying in EC2 Classic, `/etc/hosts` routing inside the VPC.

## `spinal` Goals ##

- **Pay-for-what-you-use configuration:** `spinal` should scale from a single-node load balancer to a hundred-node horde of routing machines. In doing so, you shouldn't incur overhead for things you're not using.
- **Environment agnosticism:** `spinal` can't be all things to all people, but it can be a lot of them. Whether your environment is Zookeeper-based, etcd-based, or doesn't have HA capabilities at all, `spinal` should be able to roll with the punches.
- **Fault tolerance:** Things break. `spinal` will break. To the extent that your environment allows, `spinal` should recover and keep on trucking.

## Sample Configuration ##

```scala
import com.edcanhack.spinal.singleton.config._
import com.edcanhack.spinal.leader.config._
import com.edcanhack.spinal.leader.config.specs._
import com.edcanhack.spinal.commons.config.specs._
import com.edcanhack.spinal.worker.config.specs._
import com.edcanhack.spinal.commons.state._

new SingletonConfiguration {
  val parameters = SingletonParameters()
  
  val discoverers: Seq[DiscovererSpec] = 
    Seq(StaticUniverseDiscovererSpec(
          100,
          Universe(Map(

          ))
        )
    )
  val leaderObservers: Seq[LeaderObserverSpec] = Seq()
  val workerObservers: Seq[WorkerObserverSpec] = Seq()
  val effectors: Seq[EffectorSpec] = Seq(EchoingEffectorSpec())
}
```

## Current Status ##

`spinal` is still under heavy development and is _not_ suitable for production use. It _does_ run in singleton mode, where the leader and worker actors operate within the same JVM and use Akka for communication, but there's no support for external components yet. `spinal` will only start up, find no instances, and pass no instances through to an effector that will log those no instances to stdout.

But! Getting there. The core architecture makes a lot of sense to me and it's just about time to start adding real components that do real stuff.

## Contributing ##
`spinal` is still very early in development. If the idea of a scalable, generic routing system tickles your fancy, though, and you'd like to help out, feel free to get in touch.

—Ed Ropple ([@edropple](), ed+spinal@edropple.com)


[docker-web-proxy]: https://github.com/eropple/docker-web-proxy
[overview]: https://github.com/eropple/spinal/blob/develop/doc/overview.md
[@edropple]: https://twitter.com/edropple