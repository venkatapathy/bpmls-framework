BPMLS - Business Process Management-based Learning System
==========================================================================

# Description
This project aims to provide a framework for exploiting Business Process
Management Notation and Systems (BPMN, BPMS) in order to support workplace
e-learning. 

The design and the development of this framework are supported by the [LabSEDC at CNR-ISTI](http://labsedc.isti.cnr.it).

# Structure
 * ``backend``: BPMLS Backend Engine and Concepts
 * ``frontend``: BPMLS Frontend and GUI

# Main Dependencies
 * maven
 * git
 * Java 8
 
# Build
First of all, clone the repository.

``git clone https://github.com/venkatapathy/bpmls-framework.git``

Then, once cloned, you can trigger a build with maven from the root directory.

 * ``cd ./bpmls-framework``
 * ``mvn clean install``

# Launch
Enter into the backend part of the project and run with maven:
 * ``cd ./bpmls-framework/backend``
 * ``mvn spring-boot:run``

Now with a browser access to: ``http://localhost:8080``

# Logging

> THIS WILL COME SOON