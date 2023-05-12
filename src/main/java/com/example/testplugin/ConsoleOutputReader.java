package com.example.testplugin;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleOutputReader implements ConsoleFilterProvider {

    @Override
    public Filter[] getDefaultFilters(@NotNull Project project) {
        System.out.println("returned filter");
        return new Filter[]{new Filter() {
            @Override
            public Result applyFilter(String line, int entireLength) {
                System.out.println("run filter with " + line);
                if (line != null && (line.contains("Exception") || line.contains("Error"))) { // TODO case
                    System.out.println("showing notification");
                    // Show a notification when the text appears
                    Notification notification = new Notification(
                            "Custom Notification Group",
                            "Exception",
                            line,
                            NotificationType.INFORMATION
                    );
                    Notifications.Bus.notify(notification);
                }
                return null;
            }
        }};
    }
}
