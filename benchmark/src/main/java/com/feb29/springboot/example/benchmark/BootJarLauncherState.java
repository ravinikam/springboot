package com.feb29.springboot.example.benchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class BootJarLauncherState
{
    private static final String MAIN_CLASS = "org.springframework.boot.loader.JarLauncher";
    private static final String APP_STARTED_PATTERN =
            "(?:.+): (?:Started (?:.+) in (?:\\d+\\.\\d+) seconds \\(JVM running for (?:\\d+\\.\\d+)\\))";

    private Process started;

    private boolean isStarted()
    {
        return Objects.nonNull(started) && started.isAlive();
    }

    void start()
    {
        if (!isStarted())
        {
            final String[] command = getCommand();
            ProcessBuilder pb = new ProcessBuilder(command);
            System.out.println("***** Running Command *****");
            System.out.println(Arrays.asList(command));
            try
            {
                started = pb
                        .redirectErrorStream(true)
                        .start();

                waitUntilFullyStarted(started);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void waitUntilFullyStarted(Process started) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(started.getInputStream()));
            String line;
            while ((line = br.readLine()) != null && !line.matches(APP_STARTED_PATTERN))
            {
                sb.append(line).append(lineSeparator);
            }
            if (line != null)
            {
                sb.append(line).append(lineSeparator);
            }
        }
        finally
        {
            System.out.println(sb.toString());
        }
    }

    void stop()
    {
        if (isStarted())
        {
            try
            {
                started.destroyForcibly().waitFor();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                started = null;
            }
        }
    }

    private String[] getCommand()
    {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = runtimeMxBean.getInputArguments();

        List<String> args = new ArrayList<>(
                Arrays.asList("java", "-cp", runtimeMxBean.getClassPath(), MAIN_CLASS, "--server.port=0"));
        args.addAll(1, jvmArgs);

        return args.toArray(new String[] {});
    }

    @TearDown(Level.Invocation)
    public void tearDown()
    {
        stop();
    }
}
