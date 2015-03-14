# Introduction #

This page describes the guidelines for unit testing.

# Details #

In general all of the classes that are added to the application need to have a corresponding unit test created in the test project that is associated with the application project.

Look to extending ActivityTestCase as the way to build each unit test class. Each unit test class should really only be testing the core functionality of a single class in the actual application.

Raw data that is used as the input for test cases needs to go into the raw folder of the test case. Extending from ActivityTestCase means that access will be available to the raw data in the test project. Not extending from this class will mean that your test will probably load the wrong resource. If you're unlucky the resource ID in the test project will map to a real resource ID in the application under test leading to fun times trying to work out why the test is failing based on the data set.

There should be at least one test case, preferably more for each class being tested. The success path needs to be validated as well as potential corner cases and just plain bad input.