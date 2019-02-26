package com.feb29.springboot.example.benchmark;

import org.openjdk.jmh.annotations.Benchmark;

public class JarLauncherBenchmark
{
    @Benchmark
    public void benchmark(BootJarLauncherState state)
    {
        state.start();
    }
}
