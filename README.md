# Benchmark Demo Application

Simple spring Boot demo application to benchmark spring boot services startup time. 
It uses JMH benchmarking tool similar to [this](https://github.com/dsyer/spring-boot-startup-bench) project.

### How to Build
    - Run ./gradlew clean shadowJar
    
### How to Run
    - Run java -jar benchmark/build/libs/benchmark-0.1.0-SNAPSHOT-all.jar -bm avgt -f 2 -foe true -i 2 -wi 1   
       
### How to use
    - Replace bootiful module with your spring boot module
    - Or copy benchmark module to your multi-module gradle project