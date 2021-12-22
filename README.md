![logo](src/test/resources/nats-junit.png)

# nats-server-junit
A Unit wrapper of the original [Nats server](https://github.com/nats-io/nats-server)

[![Build][build_shield]][build_link]
[![Maintainable][maintainable_shield]][maintainable_link]
[![Coverage][coverage_shield]][coverage_link]
[![Issues][issues_shield]][issues_link]
[![Commit][commit_shield]][commit_link]
[![Dependencies][dependency_shield]][dependency_link]
[![License][license_shield]][license_link]
[![Central][central_shield]][central_link]
[![Tag][tag_shield]][tag_link]
[![Javadoc][javadoc_shield]][javadoc_link]
[![Size][size_shield]][size_shield]
![Label][label_shield]

[build_shield]: https://github.com/YunaBraska/nats-server-junit/workflows/JAVA_CI/badge.svg
[build_link]: https://github.com/YunaBraska/nats-server-junit/actions?query=workflow%3AJAVA_CI
[maintainable_shield]: https://img.shields.io/codeclimate/maintainability/YunaBraska/nats-server-junit?style=flat-square
[maintainable_link]: https://codeclimate.com/github/YunaBraska/nats-server-junit/maintainability
[coverage_shield]: https://img.shields.io/codeclimate/coverage/YunaBraska/nats-server-junit?style=flat-square
[coverage_link]: https://codeclimate.com/github/YunaBraska/nats-server-junit/test_coverage
[issues_shield]: https://img.shields.io/github/issues/YunaBraska/nats-server-junit?style=flat-square
[issues_link]: https://github.com/YunaBraska/nats-server-junit/commits/main
[commit_shield]: https://img.shields.io/github/last-commit/YunaBraska/nats-server-junit?style=flat-square
[commit_link]: https://github.com/YunaBraska/nats-server-junit/issues
[license_shield]: https://img.shields.io/github/license/YunaBraska/nats-server-junit?style=flat-square
[license_link]: https://github.com/YunaBraska/nats-server-junit/blob/main/LICENSE
[dependency_shield]: https://img.shields.io/librariesio/github/YunaBraska/nats-server-junit?style=flat-square
[dependency_link]: https://libraries.io/github/YunaBraska/nats-server-junit
[central_shield]: https://img.shields.io/maven-central/v/berlin.yuna/nats-server-junit?style=flat-square
[central_link]:https://search.maven.org/artifact/berlin.yuna/nats-server-junit
[tag_shield]: https://img.shields.io/github/v/tag/YunaBraska/nats-server-junit?style=flat-square
[tag_link]: https://github.com/YunaBraska/nats-server-junit/releases
[javadoc_shield]: https://javadoc.io/badge2/berlin.yuna/nats-server-junit/javadoc.svg?style=flat-square
[javadoc_link]: https://javadoc.io/doc/berlin.yuna/nats-server-junit
[size_shield]: https://img.shields.io/github/repo-size/YunaBraska/nats-server-junit?style=flat-square
[label_shield]: https://img.shields.io/badge/Yuna-QueenInside-blueviolet?style=flat-square
[gitter_shield]: https://img.shields.io/gitter/room/YunaBraska/nats-server-junit?style=flat-square
[gitter_link]: https://gitter.im/nats-server-junit/Lobby

### Family

* Nats **plain Java**
    * [Nats-Server](https://github.com/YunaBraska/nats-server)
    * [Nats-Streaming-Server](https://github.com/YunaBraska/nats-streaming-server)
* Nats for **JUnit**
    * [Nats-Server-JUnit](https://github.com/YunaBraska/nats-server-junit)
* Nats for **Spring Boot**
    * [Nats-Server-Embedded](https://github.com/YunaBraska/nats-server-embedded)
    * [Nats-Streaming-Server-Embedded](https://github.com/YunaBraska/nats-streaming-server-embedded)

### Usage

```xml

<dependency>
  <groupId>berlin.yuna</groupId>
  <artifactId>nats-server-junit</artifactId>
  <version>0.0.19</version>
</dependency>
```

[Get latest version][central_link]

### Example

* Test without parameter

```java

@JUnitNatsServer(port = 4680)
class NatsServerFirstTest {

  final NatsServer natsServer = getNatsServer();

  @Test
  void natsServerShouldStart() {
    assertThat(natsServer, is(notNullValue()));
  }
}
```

* Test with parameter

```java

@JUnitNatsServer(port = 4680)
class NatsServerFirstTest {

  @ParameterizedTest
  @ArgumentsSource(NatsServer.class)
  void natsServerShouldStart(final NatsServer natsServer) {
    assertThat(natsServer, is(notNullValue()));
  }
}
```

* Test with all parameter

```java

@JUnitNatsServer(port = 4680, timeoutMs = 10000, configFile = "my.properties", downloadUrl = "https://example.com", binaryFile = "/tmp/natsserver", config = {"ADDR", "localhost"})
class NatsServerFirstTest {

  final NatsServer natsServer = getNatsServer();

  @Test
  void natsServerShouldStart() {
    assertThat(natsServer, is(notNullValue()));
  }
}
```

* Test with random port

```java

@JUnitNatsServer(port = -1)
class NatsServerFirstTest {

  final NatsServer natsServer = getNatsServer();

  @Test
  void natsServerShouldStart() {
    assertThat(natsServer, is(notNullValue()));
  }
}
```

* Test with nats started only one time in the whole test context with `keepAlive` flag

```java

@JUnitNatsServer(port = -1, name = "RandomNats", keepAlive = true)
class NatsServerKeepAliveFirstTest {

  @Test
  void natsServerShouldStart() {
    final NatsServer natsServer = getNatsServerByName("RandomNats");
    assertThat(natsServer, is(notNullValue()));
    assertThat(natsServer.getPort(), is(not(-1)));
  }
}
```

For more config options, see [Nats Server config priority](https://github.com/YunaBraska/nats-server#configuration-priority)
