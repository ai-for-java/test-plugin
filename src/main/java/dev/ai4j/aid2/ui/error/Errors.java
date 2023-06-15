package dev.ai4j.aid2.ui.error;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.project.Project;

import static com.intellij.notification.NotificationType.ERROR;

public class Errors {

    private static final NotificationGroup notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup("Error Notification Group");

    public static void showNotification(Throwable t, Project project) {
        notificationGroup.createNotification("Ooops", t.getLocalizedMessage(), ERROR).notify(project);
    }
}
