# imggaming-test

This is a test task for IMG Arena.
The task is done as console application.
Only third-party framework is used is ScalaTest.

## Building a project
Before building the project please ensure that JDK and SBT are installed.
```
sbt assembly
```
A built artifact will be stored in
```
./target/scala-2.13/imggaming-assembly-0.1.0.jar
```

## Launching the application
Data files are stored in `./Examples` folder. Files are exactly the same as they were sent to me.
The application supports following commands which it will execute consequently as they are parsed from the command line.
By default all output is produced to the console.

### Available commands
* `--streamFrom <file>` reads hexadecimal event data from file located at <filename> path
* `--allEvents`         prints all events
* `--lastEvent`         prints the last event
* `--lastEvents <num>`  prints <num> last
* `--journal`           prints journal of events as they were processed including events which were not accepted.

### Example
```
$ java -cp target/scala-2.13/imggaming-assembly-0.1.0.jar imggaming.Application --streamFrom ./Examples/sample1.txt --allEvents --journal > output.txt  
$ java -cp target/scala-2.13/imggaming-assembly-0.1.0.jar imggaming.Application --streamFrom ./Examples/sample1.txt --lastEvent --streamFrom ./Examples/sample2.txt --lastEvents 5
```



