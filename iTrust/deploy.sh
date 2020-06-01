#!/bin/bash
mvn -Dmaven.test.skip=true install
echo "Generating test data..."
java -cp "$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)":target/classes:target/test-classes edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator
echo "Done!"

