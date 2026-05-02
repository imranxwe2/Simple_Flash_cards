package application;

import java.time.Duration;
import java.time.LocalDateTime;

public class FormatTimeRemaining { // diff from notion
public static String formatTimeRemaining(String nextReview) {

    if (nextReview == null) return "";

    LocalDateTime next = LocalDateTime.parse(nextReview);
    LocalDateTime now = LocalDateTime.now();

    Duration diff = Duration.between(now, next);

    long hours = diff.toHours();
    long minutes = diff.toMinutes() % 60;

    if (hours <= 0 && minutes <= 0) {
        return "Ready now";
    }

    return hours + "h " + minutes + "m";
}
}