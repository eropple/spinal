# Overview #
The purpose of this document is to discuss `spinal` at an architectural level. `spinal` has been designed to be extremely configurable and pluggable without sacrificing conceptual simplicity and ease of use; much of that simplicity comes down to configuration and extension being modifications and extensions of a small set of core, easily-understood components.

## Global Concepts ##
### Universe ###
The _universe_ is the visible state of the ecosystem that `spinal` is tasked with connecting. All nodes visible to a leader and the routes they explicitly allow are part of this universe.

### Routable ###
The universe is composed of `Routable`s. A `Routable` is (surprisingly enough) anything that can be routed. At its most basic it is a `hostname -> IP` pair, though it can pass along port information that is useful to some types of effectors.

### Spine ###
A collection of linked leaders and workers is called a _spine_.


## Leader ##
The _leader_ is a coordination node. Its role is to discover the state of the current universe and provide it in some manner to the worker nodes that look up to it.

It is composed of one _locator_, one _elector_, one or more _discoverers_, zero or more _observers_, and one or more _publishers_.

### Locator ###
The _locator_ finds the appropriate _leader_ for the spine. This may be hardcoded or discovered in another manner: AWS tags, DNS, etc.

### Elector ###
The _elector_ is the core of a high-availability spine. While in a centralized system the `SolitaryElector` (which does not seek out other potential leaders) may be sufficient, a high-availability system will attempt to use leader election of some kind (i.e., `ZookeeperElector`) to elect an _active leader_ that performs the tasks of the leader while the others wait for failure.

The elector is tasked with knowing whether or not there is a healthy active leader already available as well.

### Discoverer ###
The `Discoverer` component discovers the universe of the current spine. How it does this is intentionally left vague, even down to the semantics of polling (e.g., periodically querying for the AWS EC2 tags on a set of instances) versus receiving push data (e.g., subscribing to notifications within Zookeeper). Once it discovers the current state of the universe, that state is passed along to the rest of the system.

Multiple discoverers can be used to draw from different sources of information. For example, an environment may use AWS instances by default, necessitating the use of an `EC2TagDiscoverer`. Later on, the same environment may be augmented with a Mesos cluster that runs applications across many instances; to solve this, the spine can include the `MarathonDiscoverer` to query Marathon's API for those applications. Rules to merge the universes from multiple discoverers are TBD.

### Observer ###
An _observer_ is an ancillary pluggable component within the leader that can register itself to be invoked _in a strictly read-only capacity_ when a named event is raised across the leader. It can be used for tasks such as statistics pages and more detailed logging.

### Publisher ###
The publisher takes action with the universe passed through the leader loop by the discoverer. While assumed generically to be a publisher/subscriber model, the exact mechanisms are left unspecified--HTTP, Zookeeper, etc. all can fulfill this contract. (By the strictest terms of the `Publisher` abstract class, a system where the _worker_ polls the _leader_ is legal.)




## Worker ##
A _worker_ is composed of one _locator_, one _subscriber_, zero or more _observers_, and zero or more _effectors_.

### Locator ###
Identical (same code) to the leader locator.

### Subscriber ###
The _subscriber_ acquires information about the universe for the worker to take action upon. The mechanism for this is left unspecified; it could be via an HTTP endpoint (receiving updates from the leader), it cound be via Zookeeper notifications, etc.

### Observer ###
As with the leader, the _observer_ again may take read-only action when an event is raised.

The leader and worker observers use a superset of the same API and so it is possible to write an observer that can plug into either.

### Effector ###
At the very end of the chain is the _effector_, which is tasked with turning the `Routable`s in the universe into action of some kind. For example, the effector may be charged with updating a `haproxy` node with any changes or it may be tasked with writing out `iptables` and `/etc/hosts` rules, as appropriate.


## Singleton ##
The _singleton_ mode exists for situations where a separate worker and leader don't make sense (for example, in the case of one load balancer). The singleton application includes both a leader and a worker but omits the elector (not removed but instead hard-coded to be a `SolitaryElector`), publisher, locator (hard-coded to be the `SelfLocator`), and subscriber. It becomes a single pathway from discoverer to effector.

Both worker and leader observers can be registered within the singleton, but not all events will be raised to it so the actual behavior may sometimes be undefined.