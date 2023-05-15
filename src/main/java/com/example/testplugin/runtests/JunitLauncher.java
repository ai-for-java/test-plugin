package com.example.testplugin.runtests;

import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class JunitLauncher {

    public static void runJUnitTest(String packageName, String testClassName) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage(packageName),
                        selectClass(testClassName)
                ).build();

        LauncherConfig config = LauncherConfig.builder().enableTestEngineAutoRegistration(false).build();
        Launcher launcher = LauncherFactory.create(config);

        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        System.out.println("Tests succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Tests failed: " + summary.getTestsFailedCount());
        System.out.println("Tests skipped: " + summary.getTestsSkippedCount());
    }

    public static void launch2() {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage("org.example")
//                        selectClass(MyTestClass.class)
                )
//                .filters(
//                        includeClassNamePatterns(".*Tests")
//                )
                .build();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        try {
            Class.forName("org.junit.platform.launcher.LauncherSessionListener");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Launcher launcher = LauncherFactory.create(LauncherConfig.builder()
//                            .enableTestEngineAutoRegistration(true)
                            .addTestEngines(new JupiterTestEngine())
                    .build());
            // Register a listener of your choice
            launcher.registerTestExecutionListeners(listener);
            // Discover tests and build a test plan
            TestPlan testPlan = launcher.discover(request);
            // Execute test plan
            launcher.execute(testPlan);
            // Alternatively, execute the request directly
            launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        System.out.println(summary);
    }
}
