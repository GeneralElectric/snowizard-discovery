# Snowizard Discovery

This is a [Dropwizard](http://dropwizard.io) [bundle](http://dropwizard.io/manual/core/#bundles) that can be used to register a Dropwizard service into [Zookeeper](https://zookeeper.apache.org) upon startup. Connectivity to Zookeeper is provided by Netflix's [Curator](http://curator.incubator.apache.org) library and its built in [Service Discovery](http://curator.incubator.apache.org/curator-x-discovery/index.html) framework.

# Usage

To use the snowizard-discovery module, first add it as a dependency into your Maven pom.xml file:

```
<dependencies>
    <dependency>
        <groupId>com.ge.snowizard</groupId>
        <artifactId>snowizard-discovery</artifactId>
        <version>0.6.2</version>
    </dependency>
</dependency>
```

Then in your Dropwizard [Configuration](http://dropwizard.io/manual/core/#configuration) file, add a property to represent the discovery configuration for your service:

```
# Discovery-related settings.
discovery:
    serviceName: hello-world
```

And have your configuration class expose the DiscoveryFactory.

```
public class HelloWorldConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private final DiscoveryFactory discovery = new DiscoveryFactory();

    public DiscoveryFactory getDiscoveryFactory() {
        return discovery;
    }
}
```

If you only wish to have your service register itself with Zookeeper and you don't intend on consuming any other services, you just need to add the following into your service's ```initialize``` method:

```
final DiscoveryBundle<HelloWorldConfiguration> discovery = new DiscoveryBundle<HelloWorldConfiguration>() {
    @Override
    public DiscoveryFactory getDiscoveryFactory(HelloWorldConfiguration configuration) {
        return configuration.getDiscoveryFactory();
    }
};
bootstrap.addBundle(discovery);
```

where ```HelloWorldConfiguration``` is your configuration class name.

If you want to also consume other services, you can store an instance of the ```DiscoveryBundle``` so that you can retrieve a new ```DiscoveryClient``` to access additional services.

```
private DiscoveryBundle<HelloWorldConfiguration> discovery;

public static void main(String[] args) throws Exception {
    new HelloWorldService().run(args);
}

@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    bootstrap.setName("hello-world");

    discovery = new DiscoveryBundle<HelloWorldConfiguration>() {
        @Override
        public DiscoveryFactory getDiscoveryFactory(HelloWorldConfiguration configuration) {
            return configuration.getDiscoveryFactory();
        }
    };

    bootstrap.addBundle(discovery);
}

@Override
public void run(HelloWorldConfiguration configuration,
        Environment environment) throws Exception {

    final DiscoveryClient client = discovery.newDiscoveryClient("hello-world");
    environment.manage(new DiscoveryClientManager(client));
}
```

Be sure to register the ```DiscoveryClient``` using the ```DiscoveryClientManager``` as a [Managed Object](http://dropwizard.io/manual/core/#managed-objects) so that it is properly started and shutdown when your service is stopped and started.

# Enhancements

1. Support an "[Advertise Locally, Lookup Globally](http://whilefalse.blogspot.com/2012/12/building-global-highly-available.html)" model that Camille Fournier outlined on her blog by supporting separate Zookeeper connections, one that connects locally and one that connects to a global instance.

# Contributing

To contribute:

1. fork the project
2. make a branch for each thing you want to do (don't put everything in your master branch: we don't want to cherry-pick and we may not want everything)
3. send a pull request to jplock

## Building

To build and test, run `mvn test`.

[![Build Status](https://travis-ci.org/GeneralElectric/snowizard-discovery.png)](https://travis-ci.org/GeneralElectric/snowizard-discovery)
