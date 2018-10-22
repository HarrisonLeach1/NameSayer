# NameSayer
This project involves creating a platform that will provide users with an application to help them practise unfamiliar names. The target users are university students that want to practise the names of their class mates.

## How to Use

### Prerequisites
* Java 8 or later

* JavaFX runtime

* Audio input and Output devices

* It is only runnable on a Linux OS

* The system must have ffmpeg installed, to install type the following command into the terminal:
```shell
$ sudo apt-get install ffmpeg
```

* (Optional) make sure the [UserManual.pdf](./UserManual.pdf) is located in the same directory as the `NameSayer-1.0.jar`.

* You should have database of name recordings. See sections **'Choose Database'** and **'How should my database files be named?'** in the [UserManual.pdf](./UserManual.pdf).

### How to Run
Navigate to the same directory as the `NameSayer-1.0.jar` file and type the following command into the terminal:
```shell
$ java -jar NameSayer-1.0.jar
```
Please refer to the [UserManual.pdf](./UserManual.pdf) for information on how to use the application itself.

## How to build
To build `NameSayer-1.0.jar` you should have the latest version of [Maven](https://maven.apache.org/) installed and be in the same directory as the `pom.xml`.

Enter the following command into the terminal:
```shell
$ mvn package
```
The `NameSayer-1.0.jar` will then be in the `target/` directory.

## Built With
* Java - with the use of the JavaFX and [ControlsFX](http://fxexperience.com/controlsfx/) libraries
* [Maven](https://maven.apache.org/) - for building and dependency management

## License 
This project is licensed under the GNU General Public License - see the [LICENSE](./LICENSE) file for details
