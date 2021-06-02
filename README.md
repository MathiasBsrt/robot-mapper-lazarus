# Lazarus robot project

This robot allow you to map a room with manual control or with an automatic mode.
We use 4 motors wich are connected to 2 outputs motor controller.
One output for each side of the robot.

We use also a Bluetooth controller for the communication.
 
3 ultrasonic sensors, one front and two on the right side.

2 encoders, one on the right back motor and another on the left back motor


We decided to split the work in three module :

- robot
- decision
- HMI

**The HMI needs to be built with Java 14**

You can found each program in the associated folder.
The main code of the robot is in the `main` folder of arduino folder.

## Video
A quick presentation of the project can be founded at that link.
https://youtu.be/nN9yWv9Wkwc
