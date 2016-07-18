# CustomiSE - Build Custom WebDriver interfaces for your devices

CustomiSE provides a WebDriver interface that can be configured with your own implementation of interaction with your application.

## Use Case

Though the core of selenium is meant for WebBrowser automation, the protocol is generic enough using for any application. [Marathon](http://marathontesting.com) provides WebDriver bindings to automate Java/Swing and Java/FX applications.

However, there are requirements for providing WebDriver bindings to custom applications like a PoS terminal. CustomiSE encapsulates the JSONWireProtocol and let you build your own WebDriver class by just implementing few Java interfaces.

### IJavaAgent

IJavaAgent is the main entry point into the driver.

### IWindow

Your application understanding of how a Window should behave.

### IJavaElement

Provides means of communicating with your application's components, like buttons, display etc.

## Building CustomiSE

CustomiSE is built using [gradle](http://gradle.org). Just clone this repository and use the gradle wrapper in the toplevel folder to build CustomiSE.

```
$ ./gradlew build
```

```
C:\> .\gradlew build
```

## Using Eclipse

Use the eclipse target to create eclipse projects for CustomiSE. Import the projects into a new workspace.

```
$ ./gradlew eclipse
```

```
C:\> .\gradlew eclipse
```

## Example

The `fx-calculator` is a Java/FX calculator that also provides a TCP server to communicate with it.
The `calculator-agent` contains implementation of IJavaAgent to provide the driver interfaces.
The `fx-calculator-tests` uses a CustomiSEDriver instance to communicate with the application.

