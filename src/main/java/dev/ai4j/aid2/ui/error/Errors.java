package dev.ai4j.aid2.ui.error;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.project.Project;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.intellij.notification.NotificationType.ERROR;

public class Errors {

    private static final NotificationGroup notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup("Error Notification Group");

    public static void showNotification(Throwable t, Project project) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        notificationGroup.createNotification("Ooops", sw.toString(), ERROR).notify(project);
    }
}
