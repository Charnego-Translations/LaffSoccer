package com.ygames.ysoccer.framework.commentary;

/**
 * Decides whether an incoming comment should be queued, and whether it should
 * interrupt (clear) whatever is currently playing/queued.
 */
final class QueuePolicy {

    static final float MAX_QUEUE_SECONDS = 3.0f;

    private QueuePolicy() {
    }

    static boolean shouldEnqueue(Comment incoming, float queueLength, boolean isPlaying) {
        return queueLength < MAX_QUEUE_SECONDS
            || !isPlaying
            || incoming.commentPriority.weight >= CommentPriority.HIGH.weight;
    }

    /**
     * @param queuedNext the first comment of the next queued sentence, or null if the queue is empty
     */
    static boolean shouldClearQueue(Comment incoming, Comment playing, Comment queuedNext, float queueLength) {
        if (playing == null) {
            return false;
        }
        return isHigherPriorityInterruption(incoming, playing, queuedNext)
            || isQueueOverflowClash(incoming, playing, queueLength)
            || isLowPriorityCollision(incoming, playing);
    }

    // A strictly more important comment arrives while both what's playing and what's queued next are less important
    private static boolean isHigherPriorityInterruption(Comment incoming, Comment playing, Comment queuedNext) {
        return incoming.commentPriority.weight > playing.commentPriority.weight
            && queuedNext != null
            && incoming.commentPriority.weight > queuedNext.commentPriority.weight
            && incoming.commentPriority != CommentPriority.CHITCHAT
            && incoming.commentPriority.weight > CommentPriority.HIGH.weight;
    }

    // The queue is backed up and both what's playing and what's arriving are important enough to jump it
    private static boolean isQueueOverflowClash(Comment incoming, Comment playing, float queueLength) {
        return queueLength > MAX_QUEUE_SECONDS
            && incoming.commentPriority.weight >= CommentPriority.HIGH.weight
            && playing.commentPriority.weight >= CommentPriority.HIGH.weight;
    }

    // Two low-priority comments overlapping: prefer the newer one over piling up small talk
    private static boolean isLowPriorityCollision(Comment incoming, Comment playing) {
        return incoming.commentPriority == CommentPriority.LOW
            && playing.commentPriority == CommentPriority.LOW;
    }
}
